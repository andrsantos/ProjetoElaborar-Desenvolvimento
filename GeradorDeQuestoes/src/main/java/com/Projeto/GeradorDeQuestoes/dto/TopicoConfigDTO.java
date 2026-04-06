package com.Projeto.GeradorDeQuestoes.dto;

public class TopicoConfigDTO {

    private String id;
    private String topico;
    private String nivel;
    private String instrucoesEspecificas;


    public TopicoConfigDTO() {
    }

    public TopicoConfigDTO(String topico, String nivel, String instrucoesEspecificas) {
        this.topico = topico;
        this.nivel = nivel;
        this.instrucoesEspecificas = instrucoesEspecificas;
    }



    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopico() {
        return this.topico;
    }

    public void setTopico(String topico) {
        this.topico = topico;
    }

    public String getNivel() {
        return this.nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getInstrucoesEspecificas() {
        return this.instrucoesEspecificas;
    }

    public void setInstrucoesEspecificas(String instrucoesEspecificas) {
        this.instrucoesEspecificas = instrucoesEspecificas;
    }

    
}
