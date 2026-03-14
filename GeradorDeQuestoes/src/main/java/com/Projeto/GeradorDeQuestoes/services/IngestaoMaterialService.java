package com.Projeto.GeradorDeQuestoes.services;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class IngestaoMaterialService {

    private final VectorStore vectorStore;

    public IngestaoMaterialService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void importarCapituloLivroDificil(Resource pdfResource, String topico, String fonte) {
        
        TikaDocumentReader pdfReader = new TikaDocumentReader(pdfResource);
        

        TokenTextSplitter splitter = new TokenTextSplitter(
            1500, 
            400,  
            10,   
            5000, 
            true  
        );

        List<Document> documents = pdfReader.get();
        List<Document> chunks = splitter.apply(documents);

        for (Document chunk : chunks) {
            chunk.getMetadata().put("topico", topico);
            chunk.getMetadata().put("fonte", fonte);
            chunk.getMetadata().put("nivel_material", "universitario_avancado");
        }

        this.vectorStore.accept(chunks);
        System.out.println("Sucesso: " + chunks.size() + " fragmentos técnicos importados.");
    }

    public void importarCapituloLivroMedio(Resource pdfResource, String topico, String fonte) {
        
        TikaDocumentReader pdfReader = new TikaDocumentReader(pdfResource);
        

        TokenTextSplitter splitter = new TokenTextSplitter(
            1500, 
            400,  
            10,   
            5000, 
            true  
        );

        List<Document> documents = pdfReader.get();
        List<Document> chunks = splitter.apply(documents);

        for (Document chunk : chunks) {
            chunk.getMetadata().put("topico", topico);
            chunk.getMetadata().put("fonte", fonte);
            chunk.getMetadata().put("nivel_material", "universitario_intermediario");
        }

        this.vectorStore.accept(chunks);
        System.out.println("Sucesso: " + chunks.size() + " fragmentos técnicos importados.");
    }

    public void importarCapituloLivroFacil(Resource pdfResource, String topico, String fonte) {
        
        TikaDocumentReader pdfReader = new TikaDocumentReader(pdfResource);
        

        TokenTextSplitter splitter = new TokenTextSplitter(
            1500, 
            400,  
            10,   
            5000, 
            true  
        );

        List<Document> documents = pdfReader.get();
        List<Document> chunks = splitter.apply(documents);

        for (Document chunk : chunks) {
            chunk.getMetadata().put("topico", topico);
            chunk.getMetadata().put("fonte", fonte);
            chunk.getMetadata().put("nivel_material", "universitario_iniciante");
        }

        this.vectorStore.accept(chunks);
        System.out.println("Sucesso: " + chunks.size() + " fragmentos técnicos importados.");
    }


}