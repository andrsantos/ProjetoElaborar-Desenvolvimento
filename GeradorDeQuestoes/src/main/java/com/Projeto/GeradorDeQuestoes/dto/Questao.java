package com.Projeto.GeradorDeQuestoes.dto;
import java.util.Map;

public class Questao

{
    String id;
    String enunciado;
    Map<String, String> alternativas; 
    String respostaCorreta;
    String explicacao;


    public Questao(String id, String enunciado, Map<String,String> alternativas, String respostaCorreta, String explicacao) {
        this.id = id;
        this.enunciado = enunciado;
        this.alternativas = alternativas;
        this.respostaCorreta = respostaCorreta;
        this.explicacao = explicacao;
    }
    

    public String getExplicacao() {
        return this.explicacao;
    }

    public void setExplicacao(String explicacao) {
        this.explicacao = explicacao;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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


}