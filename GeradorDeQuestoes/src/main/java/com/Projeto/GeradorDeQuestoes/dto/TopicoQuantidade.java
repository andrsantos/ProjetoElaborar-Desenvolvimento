package com.Projeto.GeradorDeQuestoes.dto;

public class TopicoQuantidade{

    String topico;
    int quantidade;
    int quantidadeDificeis;
    int quantidadeMedias;
    int quantidadeFaceis;

    public String getTopico() {
        return this.topico;
    }
    public void setTopico(String topico) {
        this.topico = topico;
    }
    public int getQuantidade() {
        return this.quantidade;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getQuantidadeDificeis() {
        return this.quantidadeDificeis;
    }

    public void setQuantidadeDificeis(int quantidadeDificeis) {
        this.quantidadeDificeis = quantidadeDificeis;
    }

    public int getQuantidadeMedias() {
        return this.quantidadeMedias;
    }

    public void setQuantidadeMedias(int quantidadeMedias) {
        this.quantidadeMedias = quantidadeMedias;
    }

    public int getQuantidadeFaceis() {
        return this.quantidadeFaceis;
    }

    public void setQuantidadeFaceis(int quantidadeFaceis) {
        this.quantidadeFaceis = quantidadeFaceis;
    }

}
