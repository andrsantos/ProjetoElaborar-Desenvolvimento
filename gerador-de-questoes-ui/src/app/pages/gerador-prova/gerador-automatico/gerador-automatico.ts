import { Component, OnInit } from '@angular/core';
import { Prova } from '../../../models/prova.model';
import { Observable } from 'rxjs/internal/Observable';
import { ProvaService } from '../../../services/prova/prova-service';
import { GerarQuestaoRequest } from '../../../models/gerar-questao-request.model';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import { TopicoQuantidade } from '../../../models/topico-quantidade.model';

@Component({
  selector: 'app-gerador-prova',
  imports: [CommonModule, FormsModule],
  templateUrl: './gerador-automatico.html',
  styleUrl: './gerador-automatico.scss',
  standalone: true
})
export class GeradorAutomatico implements OnInit {

  public topicosDisponiveis: string[] = [];
  public topicosSelecionados: TopicoQuantidade[] = [];
  public isDropdownOpen = false;
  prova$: Observable<Prova> | null = null;
  provaId: string | null = null;
  topico: string = "Modelo OSI";
  quantidade: number = 5;
  isLoadingCriar = false;
  isLoadingAdicionar = false;
  isLoadingFinalizar = false;
  descartandoIndex: number | null = null;

  constructor(private provaService: ProvaService, private toastr: ToastrService) { }

  ngOnInit(): void {
    this.onCriarProva();
    this.provaService.getTopicosDisponiveis().subscribe(topicos => {
      this.topicosDisponiveis = topicos;
    });
  }
  
  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  isTopicoSelecionado(topico: string): boolean {
    this.isDropdownOpen = false;
    return this.topicosSelecionados.some(t => t.topico === topico);
  }

  onToggleTopico(topico: string, event: any): void {
    const isChecked = event.target.checked;

    if (isChecked) {
      this.topicosSelecionados.push({
        topico: topico,
        quantidade: 5
      });
    } else {
      this.onRemoverTopico(topico);
    }
  }

  onRemoverTopico(topicoParaRemover: string): void {
    this.topicosSelecionados = this.topicosSelecionados.filter(
      t => t.topico !== topicoParaRemover
    );
  }

  get listaTopicosFormatada(): string {
    if (this.topicosSelecionados.length === 0) {
      return "Nenhum tópico selecionado.";
    }
    return this.topicosSelecionados.map(t => t.topico).join(', ');
  }
  // onRemoverTopico(topicoParaRemover: string): void {
  //   this.topicosSelecionados = this.topicosSelecionados.filter(
  //     t => t.topico !== topicoParaRemover
  //   );
  // }

  onCriarProva() {
      this.isLoadingCriar = true; 
      this.prova$ = this.provaService.criarProva();
      this.prova$.subscribe({
        next: p => {
          this.provaId = p.id;
          this.isLoadingCriar = false; 
        },
        error: () => this.isLoadingCriar = false 
      });
    }

  onGerarProvaAutomatica() {
    if (!this.provaId || this.topicosSelecionados.length === 0) {
      alert("Por favor, adicione pelo menos um tópico."); 
      return;
    }
    this.isLoadingAdicionar = true; 
    this.prova$ = this.provaService.adicionarQuestoesAutomatico(
      this.provaId, 
      this.topicosSelecionados
    );
    
    this.prova$.subscribe({
      next: () => {
        this.isLoadingAdicionar = false;
        this.topicosSelecionados = []; 
      },
      error: () => this.isLoadingAdicionar = false
    });
  }

  onAdicionarQuestoes() {
      if (!this.provaId) return;
      this.isLoadingAdicionar = true; 

      const request: GerarQuestaoRequest = {
        topico: this.topico,
        quantidade: this.quantidade
      };

      this.prova$ = this.provaService.adicionarQuestoes(this.provaId, request);
      this.prova$.subscribe({
        next: () => this.isLoadingAdicionar = false, 
        error: () => this.isLoadingAdicionar = false 
      });
    }

  onDescartarQuestao(indice: number) {
        if (!this.provaId) return;
        this.descartandoIndex = indice; 

        this.prova$ = this.provaService.descartarQuestao(this.provaId, indice);
        this.prova$.subscribe({
          next: () => this.descartandoIndex = null, 
          error: () => this.descartandoIndex = null 
        });
  
      }

  onFinalizarProva() {
    if (!this.provaId) {
      alert("Nenhuma prova ativa.");
      return;
    }
    this.isLoadingFinalizar = true;
    this.provaService.finalizarProvaPdf(this.provaId).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = `prova_${this.provaId}.pdf`; 
        
        document.body.appendChild(a);
        a.click();

        window.URL.revokeObjectURL(url);
        a.remove();

        this.prova$ = null;
        this.provaId = null;
        this.isLoadingFinalizar = false;
        this.toastr.success("Prova gerada com sucesso!", 'Sucesso!');
      },
      error: (err) => {
        console.error("Erro ao finalizar a prova:", err);
        alert("Falha ao gerar o PDF da prova.");
        this.isLoadingFinalizar = false;
      }
    });
  }

}
