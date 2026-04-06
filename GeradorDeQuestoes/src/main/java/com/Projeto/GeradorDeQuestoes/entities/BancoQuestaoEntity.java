package com.Projeto.GeradorDeQuestoes.entities;

import com.Projeto.GeradorDeQuestoes.enums.TipoQuestao;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "tb_banco_questoes")
public class BancoQuestaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String topico; 

    @Column(columnDefinition = "TEXT", nullable = false)
    private String enunciado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoQuestao tipo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> alternativas;

    @Column(name = "resposta_correta", columnDefinition = "TEXT")
    private String respostaCorreta;

    @Column(name = "conceito", columnDefinition = "TEXT")
    private String conceito;

    @Column(name = "comentario_tecnico", columnDefinition = "TEXT")
    private String comentarioTecnico;
    
    @Column(name = "competencia", columnDefinition = "TEXT")
    private String competencia;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTopico() { return topico; }
    public void setTopico(String topico) { this.topico = topico; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public TipoQuestao getTipo() { return tipo; }
    public void setTipo(TipoQuestao tipo) { this.tipo = tipo; }
    public Map<String, String> getAlternativas() { return alternativas; }
    public void setAlternativas(Map<String, String> alternativas) { this.alternativas = alternativas; }
    public String getRespostaCorreta() { return respostaCorreta; }
    public void setRespostaCorreta(String respostaCorreta) { this.respostaCorreta = respostaCorreta; }

    public String getConceito() {
        return this.conceito;
    }

    public void setConceito(String conceito) {
        this.conceito = conceito;
    }

    public String getComentarioTecnico() {
        return this.comentarioTecnico;
    }

    public void setComentarioTecnico(String comentarioTecnico) {
        this.comentarioTecnico = comentarioTecnico;
    }

    public String getCompetencia() {
        return this.competencia;
    }

    public void setCompetencia(String competencia) {
        this.competencia = competencia;
    }


}