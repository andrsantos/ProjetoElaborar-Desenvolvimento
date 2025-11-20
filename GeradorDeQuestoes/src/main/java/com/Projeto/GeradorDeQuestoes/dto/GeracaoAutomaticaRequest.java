package com.Projeto.GeradorDeQuestoes.dto;

import java.util.List;
public class GeracaoAutomaticaRequest{
    List<TopicoQuantidade> topicos;


    public List<TopicoQuantidade> getTopicos() {
        return this.topicos;
    }

    public void setTopicos(List<TopicoQuantidade> topicos) {
        this.topicos = topicos;
    }



}