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
}