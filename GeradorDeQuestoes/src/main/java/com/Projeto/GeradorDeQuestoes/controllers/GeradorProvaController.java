package com.Projeto.GeradorDeQuestoes.controllers;

import com.Projeto.GeradorDeQuestoes.dto.GeracaoAutomaticaRequest;
import com.Projeto.GeradorDeQuestoes.dto.GerarQuestaoRequest;
import com.Projeto.GeradorDeQuestoes.dto.Prova;
import com.Projeto.GeradorDeQuestoes.dto.Questao;
import com.Projeto.GeradorDeQuestoes.services.GeradorProvaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/provas")
@CrossOrigin(origins = "http://localhost:4200")
public class GeradorProvaController {

    private final GeradorProvaService provaService;

    public GeradorProvaController(GeradorProvaService provaService) {
        this.provaService = provaService;
    }


    @PostMapping
    public ResponseEntity<Prova> criarProva() {
        Prova prova = provaService.criarNovaProva();
        return ResponseEntity.ok(prova); 
    }

 
    @GetMapping("/{id}")
    public ResponseEntity<Prova> getProva(@PathVariable UUID id) {
        Prova prova = provaService.getProva(id);
        if (prova == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(prova);
    }

    @PostMapping("/{id}/questoes")
    public ResponseEntity<Prova> adicionarQuestoes(
            @PathVariable UUID id, 
            @RequestBody GerarQuestaoRequest request) {
        
        try {
            Prova prova = provaService.adicionarQuestoes(id, request);
            return ResponseEntity.ok(prova);
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); 
        }
    }

    @PostMapping("/{id}/questoes-automaticas")
    public ResponseEntity<Prova> adicionarQuestoesAutomatico(
            @PathVariable UUID id, 
            @RequestBody GeracaoAutomaticaRequest request) {
        
        try {
            Prova prova = provaService.adicionarQuestoesAutomatico(id, request);
            return ResponseEntity.ok(prova);
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); 
        }
    }
    
    @PostMapping("/{id}/manual")
    public ResponseEntity<Prova> salvarProvaManual(
            @PathVariable UUID id, 
            @RequestBody List<Questao> questoes) { 
        
        try {
            Prova prova = provaService.adicionarQuestoesManuais(id, questoes);
            return ResponseEntity.ok(prova);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}/questoes")
    public ResponseEntity<Prova> descartarQuestao(
            @PathVariable UUID id, 
            @RequestParam int indice) {
        
        try {
            Prova prova = provaService.descartarQuestao(id, indice);
            return ResponseEntity.ok(prova); 
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/finalizar-pdf")
    public ResponseEntity<byte[]> finalizarEBaixarPdf(@PathVariable UUID id) {
        try {
            byte[] pdfBytes = provaService.finalizarEGerarPdf(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "prova_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}