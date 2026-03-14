package com.Projeto.GeradorDeQuestoes.services.impl;

import com.Projeto.GeradorDeQuestoes.dto.GerarQuestaoRequest;
import com.Projeto.GeradorDeQuestoes.dto.ListaQuestoes;
import com.Projeto.GeradorDeQuestoes.dto.Questao;
import com.Projeto.GeradorDeQuestoes.entities.TopicoConfigEntity;
import com.Projeto.GeradorDeQuestoes.enums.NivelTecnico;
import com.Projeto.GeradorDeQuestoes.repositories.TopicoConfigRepository;
import com.Projeto.GeradorDeQuestoes.services.GeradorQuestaoService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class GeradorQuestaoServiceImpl implements GeradorQuestaoService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final TopicoConfigRepository topicoConfigRepository;

    public GeradorQuestaoServiceImpl(ChatClient.Builder chatClientBuilder,
                                     VectorStore vectorStore,
                                     TopicoConfigRepository configRepository) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
        this.topicoConfigRepository = configRepository;
    }

    @Override
    public ListaQuestoes gerarQuestoes(GerarQuestaoRequest request) {
        List<Questao> todasAsQuestoes = new ArrayList<>();

        if (request.quantidadeFaceis() > 0) {
            todasAsQuestoes.addAll(gerarBlocoPorNivel(request.topico(), "FACIL", request.quantidadeFaceis()));
        }
        if (request.quantidadeMedias() > 0) {
            todasAsQuestoes.addAll(gerarBlocoPorNivel(request.topico(), "MEDIO", request.quantidadeMedias()));
        }
        if (request.quantidadeDificeis() > 0) {
            todasAsQuestoes.addAll(gerarBlocoPorNivel(request.topico(), "DIFICIL", request.quantidadeDificeis()));
        }

        return new ListaQuestoes(todasAsQuestoes);
    }

    private List<Questao> gerarBlocoPorNivel(String nomeTopico, String nivel, int quantidadeSolicitada) {
        List<Questao> blocoFinal = new ArrayList<>();
        List<String> historicoEnunciados = new ArrayList<>();

        TopicoConfigEntity config = topicoConfigRepository.findByTopicoAndNivel(nomeTopico, nivel)
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada para " + nivel));

        String contextoTecnico = recuperarContextoDoBanco(nomeTopico, nivel);

        int quantidadeParaExtrair = quantidadeSolicitada * 3;
        List<String> todosOsConceitos = chamarAgenteExtrator(contextoTecnico, nivel, quantidadeParaExtrair);

        Collections.shuffle(todosOsConceitos);
        List<String> conceitosSorteados = todosOsConceitos.stream()
                .limit(quantidadeSolicitada)
                .collect(Collectors.toList());

        for (String conceitoAtual : conceitosSorteados) {
            Questao questaoFinal = null;
            int tentativas = 0;

            while (questaoFinal == null && tentativas < 5) {
                try {
                    String rascunhoRaw = chamarAgenteEscritor(nomeTopico, nivel, contextoTecnico, historicoEnunciados, config, conceitoAtual);
                    List<Questao> listaRascunho = parsearRespostaTags(rascunhoRaw);

                    if (listaRascunho.isEmpty()) {
                        tentativas++;
                        continue;
                    }
                    Questao rascunho = listaRascunho.get(0);

                    String solucaoRaw = chamarAgenteSolucionador(rascunho.getEnunciado(), rascunho.getAlternativas(), contextoTecnico);
                    String respostaResolvida = extrairLetraDaResposta(solucaoRaw);

                    System.out.println("AUDITORIA [" + conceitoAtual + "]: Escritor=" + rascunho.getRespostaCorreta().toUpperCase() + " | Solucionador=" + respostaResolvida);

                    String feedbackVindoDaSolucao = "";
                    if (!respostaResolvida.equalsIgnoreCase(rascunho.getRespostaCorreta())) {
                        feedbackVindoDaSolucao = "[CONFLITO TÉCNICO] O auditor divergiu do autor. Raciocínio do auditor: " + solucaoRaw;
                    }

                    String feedbackCritico = chamarAgenteCritico(rascunho, nivel, historicoEnunciados);

                    String feedbackTotal = feedbackCritico + " " + feedbackVindoDaSolucao;

                    if (feedbackCritico.contains("[APROVADA]") && feedbackVindoDaSolucao.isEmpty()) {
                        questaoFinal = rascunho;
                    } else {
                        System.out.println("Refinando questão [" + conceitoAtual + "]. Motivo: " + feedbackTotal);
                        String refinadaRaw = chamarAgenteRefinador(rascunho, feedbackTotal, contextoTecnico, nivel);

                        List<Questao> listaRefinada = parsearRespostaTags(refinadaRaw);
                        if (!listaRefinada.isEmpty()) {
                            questaoFinal = listaRefinada.get(0);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Falha na orquestração: " + e.getMessage());
                }
                tentativas++;
            }

            if (questaoFinal != null) {
                blocoFinal.add(questaoFinal);
                historicoEnunciados.add(questaoFinal.getEnunciado());
            }

            if (blocoFinal.size() >= quantidadeSolicitada) break;
        }
        return blocoFinal;
    }

    private String chamarAgenteSolucionador(String enunciado, Map<String, String> alternativas, String contexto) {
        String alternativasFormatadas = alternativas.entrySet().stream()
                .map(e -> e.getKey().toUpperCase() + ") " + e.getValue())
                .collect(Collectors.joining("\n"));

        String prompt = """
            Você é um Professor de Redes especialista em auditoria de provas. Sua tarefa é resolver a questão abaixo de forma independente.
            
            ### MATERIAL TÉCNICO DE REFERÊNCIA ###
            %s
            
            ### QUESTÃO PARA RESOLVER ###
            ENUNCIADO: %s
            
            ALTERNATIVAS:
            %s
            
            ### TAREFA ###
            1. Analise o enunciado e as alternativas sem conhecer o gabarito.
            2. Aplique os conceitos técnicos do material de referência passo a passo.
            3. Identifique a ÚNICA alternativa correta.
            
            Responda obrigatoriamente neste formato:
            RACIOCÍNIO: [seu passo a passo lógico]
            RESPOSTA: [Letra]
            """.formatted(contexto, enunciado, alternativasFormatadas);

        return this.chatClient.prompt(prompt)
                .options(ChatOptions.builder().temperature(0.1).build()) 
                .call().content();
    }

    private List<Questao> parsearRespostaTags(String rawText) {
        List<Questao> questoes = new ArrayList<>();

        Pattern patternQuestao = Pattern.compile("(?si)\\[QUESTAO\\](.*?)(?:\\[/EXPLICACAO\\]|\\[/QUESTA[OÕÃ]\\]|$)");
        Matcher matcherQuestao = patternQuestao.matcher(rawText);

        while (matcherQuestao.find()) {
            String blocoLimpo = matcherQuestao.group(1).trim();

            if (!blocoLimpo.toUpperCase().contains("EXPLICACAO")) {
                blocoLimpo = rawText.substring(matcherQuestao.start());
            }

            if (blocoLimpo.toUpperCase().contains("ENUNCIADO")) {
                try {
                    Map<String, String> alternativas = new HashMap<>();
                    alternativas.put("a", extrairValorNinja(blocoLimpo, "A"));
                    alternativas.put("b", extrairValorNinja(blocoLimpo, "B"));
                    alternativas.put("c", extrairValorNinja(blocoLimpo, "C"));
                    alternativas.put("d", extrairValorNinja(blocoLimpo, "D"));
                    alternativas.put("e", extrairValorNinja(blocoLimpo, "E"));

                    String enunciado = extrairValorNinja(blocoLimpo, "ENUNCIADO");
                    String explicacao = extrairValorNinja(blocoLimpo, "EXPLICACAO");

                    String respostaBruta = extrairValorNinja(blocoLimpo, "RESPOSTA");
                    String resposta = "";
                    Matcher m = Pattern.compile("(?i)([a-e])").matcher(respostaBruta);
                    if (m.find()) {
                        resposta = m.group(1).toLowerCase();
                    }

                    boolean alternativasPreenchidas = alternativas.values().stream().allMatch(v -> v != null && !v.isBlank());
                    boolean enunciadoFgv = enunciado.split("\\s+").length >= 20; 
                    boolean temExplicacao = explicacao.length() > 10;

                    if (enunciadoFgv && alternativasPreenchidas && !resposta.isEmpty() && temExplicacao) {
                        questoes.add(new Questao(UUID.randomUUID().toString(), enunciado, alternativas, resposta, explicacao));
                    } else {
                        System.err.println("Parser: Questão ignorada por falta de robustez (Enunciado curto ou alternativas vazias).");
                    }
                } catch (Exception e) {
                    System.err.println("Erro no parser de bloco: " + e.getMessage());
                }
            }
        }
        return questoes;
    }

    private String recuperarContextoDoBanco(String nomeTopico, String nivel) {
        String nivelMaterial = switch (nivel.toUpperCase()) {
            case "FACIL" -> NivelTecnico.UNIVERSITARIO_INICIANTE.name().toLowerCase();
            case "MEDIO" -> NivelTecnico.UNIVERSITARIO_INTERMEDIARIO.name().toLowerCase();
            case "DIFICIL" -> NivelTecnico.UNIVERSITARIO_AVANCADO.name().toLowerCase();
            default -> "universitario_iniciante";
        };

        SearchRequest searchRequest = SearchRequest.builder()
                .query(nomeTopico)
                .topK(4) 
                .filterExpression(new FilterExpressionBuilder()
                        .eq("nivel_material", nivelMaterial)
                        .build())
                .build();

        String contextoEncontrado = this.vectorStore.similaritySearch(searchRequest).stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

        if (contextoEncontrado.length() > 8000) {
            contextoEncontrado = contextoEncontrado.substring(0, 8000) + "... [Truncado]";
        }

        if (contextoEncontrado.isBlank()) {
            return "Use o conhecimento técnico consolidado sobre " + nomeTopico + " (Referência: Kurose/Tanenbaum).";
        }

        return contextoEncontrado;
    }


    private List<String> chamarAgenteExtrator(String contexto, String nivel, int quantidade) {
        String promptExtrator = """
            Você é um Analista de Conteúdo. Extraia exatamente %d conceitos técnicos de nível %s do material:
            %s
            Retorne os conceitos separados por vírgula, sem numeração.
            """.formatted(quantidade, nivel, contexto);
        try {
            String respostaRaw = this.chatClient.prompt(promptExtrator)
                    .options(ChatOptions.builder().temperature(0.5).build())
                    .call().content();
            return Arrays.stream(respostaRaw.split("[,\\n]"))
                    .map(String::trim)
                    .filter(s -> s.length() > 3)
                    .distinct()
                    .limit(quantidade)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of("TCP", "UDP", "Camada de Transporte");
        }
    }

    private String chamarAgenteCritico(Questao questao, String nivel, List<String> historico) {
        String historicoFormatado = historico.isEmpty() ? "Vazio" : String.join("\n- ", historico);
        String prompt = """
            Como revisor técnico, avalie a questão abaixo (Nível %s).
            Enunciado: %s
            Gabarito: %s
            
            Se a questão for inédita e tecnicamente correta, responda: [APROVADA].
            Caso contrário: [REPROVADA] - Motivo.
            """.formatted(nivel, questao.getEnunciado(), questao.getRespostaCorreta());

        return this.chatClient.prompt(prompt)
                .options(ChatOptions.builder().temperature(0.2).build())
                .call().content();
    }

    private String chamarAgenteEscritor(String nomeTopico, String nivel, String contexto, List<String> historico, TopicoConfigEntity config, String conceitoAtual) {
        PromptTemplate template = new PromptTemplate(config.getInstrucoesEspecificas());
        Map<String, Object> params = new HashMap<>();
        params.put("nivel", nivel);
        params.put("topico", nomeTopico);
        params.put("contexto", contexto);
        params.put("conceitos", conceitoAtual);
        
        return this.chatClient.prompt(template.render(params))
                .options(ChatOptions.builder().temperature(0.8).build())
                .call().content();
    }

    private String chamarAgenteRefinador(Questao questaoRuim, String feedback, String contexto, String nivel) {
        String prompt = "Conserte esta questão seguindo o feedback: " + feedback + "\nQuestão original: " + questaoRuim.getEnunciado();
        return this.chatClient.prompt(prompt)
                .options(ChatOptions.builder().temperature(0.4).build())
                .call().content();
    }

    private String extrairValorNinja(String bloco, String tag) {
        try {
            String regex = "(?mi)^\\s*" + tag + "\\s*[\\s:;.-]+(.*?)(?=\\s*^(?:ENUNCIADO|A|B|C|D|E|RESPOSTA|EXPLICACAO):|\\s*\\[/|\\s*$)";
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(bloco);
            if (matcher.find()) return matcher.group(1).trim();
        } catch (Exception e) { }
        return "";
    }

    private String extrairLetraDaResposta(String solucaoRaw) {
        if (solucaoRaw == null) return "";
        Pattern p = Pattern.compile("(?i)RESPOSTA\\s*[:\\-]*\\s*([a-e])");
        Matcher m = p.matcher(solucaoRaw);
        if (m.find()) return m.group(1).toUpperCase();
        
        Pattern p2 = Pattern.compile("(?i)\\b([a-e])\\b");
        Matcher m2 = p2.matcher(solucaoRaw);
        String last = "";
        while (m2.find()) last = m2.group(1).toUpperCase();
        return last;
    }
}