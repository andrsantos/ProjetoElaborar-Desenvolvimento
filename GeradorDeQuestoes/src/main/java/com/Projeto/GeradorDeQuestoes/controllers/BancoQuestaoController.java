package com.Projeto.GeradorDeQuestoes.controllers;

import com.Projeto.GeradorDeQuestoes.entities.BancoQuestaoEntity;
import com.Projeto.GeradorDeQuestoes.repositories.BancoQuestaoRepository;

import java.util.List;
import java.util.UUID;

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
    @GetMapping
    public ResponseEntity<List<BancoQuestaoEntity>> listarTodas() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BancoQuestaoEntity> buscarPorId(@PathVariable UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BancoQuestaoEntity> atualizarQuestao(@PathVariable UUID id, @RequestBody BancoQuestaoEntity questaoAtualizada) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        questaoAtualizada.setId(id);
        BancoQuestaoEntity salva = repository.save(questaoAtualizada);
        return ResponseEntity.ok(salva);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirQuestao(@PathVariable UUID id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}