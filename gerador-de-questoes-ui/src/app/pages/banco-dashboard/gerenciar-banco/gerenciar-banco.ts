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
    this.questaoEmEdicao = JSON.parse(JSON.stringify(questao));
    this.isEditModalOpen = true;
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
}