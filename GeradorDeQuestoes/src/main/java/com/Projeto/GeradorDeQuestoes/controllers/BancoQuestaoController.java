package com.Projeto.GeradorDeQuestoes.controllers;

import com.Projeto.GeradorDeQuestoes.entities.BancoQuestaoEntity;
import com.Projeto.GeradorDeQuestoes.repositories.BancoQuestaoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/banco-questoes")
@CrossOrigin(origins = "http://localhost:4200")
public class BancoQuestaoController {

    private final BancoQuestaoRepository repository;

    public BancoQuestaoController(BancoQuestaoRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<BancoQuestaoEntity> criarQuestao(@RequestBody BancoQuestaoEntity questao) {
        BancoQuestaoEntity salva = repository.save(questao);
        return ResponseEntity.ok(salva);
    }
}