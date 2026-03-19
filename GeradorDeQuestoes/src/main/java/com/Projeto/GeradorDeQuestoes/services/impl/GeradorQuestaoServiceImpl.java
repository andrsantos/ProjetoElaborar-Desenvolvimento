package com.Projeto.GeradorDeQuestoes.services.impl;

import com.Projeto.GeradorDeQuestoes.dto.GerarQuestaoRequest;
import com.Projeto.GeradorDeQuestoes.dto.ListaQuestoes;
import com.Projeto.GeradorDeQuestoes.dto.Questao;
import com.Projeto.GeradorDeQuestoes.entities.CenarioConfigEntity;
import com.Projeto.GeradorDeQuestoes.entities.TopicoConfigEntity;
import com.Projeto.GeradorDeQuestoes.repositories.CenarioConfigRepository;
import com.Projeto.GeradorDeQuestoes.repositories.TopicoConfigRepository;
import com.Projeto.GeradorDeQuestoes.services.GeradorQuestaoService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
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
    private final CenarioConfigRepository cenarioConfigRepository;

    public GeradorQuestaoServiceImpl(ChatClient.Builder chatClientBuilder,
                                     VectorStore vectorStore,
                                     TopicoConfigRepository configRepository,
                                     CenarioConfigRepository cenarioConfigRepository) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
        this.topicoConfigRepository = configRepository;
        this.cenarioConfigRepository = cenarioConfigRepository;
    }

    @Override
    public ListaQuestoes gerarQuestoes(GerarQuestaoRequest request) {
        List<Questao> todasAsQuestoes = new ArrayList<>();
        if (request.quantidadeFaceis() > 0) todasAsQuestoes.addAll(gerarBlocoPorNivel(request.topico(), "FACIL", request.quantidadeFaceis()));
        if (request.quantidadeMedias() > 0) todasAsQuestoes.addAll(gerarBlocoPorNivel(request.topico(), "MEDIO", request.quantidadeMedias()));
        if (request.quantidadeDificeis() > 0) todasAsQuestoes.addAll(gerarBlocoPorNivel(request.topico(), "DIFICIL", request.quantidadeDificeis()));
        return new ListaQuestoes(todasAsQuestoes);
    }

    private List<Questao> gerarBlocoPorNivel(String nomeTopico, String nivel, int quantidadeSolicitada) {
        List<Questao> blocoFinal = new ArrayList<>();
        Random random = new Random();
        TopicoConfigEntity config = topicoConfigRepository.findByTopicoAndNivel(nomeTopico, nivel)
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada"));

        String contextoTecnico = recuperarContextoDoBanco(nomeTopico, nivel);
        
        List<String> conceitosParaUsar = extrairConceitosUnicos(contextoTecnico, nivel, 15);
        System.out.println("DEBUG: Conceitos únicos detectados: " + conceitosParaUsar.size());

        List<String> listaContextos = cenarioConfigRepository.findByTopicoAndNivel(nomeTopico, nivel).stream()
                .map(CenarioConfigEntity::getDescricao).collect(Collectors.toList());
        if (listaContextos.isEmpty()) listaContextos = List.of("Cenário de rede corporativa");

        int tentativaConceito = 0;

    while (blocoFinal.size() < quantidadeSolicitada && tentativaConceito < conceitosParaUsar.size()) {
        String conceitoAtual = conceitosParaUsar.get(tentativaConceito);
        Questao questaoFinal = null;
        int subTentativas = 0;
        String cenarioSorteado = listaContextos.get(random.nextInt(listaContextos.size()));

        while (questaoFinal == null && subTentativas < 2) { 
            try {
                String enunciadoRaw = chamarAgenteEscritor(nomeTopico, nivel, contextoTecnico, config, conceitoAtual, cenarioSorteado);
                System.out.println("DEBUG: Enunciado gerado: " + enunciadoRaw);
                String questaoCompletaRaw = chamarAgenteAlternativas(enunciadoRaw, contextoTecnico, conceitoAtual, nivel);
                System.out.println("DEBUG: Questão completa gerada: " + questaoCompletaRaw);
                List<Questao> listaRascunho = parsearRespostaTags(questaoCompletaRaw);
                
                if (!listaRascunho.isEmpty()) {
                    questaoFinal = listaRascunho.get(0);
                }
            } catch (Exception e) { 
                System.err.println("Erro na geração [" + conceitoAtual + "]: " + e.getMessage()); 
            }
            subTentativas++;
        }

        if (questaoFinal != null) {
            blocoFinal.add(questaoFinal);
            System.out.println("Progresso: " + blocoFinal.size() + "/" + quantidadeSolicitada);
        }
        
        if (blocoFinal.size() >= quantidadeSolicitada) break;

        tentativaConceito++;
    }
        return blocoFinal;
    }

    private List<String> extrairConceitosUnicos(String contexto, String nivel, int qtd) {
        String prompt = "Liste exatamente %d conceitos técnicos distintos (ex: Protocolo, Atraso de Fila) de %s baseados no material: %s. Separe os itens obrigatoriamente por VÍRGULA.".formatted(qtd, nivel, contexto);
        String r = this.chatClient.prompt(prompt).options(ChatOptions.builder().temperature(0.7).build()).call().content();
        
        String[] partes = r.split(",|\\n|\\r|\\d+\\.");
        
        return Arrays.stream(partes)
                .map(String::trim)
                .filter(s -> s.length() > 3 && s.length() < 60) 
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Questao> parsearRespostaTags(String rawText) {
        List<Questao> questoes = new ArrayList<>();
        Pattern patternQuestao = Pattern.compile("(?si)\\[QUESTAO\\](.*?)(?:\\[/QUESTA[OÕÃ]\\]|$)");
        Matcher matcherQuestao = patternQuestao.matcher(rawText);

        while (matcherQuestao.find()) {
            String bloco = matcherQuestao.group(1).trim();
            try {
                String enun = extrairValorNinja(bloco, "ENUNCIADO");
                Map<String, String> alts = new HashMap<>();
                alts.put("a", extrairValorNinja(bloco, "ALT_1"));
                alts.put("b", extrairValorNinja(bloco, "ALT_2"));
                alts.put("c", extrairValorNinja(bloco, "ALT_3"));
                alts.put("d", extrairValorNinja(bloco, "ALT_4"));
                alts.put("e", extrairValorNinja(bloco, "ALT_5"));

                String respRaw = extrairValorNinja(bloco, "RESPOSTA");
                String resp = mapResposta(respRaw);

                if (!enun.isEmpty() && !resp.isEmpty() && !alts.containsValue("")) {
                    questoes.add(new Questao(UUID.randomUUID().toString(), enun, alts, resp, extrairValorNinja(bloco, "EXPLICACAO")));
                }
            } catch (Exception e) { System.err.println("Erro Parse: " + e.getMessage()); }
        }
        return questoes;
    }

    private String mapResposta(String raw) {
        if (raw.contains("1")) return "a";
        if (raw.contains("2")) return "b";
        if (raw.contains("3")) return "c";
        if (raw.contains("4")) return "d";
        if (raw.contains("5")) return "e";
        Matcher m = Pattern.compile("(?i)([a-e])").matcher(raw);
        return m.find() ? m.group(1).toLowerCase() : "";
    }

    private String extrairValorNinja(String bloco, String tag) {
        String regex = "(?mi)^\\s*" + tag + "\\s*[:;\\)\\.\\-]*\\s*(.*?)(?=\\s*^(?:ENUNCIADO|ALT_1|ALT_2|ALT_3|ALT_4|ALT_5|RESPOSTA|EXPLICACAO)[:;\\)\\.\\-]|\\s*\\[/|\\s*$)";
        Matcher m = Pattern.compile(regex, Pattern.DOTALL).matcher(bloco);
        return m.find() ? m.group(1).trim() : "";
    }

    private String chamarAgenteEscritor(String topico, String nivel, String contexto, TopicoConfigEntity config, String conceito, String cenario) {
        PromptTemplate template = new PromptTemplate(config.getInstrucoesEspecificas());
        Map<String, Object> params = Map.of(
            "nivel", nivel, 
            "topico", topico, 
            "contexto", contexto, 
            "conceitos", conceito, 
            "cenario", cenario
        );

        String reforçoEnunciado = "\n\nFOCO: Gere APENAS o enunciado da questão dentro da tag [ENUNCIADO]...[/ENUNCIADO]. " +
                                "O texto deve ser denso (+100 palavras), técnico e baseado no cenário fornecido. " +
                                "Não gere alternativas ou respostas agora.";

        return this.chatClient.prompt(template.render(params) + reforçoEnunciado)
                .options(ChatOptions.builder().temperature(0.8).build()) 
                .call()
                .content();
    }


    private String chamarAgenteAlternativas(String enunciadoGerado, String contextoTecnico, String conceito, String nivel) {
        String prompt = """
                Sua missão é gerar as alternativas para um enunciado de prova.
                ATENÇÃO: Gere APENAS UMA ÚNICA ESTRUTURA [QUESTAO]...[/QUESTAO]. É proibido gerar variações ou múltiplas questões.
                
                CONTEXTO: %s
                CONCEITO: %s
                
                ENUNCIADO:
                %s
                
                DIRETRIZES:
                - Crie 5 alternativas (ALT_1 a ALT_5).
                - Use entre 15 e 30 palavras por alternativa (seja técnico e claro).
                - Indique a RESPOSTA (1 a 5).

                DIRETRIZES DE REFINAMENTO:
                1. EQUILÍBRIO DE TEXTO: Cada alternativa deve ter, obrigatoriamente, entre 25 e 40 palavras.
                2. DENSIDADE TÉCNICA: Em vez de frases genéricas, use argumentos técnicos. Trnasforme frases simples em frases mais longas e conceituadas.
                Exemplo ruim: "Adotar TCP garante confiabilidade."
                Exemplo bom: "A transição para o protocolo TCP introduz mecanismos de controle de fluxo e retransmissão seletiva, garantindo que segmentos perdidos em rajadas de tráfego sejam recuperados, embora introduza um overhead de cabeçalho."
                3. DISTRATORES OBSCUROS: Crie alternativas que pareçam corretas para quem não domina o conceito, mas claramente erradas para quem domina o conceito. O objetivo é criar armadilhas para induzir o erro de candidatos que não dominam o assunto.
                
                FORMATO:
                [QUESTAO]
                ENUNCIADO: [REPITA O ENUNCIADO RECEBIDO AQUI]
                ALT_1: ...
                ALT_2: ...
                ALT_3: ...
                ALT_4: ...
                ALT_5: ...
                RESPOSTA: [Número]
                EXPLICACAO: ...
                [/QUESTAO]
                """.formatted(contextoTecnico, conceito, enunciadoGerado);

        return this.chatClient.prompt(prompt)
                .options(ChatOptions.builder().temperature(0.4).build())
                .call()
                .content();
    }

    private String recuperarContextoDoBanco(String t, String n) {
        SearchRequest sr = SearchRequest.builder().query(t).topK(6).build();
        return this.vectorStore.similaritySearch(sr).stream().map(Document::getText).collect(Collectors.joining("\n"));
    }
}