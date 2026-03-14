package com.Projeto.GeradorDeQuestoes.dto;

public record GerarQuestaoRequest(String topico, int quantidade, 
    int quantidadeDificeis, int quantidadeMedias, int quantidadeFaceis) {
}