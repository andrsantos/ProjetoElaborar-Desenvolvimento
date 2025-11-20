import { Injectable } from '@angular/core';
import { Questao } from '../../models/questao.model';
import { EstadoProvaManual } from '../../models/estado-prova-manual.model';



@Injectable({
  providedIn: 'root'
})
export class ProvaManualStateService {

  private estado: EstadoProvaManual | null = null;

  constructor() { }

  salvarEstado(dados: EstadoProvaManual): void {
    this.estado = dados;
  }

  lerEstado(): EstadoProvaManual | null {
    return this.estado;
  }

  definirQuestaoEscolhida(questao: Questao): void {
    if (this.estado && this.estado.indiceEdicao !== null && this.estado.cards) {
      console.log("Questao escolhida:", questao);
      this.estado.cards[this.estado.indiceEdicao].questaoPreenchida = questao;
      this.estado.cards[this.estado.indiceEdicao].usarBanco = 'S'; 
      this.estado.cards[this.estado.indiceEdicao].usarIA = 'N';
      this.estado.indiceEdicao = null; 
    }
  }

  limparEstado(): void {
    this.estado = null;
  }

  temEstadoSalvo(): boolean {
    return this.estado !== null;
  }
}