package com.Projeto.GeradorDeQuestoes.services;

import java.util.List;
import com.Projeto.GeradorDeQuestoes.dto.CenarioConfigDTO;
import com.Projeto.GeradorDeQuestoes.dto.TopicoConfigDTO;
import com.Projeto.GeradorDeQuestoes.entities.CenarioConfigEntity;
import com.Projeto.GeradorDeQuestoes.entities.TopicoConfigEntity;


public interface GerenciamentoService {

   public List<TopicoConfigEntity> listarTopicos();
   public TopicoConfigDTO criarTopico(TopicoConfigDTO topicoConfigDTO);
   public void deletarTopico(String id);
   public TopicoConfigDTO atualizarTopico(String id, TopicoConfigDTO topicoConfigDTO);

   public List<CenarioConfigEntity> listarCenarios();
   public CenarioConfigDTO criarCenario(CenarioConfigDTO cenarioConfigDTO);
   public void deletarCenario(Long id);
   public CenarioConfigDTO atualizarCenario(Long id, CenarioConfigDTO cenarioConfigDTO);  

    
}
