package com.Projeto.GeradorDeQuestoes.dto;
import java.util.Map;
import java.util.UUID;

public record Questao(
    UUID id,
    String enunciado,
    Map<String, String> alternativas, 
    String respostaCorreta 
) {
}