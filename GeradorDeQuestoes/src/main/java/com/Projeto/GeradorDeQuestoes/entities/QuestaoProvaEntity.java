package com.Projeto.GeradorDeQuestoes.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.Map;

@Entity
@Table(name = "tb_provas_questoes")
public class QuestaoProvaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prova_id", nullable = false)
    private ProvaEntity prova;

    @Column(name = "enunciado", columnDefinition = "TEXT")
    private String enunciado;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "alternativas", columnDefinition = "jsonb")
    private Map<String, String> alternativas;

    @Column(name = "resposta_correta")
    private String respostaCorreta;

    @Column(name = "explicacao", columnDefinition = "TEXT")
    private String explicacao;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public ProvaEntity getProva() { return prova; }
    public void setProva(ProvaEntity prova) { this.prova = prova; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public Map<String, String> getAlternativas() { return alternativas; }
    public void setAlternativas(Map<String, String> alternativas) { this.alternativas = alternativas; }
    public String getRespostaCorreta() { return respostaCorreta; }
    public void setRespostaCorreta(String respostaCorreta) { this.respostaCorreta = respostaCorreta; }

    public String getExplicacao() {
        return this.explicacao;
    }

    public void setExplicacao(String explicacao) {
        this.explicacao = explicacao;
    }

}