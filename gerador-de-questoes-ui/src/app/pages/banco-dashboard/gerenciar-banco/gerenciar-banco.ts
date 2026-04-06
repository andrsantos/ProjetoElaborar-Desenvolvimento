import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { BancoQuestoesService } from '../../../services/banco-questoes/banco-questoes';
import { BancoQuestao } from '../../../models/banco-questao.model';

@Component({
  selector: 'app-gerenciar-banco',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gerenciar-banco.html',
  styleUrls: ['./gerenciar-banco.scss']
})
export class GerenciarBanco implements OnInit {

  questoes: BancoQuestao[] = [];
  isLoading = false;
  isEditModalOpen = false;
  questaoEmEdicao: BancoQuestao | null = null;
  objectKeys = Object.keys;
  isComentarioModalOpen = false;
  questaoComentario: BancoQuestao | null = null;
  isCadastroModalOpen = false;
  novaQuestao: BancoQuestao = this.criarNovaQuestao();
  isModoEdicao = false;

  constructor(
    private bancoService: BancoQuestoesService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.carregarQuestoes();
  }

  carregarQuestoes(): void {
    this.isLoading = true;
    this.bancoService.listarTodas().subscribe({
      next: (data) => {
        this.questoes = data;
        this.isLoading = false;
      },
      error: () => {
        this.toastr.error('Erro ao carregar questões.');
        this.isLoading = false;
      }
    });
  }

  onExcluir(id: string | undefined): void {
    if (!id) return;
    if (confirm('Tem certeza que deseja excluir esta questão?')) {
      this.bancoService.excluirQuestao(id).subscribe({
        next: () => {
          this.toastr.success('Questão excluída.');
          this.carregarQuestoes(); // Recarrega a lista
        },
        error: () => this.toastr.error('Erro ao excluir.')
      });
    }
  }

  onAbrirEdicao(questao: BancoQuestao): void {
    this.novaQuestao = JSON.parse(JSON.stringify(questao));
    this.isModoEdicao = true;
    this.isCadastroModalOpen = true;
  }

  onCancelarEdicao(): void {
    this.isEditModalOpen = false;
    this.questaoEmEdicao = null;
  }

  onSalvarEdicao(): void {
    if (!this.questaoEmEdicao || !this.questaoEmEdicao.id) return;
    this.bancoService.atualizarQuestao(this.questaoEmEdicao.id, this.questaoEmEdicao).subscribe({
      next: () => {
        this.toastr.success('Questão atualizada com sucesso!');
        this.isEditModalOpen = false;
        this.carregarQuestoes(); 
      },
      error: () => this.toastr.error('Erro ao atualizar questão.')
    });
  }

  abrirComentarios(questao: BancoQuestao) {
  this.questaoComentario = questao;
  this.isComentarioModalOpen = true;
  }

  fecharComentarios() {
  this.isComentarioModalOpen = false;
  this.questaoComentario = null;
  }

  criarNovaQuestao(): BancoQuestao {
  return {
    tipo: "MULTIPLA_ESCOLHA_5",
    topico: "",
    enunciado: "",
    alternativas: {
      a: "",
      b: "",
      c: "",
      d: "",
      e: ""
    },
    respostaCorreta: "",
    competencia: "",
    conceito: "",
    comentarioTecnico: ""
  } as BancoQuestao;
  }

  abrirCadastro() {
  this.novaQuestao = this.criarNovaQuestao();
  this.isCadastroModalOpen = true;
  }

  fecharCadastro() {
  this.isCadastroModalOpen = false;
  this.isModoEdicao = false;
  }

  // cadastrarQuestao() {

  // this.bancoService.cadastrarQuestao(this.novaQuestao)
  //   .subscribe({
  //     next: () => {
  //       this.toastr.success("Questão cadastrada com sucesso!");
  //       this.fecharCadastro();
  //       this.carregarQuestoes();
  //     },
  //     error: () => {
  //       this.toastr.error("Erro ao cadastrar questão");
  //     }
  //   });

  // }

  salvarQuestao() {

  if (this.isModoEdicao && this.novaQuestao.id) {

    this.bancoService
      .atualizarQuestao(this.novaQuestao.id, this.novaQuestao)
      .subscribe({
        next: () => {
          this.toastr.success("Questão atualizada com sucesso!");
          this.fecharCadastro();
          this.carregarQuestoes();
        },
        error: () => {
          this.toastr.error("Erro ao atualizar questão");
        }
      });

  } else {

    this.bancoService
      .cadastrarQuestao(this.novaQuestao)
      .subscribe({
        next: () => {
          this.toastr.success("Questão cadastrada com sucesso!");
          this.fecharCadastro();
          this.carregarQuestoes();
        },
        error: () => {
          this.toastr.error("Erro ao cadastrar questão");
        }
      });

  }

  }

}