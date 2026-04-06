package com.Projeto.GeradorDeQuestoes.dto;

import java.util.Map;

public class QuestaoDTO {

    String enunciado;
    Map<String, String> alternativas; 
    String respostaCorreta;
    String explicacao;
    String conceito;
    String competencia;
    String comentarioTecnico;
    String topico;


    public QuestaoDTO(String enunciado, Map<String,String> alternativas, String respostaCorreta, String explicacao, String conceito, String competencia, String comentarioTecnico) {
        this.enunciado = enunciado;
        this.alternativas = alternativas;
        this.respostaCorreta = respostaCorreta;
        this.explicacao = explicacao;
        this.conceito = conceito;
        this.competencia = competencia;
        this.comentarioTecnico = comentarioTecnico;
    }



    public String getEnunciado() {
        return this.enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public Map<String,String> getAlternativas() {
        return this.alternativas;
    }

    public void setAlternativas(Map<String,String> alternativas) {
        this.alternativas = alternativas;
    }

    public String getRespostaCorreta() {
        return this.respostaCorreta;
    }

    public void setRespostaCorreta(String respostaCorreta) {
        this.respostaCorreta = respostaCorreta;
    }

    public String getExplicacao() {
        return this.explicacao;
    }

    public void setExplicacao(String explicacao) {
        this.explicacao = explicacao;
    }

    public String getConceito() {
        return this.conceito;
    }

    public void setConceito(String conceito) {
        this.conceito = conceito;
    }

    public String getCompetencia() {
        return this.competencia;
    }

    public void setCompetencia(String competencia) {
        this.competencia = competencia;
    }

    public String getComentarioTecnico() {
        return this.comentarioTecnico;
    }

    public void setComentarioTecnico(String comentarioTecnico) {
        this.comentarioTecnico = comentarioTecnico;
    }

    public String getTopico() {
        return this.topico;
    }

    public void setTopico(String topico) {
        this.topico = topico;
    }


    
}
