export type TipoQuestao = 
  | 'MULTIPLA_ESCOLHA_5' 
  | 'MULTIPLA_ESCOLHA_4' 
  | 'VERDADEIRO_FALSO' 
  | 'DISCURSIVA';

export interface BancoQuestao {
  id?: string; 
  
  topico: string;
  
  enunciado: string;
  
  tipo: TipoQuestao; 
  
  alternativas: { [key: string]: string }; 
  
  respostaCorreta: string;

  conceito: string;

  comentarioTecnico: string;

  competencia: string;
  
}