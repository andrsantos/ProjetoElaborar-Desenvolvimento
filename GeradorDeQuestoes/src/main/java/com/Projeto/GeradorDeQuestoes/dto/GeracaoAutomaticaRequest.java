package com.Projeto.GeradorDeQuestoes.dto;

import java.util.List;
public record GeracaoAutomaticaRequest(
    List<TopicoQuantidade> topicos 
) {
}