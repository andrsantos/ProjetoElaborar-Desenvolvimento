package com.Projeto.GeradorDeQuestoes.controllers;

import com.Projeto.GeradorDeQuestoes.repositories.VectorStoreRepository; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/topicos")
@CrossOrigin(origins = "*")
public class TopicoController {

    private final VectorStoreRepository vectorStoreRepository;

    public TopicoController(VectorStoreRepository vectorStoreRepository) {
        this.vectorStoreRepository = vectorStoreRepository;
    }

    @GetMapping
    public ResponseEntity<List<String>> getTopicosDisponiveis() {
        List<String> topicos = vectorStoreRepository.findDistinctTopicos();
        return ResponseEntity.ok(topicos);
    }
}
