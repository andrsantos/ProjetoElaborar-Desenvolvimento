package com.Projeto.GeradorDeQuestoes.controllers;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Projeto.GeradorDeQuestoes.services.IngestaoMaterialService;

@RestController
@RequestMapping("/api/admin/material")
@CrossOrigin(origins = "*") 
public class IngestaoController {

    private final IngestaoMaterialService ingestaoService;

    public IngestaoController(IngestaoMaterialService ingestaoService) {
        this.ingestaoService = ingestaoService;
    }

    @PostMapping("/upload/dificil")
    public ResponseEntity<String> uploadMaterialDificil(
            @RequestParam("file") MultipartFile file,
            @RequestParam("topico") String topico,
            @RequestParam("fonte") String fonte) {
        
        try {
            Resource pdfResource = file.getResource();
            
            ingestaoService.importarCapituloLivroDificil(pdfResource, topico, fonte);
            
            return ResponseEntity.ok("Material processado e indexado com sucesso no PGVector!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar PDF: " + e.getMessage());
        }
    }

    @PostMapping("/upload/medio")
    public ResponseEntity<String> uploadMaterialMedio(
            @RequestParam("file") MultipartFile file,
            @RequestParam("topico") String topico,
            @RequestParam("fonte") String fonte) {
        
        try {
            Resource pdfResource = file.getResource();
            
            ingestaoService.importarCapituloLivroMedio(pdfResource, topico, fonte);
            
            return ResponseEntity.ok("Material processado e indexado com sucesso no PGVector!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar PDF: " + e.getMessage());
        }
    }

    @PostMapping("/upload/facil")
    public ResponseEntity<String> uploadMaterialFacil(
            @RequestParam("file") MultipartFile file,
            @RequestParam("topico") String topico,
            @RequestParam("fonte") String fonte) {
        
        try {
            Resource pdfResource = file.getResource();
            
            ingestaoService.importarCapituloLivroFacil(pdfResource, topico, fonte);
            
            return ResponseEntity.ok("Material processado e indexado com sucesso no PGVector!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar PDF: " + e.getMessage());
        }
    }


}