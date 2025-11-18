import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Questao } from '../../../models/questao.model';
import { Router } from '@angular/router';
import { ProvaService } from '../../../services/prova/prova-service';

interface CardQuestao {
  indice: number;      
  topicoSelecionado: string;
  usarIA: string;       
  usarBanco: string;    
  questaoPreenchida: Questao | null; 
  isLoading: boolean;   
}

@Component({
  selector: 'app-gerador-manual',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gerador-manual.html',
  styleUrls: ['./gerador-manual.scss']
})
export class GeradorManual implements OnInit {

  quantidadeDesejada: number | null = null;
  cards: CardQuestao[] = [];
  topicosDisponiveis: string[] = [];
  provaId: string | null = null;
  isCardsGenerated = false;
  isFinalizing = false;

  constructor(
    private provaService: ProvaService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  ngOnInit(): void {

    this.provaService.criarProva().subscribe(p => this.provaId = p.id);
    this.provaService.getTopicosDisponiveis().subscribe(t => this.topicosDisponiveis = t);
  }

  onGerarCards(): void {
    if (!this.quantidadeDesejada || this.quantidadeDesejada <= 0) {
      this.toastr.warning('Selecione uma quantidade válida.');
      return;
    }

    this.cards = [];
    for (let i = 0; i < this.quantidadeDesejada; i++) {
      this.cards.push({
        indice: i,
        topicoSelecionado: '', 
        usarIA: 'S',          
        usarBanco: 'N',
        questaoPreenchida: null,
        isLoading: false
      });
    }
    this.isCardsGenerated = true;
  }


  onGerarQuestaoCard(card: CardQuestao): void {

    if (!card.topicoSelecionado) {
      this.toastr.warning(`Selecione um tópico para a Questão ${card.indice + 1}`);
      return;
    }
    if (card.usarIA === 'N') {
      this.toastr.info('Geração manual sem IA não implementada ainda.');
      return;
    }

    card.isLoading = true;

    this.provaService.gerarQuestaoAvulsa(card.topicoSelecionado, 1).subscribe({
      next: (response) => {
        if (response.questoes && response.questoes.length > 0) {
          card.questaoPreenchida = response.questoes[0]; 
        }
        card.isLoading = false;
      },
      error: () => {
        this.toastr.error('Erro ao gerar questão.');
        card.isLoading = false;
      }
    });
  }

  onDescartarQuestaoCard(card: CardQuestao): void {
    card.questaoPreenchida = null;
  }


  onFinalizarProvaManual(): void {
    if (!this.provaId) return;

    const questoesProntas = this.cards
      .filter(c => c.questaoPreenchida !== null)
      .map(c => c.questaoPreenchida!); 

    if (questoesProntas.length !== this.cards.length) {
      this.toastr.warning(`Você preencheu ${questoesProntas.length} de ${this.cards.length} questões. Preencha todas antes de gerar.`);
      return;
    }

    this.isFinalizing = true;

    this.provaService.salvarProvaManual(this.provaId, questoesProntas).subscribe({
      next: () => {
        this.baixarPdfEFinalizar();
      },
      error: () => {
        this.toastr.error('Erro ao salvar prova manual.');
        this.isFinalizing = false;
      }
    });
  }

  baixarPdfEFinalizar() {
    if (!this.provaId) return;
    this.provaService.finalizarProvaPdf(this.provaId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `prova_manual_${this.provaId}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        a.remove();

        this.toastr.success('Prova manual gerada com sucesso!');
        this.router.navigate(['/provas-salvas']); 
      },
      error: () => {
        this.toastr.error('Erro ao gerar PDF.');
        this.isFinalizing = false;
      }
    });
  }
}