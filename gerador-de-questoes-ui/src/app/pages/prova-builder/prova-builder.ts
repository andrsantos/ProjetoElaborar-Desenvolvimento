import { Component, OnInit } from '@angular/core';
import { TopicoQuantidade } from '../../models/topico-quantidade.model';
import { Observable, shareReplay } from 'rxjs';
import { Prova } from '../../models/prova.model';
import { ProvaService } from '../../services/prova/prova-service';
import { ToastrService } from 'ngx-toastr';
import { BancoQuestoesService } from '../../services/banco-questoes/banco-questoes';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export type CampoEdicao = 'enunciado' | 'resposta' | 'a' | 'b' | 'c' | 'd' | 'e';


export interface EstadoEdicao {
  indexQuestao: number;
  campo: CampoEdicao;
}


@Component({
  selector: 'app-prova-builder',
  imports: [CommonModule, FormsModule],
  templateUrl: './prova-builder.html',
  styleUrl: './prova-builder.scss',
})
export class ProvaBuilder implements OnInit {

  provaId: string | null = null;
  public isDropdownOpen = false;
  public topicosSelecionados: TopicoQuantidade[] = [];
  public topicosDisponiveis: string[] = [];
  isLoadingAdicionar = false;
  prova$: Observable<Prova> | null = null;
  isLoadingCriar = false;
  public editando: EstadoEdicao | null = null;
  descartandoIndex: number | null = null;
  modalAberta = false;
  questaoSelecionada: any = null;
  isLoadingFinalizar = false;




 

  constructor(private provaService: ProvaService, private toastr: ToastrService,
    private bancoQuestoesService: BancoQuestoesService
  ) { }
  


  ngOnInit(): void {
  this.onCriarProva();
    this.provaService.getTopicosDisponiveis().subscribe(topicos => {
      this.topicosDisponiveis = topicos;
  });
  }


  toggleDropdown(): void {
        this.isDropdownOpen = !this.isDropdownOpen;
  }

  onCriarProva() {

      this.isLoadingCriar = true; 
      this.prova$ = this.provaService.criarProva().pipe(
        shareReplay(1) 
      );

      this.prova$.subscribe({
        next: p => {
          this.provaId = p.id;
          this.isLoadingCriar = false; 
        },
        error: () => this.isLoadingCriar = false 
      });
  }

  

  isTopicoSelecionado(topico: string): boolean {
    return this.topicosSelecionados.some(t => t.topico === topico);
  }

  onToggleTopico(topico: string, event: any): void {
    const isChecked = event.target.checked;

    if (isChecked) {
      const jaExiste = this.topicosSelecionados.some(t => t.topico === topico);
      
      if (!jaExiste) {
        this.topicosSelecionados.push({
          topico: topico,
          quantidade: 5,
          quantidadeDificeis: 0,
          quantidadeFaceis: 0,
          quantidadeMedias: 0
        });
      }
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

  atualizarTotal(item: TopicoQuantidade): void {
  if (item.quantidadeDificeis < 0) item.quantidadeDificeis = 0;
  if (item.quantidadeMedias < 0) item.quantidadeMedias = 0;
  if (item.quantidadeFaceis < 0) item.quantidadeFaceis = 0;
  item.quantidade = item.quantidadeDificeis + item.quantidadeMedias + item.quantidadeFaceis;
  }


  onGerarProvaBanco() {

    if (!this.provaId || this.topicosSelecionados.length === 0) {
      alert("Por favor, adicione pelo menos um tópico."); 
      return;
    }

    this.isLoadingAdicionar = true; 
        
    console.log("Topicos selecionados para geração de prova a partir de questões do banco:", this.topicosSelecionados);
        
    this.prova$ = this.provaService.gerarProvaBanco(
          this.provaId, 
          this.topicosSelecionados
    ).pipe(
          shareReplay(1) 
    );
        
    this.prova$.subscribe({
          next: () => {
            this.isLoadingAdicionar = false;
          },
          error: () => this.isLoadingAdicionar = false
    });
    
    this.prova$.forEach(prova => {
          console.log("Prova gerada com questões do banco", prova.questoes);
    });
  

  }

   isEditando(index: number, campo: string): boolean {
    return this.editando?.indexQuestao === index && this.editando?.campo === campo;
  }

  ativarEdicao(index: number, campo: string): void {
      this.editando = { indexQuestao: index, campo: campo as CampoEdicao };
  }
  
  salvarEdicao(): void {
    this.editando = null;
    this.toastr.info("Alteração salva localmente.");
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

  abrirComentarios(questao: any) {
  this.questaoSelecionada = questao;
  this.modalAberta = true;
  }

  fecharModal() {
  this.modalAberta = false;
  this.questaoSelecionada = null;
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
