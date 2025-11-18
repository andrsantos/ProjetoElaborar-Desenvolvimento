package com.Projeto.GeradorDeQuestoes.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "vector_store") 
public class VectorStoreEntity {
    @Id
    private UUID id;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; 
    
    public String getMetadata() { return metadata; }
}