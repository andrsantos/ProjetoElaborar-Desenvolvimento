import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { GerenciamentoService } from '../../services/gerenciamento/gerenciamento-service';
import { Cenario } from '../../models/cenario.model';
import { Prompt } from '../../models/prompt.model';

@Component({
  selector: 'app-gerenciamento',
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './gerenciamento.html',
  styleUrl: './gerenciamento.scss',
  standalone: true
})
export class Gerenciamento implements OnInit {
  

  // Variáveis de Controle
  managementForm!: FormGroup;
  searchPerformed = false;
  listaDocumentacao: any[] = [];
  paginaAtual: number = 1;
  itensPorPagina: number = 7;
  filtroTopico: string = '';
  filtroNivel: string = '';
  
  //Variáveis de Cenário
  cenarioAtualizado: Cenario | null = null;
  listaCenarios: Cenario[] = [];
  showEditModal: boolean = false;
  editCenarioForm!: FormGroup;
  showDeleteModal: boolean = false;
  cenarioParaDeletar: Cenario | null = null;
  showInsertModal: boolean = false;
  insertCenarioForm!: FormGroup;

  // Variáveis de Prompt
  listaPrompts: Prompt[] = [];
  promptSelecionado: Prompt | null = null;
  showPromptModal: boolean = false;
  idPromptEditando: string | null = null;
  promptTemporario: any = {}; 
  showInsertPromptModal: boolean = false;
  insertPromptForm!: FormGroup;

  constructor(private fb: FormBuilder, private gerenciamentoService: GerenciamentoService) { }

  ngOnInit(): void {
    this.managementForm = this.fb.group({
      tableType: ['', Validators.required]
    });
    
    // Formulários de cenário e prompt
    this.editCenarioForm = this.fb.group({
      id: [''],
      topico: ['', [Validators.required, Validators.minLength(5)]],
      nivel: ['', Validators.required],
      descricao: ['', [Validators.required, Validators.minLength(10)]]
    });

    this.insertCenarioForm = this.fb.group({
      topico: ['', [Validators.required, Validators.minLength(5)]],
      nivel: ['', Validators.required],
      descricao: ['', [Validators.required, Validators.minLength(10)]]
    });

    this.insertPromptForm = this.fb.group({
      topico: ['', [Validators.required, Validators.minLength(5)]],
      nivel: ['', Validators.required],
      instrucoesEspecificas: ['', [Validators.required, Validators.minLength(20)]]
    });

  }



  onSearch(): void {
    if (this.managementForm.valid) {
      this.searchPerformed = true;
      this.listar();
    }
  }

  // Método de Listagem comum para todos os cenários
  listar(): void {
    console.log('Tipo selecionado para listagem:', this.managementForm.value.tableType);
    const request = {
      filtro: this.managementForm.value.tableType
    };

    if (request.filtro === 'scenarios') {
      request.filtro = 'CENARIO';
      this.gerenciamentoService.listarCenarios(request).subscribe({
        next: (cenarios) => {
          this.paginaAtual = 1;
          this.listaCenarios = cenarios;
          console.log('Cenarios listados:', cenarios);
        },
        error: (error) => console.error('Erro ao listar cenarios:', error)
      });
    }

    if (request.filtro === 'prompts') {
      request.filtro = 'PROMPTS';
      this.gerenciamentoService.listarPrompts(request).subscribe({
        next: (prompts) => {
          this.paginaAtual = 1;
          this.listaPrompts = prompts;
          console.log('Prompts listados:', prompts);
        },
        error: (error) => console.error('Erro ao listar prompts:', error)
      });
    }
  }

  
  // --- MÉTODOS DE PROMPT ---
  iniciarEdicaoPrompt(prompt: Prompt): void {
    this.idPromptEditando = prompt.id;
    this.promptTemporario = { ...prompt };
  }

  
  cancelarEdicao(): void {
    this.idPromptEditando = null;
    this.promptTemporario = {};
  }

  salvarEdicaoInline(): void {
    if (this.idPromptEditando) {
      this.gerenciamentoService.atualizarPrompt(this.idPromptEditando, this.promptTemporario).subscribe({
        next: (promptAtualizado) => {
          const index = this.listaPrompts.findIndex(p => p.id === this.idPromptEditando);
          if (index !== -1) {
            this.listaPrompts[index] = promptAtualizado;
          }
          this.cancelarEdicao(); 
          alert('Prompt atualizado com sucesso!');
        },
        error: (error) => {
          console.error('Erro ao atualizar prompt:', error);
          alert('Erro ao salvar alterações do prompt.');
        }
      });
    }
  }

  visualizarPrompt(prompt: Prompt): void {
    this.promptSelecionado = prompt;
    this.showPromptModal = true;
  }

  deletarPrompt(id: string): void {
    if (confirm('Deseja realmente excluir este prompt?')) {
      this.gerenciamentoService.deletarPrompt(id).subscribe({
        next: () => {
          this.listaPrompts = this.listaPrompts.filter(p => p.id !== id);
          console.log("Prompt deletado:", id);
        },
        error: (error) => console.error('Erro ao deletar prompt:', error)
      });
    }
  }

  abrirModalInsercaoPrompt(): void {
    this.insertPromptForm.reset({ nivel: '' });
    this.showInsertPromptModal = true;
  }

  fecharModalInsercaoPrompt(): void {
    this.showInsertPromptModal = false;
  }

  submeterInsercaoPrompt(): void {
    if (this.insertPromptForm.valid) {
      const novoPrompt: Prompt = this.insertPromptForm.value;
      
      this.gerenciamentoService.inserirPrompt(novoPrompt).subscribe({
        next: (promptCriado) => {
          console.log('Prompt criado com sucesso:', promptCriado);
          this.listaPrompts = [promptCriado, ...this.listaPrompts]; 
          this.fecharModalInsercaoPrompt();
          alert('Novo Prompt cadastrado!');
        },
        error: (error) => {
          console.error('Erro ao inserir prompt:', error);
          alert('Erro ao salvar o prompt no banco.');
        }
      });
    }
  }


  // --- MÉTODOS DE CENÁRIO ---
  submeterInsercao(): void {
    if (this.insertCenarioForm.valid) {
      const novoCenario: Cenario = this.insertCenarioForm.value;
      this.gerenciamentoService.inserirCenario(novoCenario).subscribe({
        next: (cenarioCriado) => {
          this.listaCenarios = [cenarioCriado, ...this.listaCenarios];
          this.fecharModalInsercao();
          alert('Cenário inserido com sucesso!');
        },
        error: (error) => alert('Erro ao salvar o novo cenário.')
      });
    }
  }

  abrirModalInsercao(): void {
    this.insertCenarioForm.reset({ nivel: '' });
    this.showInsertModal = true;
  }

  fecharModalInsercao(): void {
    this.showInsertModal = false;
  }

  confirmarExclusao(): void {
    if (this.cenarioParaDeletar?.id) {
      const id = this.cenarioParaDeletar.id;
      this.gerenciamentoService.deletarCenario(id).subscribe({
        next: () => {
          this.listaCenarios = this.listaCenarios.filter(c => c.id !== id);
          if (this.listaCenariosPaginada.length === 0 && this.paginaAtual > 1) this.paginaAtual--;
          this.fecharModalDelecao();
        },
        error: (error) => {
          console.error('Erro ao deletar:', error);
          this.fecharModalDelecao();
        }
      });
    }
  }

  abrirModalDelecao(cenario: Cenario): void {
    this.cenarioParaDeletar = cenario;
    this.showDeleteModal = true;
  }

  fecharModalDelecao(): void {
    this.showDeleteModal = false;
    this.cenarioParaDeletar = null;
  }

  submeterEdicao(): void {
    if (this.editCenarioForm.valid) {
      const cenarioDados: Cenario = this.editCenarioForm.value;
      this.gerenciamentoService.atualizarCenario(cenarioDados.id!, cenarioDados).subscribe({
        next: (cenarioRetornado) => {
          this.listaCenarios = this.listaCenarios.map(c =>
            c.id === cenarioRetornado.id ? cenarioRetornado : c
          );
          this.fecharModalEdicao();
          alert('Cenário atualizado com sucesso!');
        },
        error: (error) => console.error('Erro ao atualizar cenario:', error)
      });
    }
  }

  abrirModalEdicao(cenario: Cenario): void {
    this.showEditModal = true;
    this.cenarioAtualizado = { ...cenario };
    this.editCenarioForm.patchValue({
      id: cenario.id,
      topico: cenario.topico,
      nivel: cenario.nivel,
      descricao: cenario.descricao
    });
  }

  fecharModalEdicao(): void {
    this.showEditModal = false;
    this.editCenarioForm.reset();
    this.cenarioAtualizado = null;
  }

  // --- AUXILIARES ---
  mudarPagina(proxima: boolean): void {
    if (proxima && this.paginaAtual < this.totalPaginas) {
      this.paginaAtual++;
    } else if (!proxima && this.paginaAtual > 1) {
      this.paginaAtual--;
    }
  }

  get listaCenariosFiltrada(): Cenario[] {
    return this.listaCenarios.filter(cenario => {
      const correspondeTopico = cenario.topico.toLowerCase().includes(this.filtroTopico.toLowerCase());
      const correspondeNivel = this.filtroNivel === '' || cenario.nivel === this.filtroNivel;
      return correspondeTopico && correspondeNivel;
    });
  }

  get listaCenariosPaginada(): Cenario[] {
    const inicio = (this.paginaAtual - 1) * this.itensPorPagina;
    const fim = inicio + this.itensPorPagina;
    return this.listaCenariosFiltrada.slice(inicio, fim);
  }

  get listaPromptsFiltrada(): Prompt[] {
    return this.listaPrompts.filter(prompt => {
      const correspondeTopico = prompt.topico.toLowerCase().includes(this.filtroTopico.toLowerCase());
      const correspondeNivel = this.filtroNivel === '' || prompt.nivel === this.filtroNivel;
      return correspondeTopico && correspondeNivel;
    });
  }

  get listaPromptsPaginada(): Prompt[] {
    const inicio = (this.paginaAtual - 1) * this.itensPorPagina;
    const fim = inicio + this.itensPorPagina;
    return this.listaPromptsFiltrada.slice(inicio, fim);
  }

  get totalPaginas(): number {
    const tipoAtivo = this.managementForm.get('tableType')?.value;
    const totalRegistros = tipoAtivo === 'prompts' 
      ? this.listaPromptsFiltrada.length 
      : this.listaCenariosFiltrada.length;
      
    return Math.ceil(totalRegistros / this.itensPorPagina) || 1;
  }

  onFiltroChange(): void {
    this.paginaAtual = 1;
  }

  limparFiltros(): void {
    this.filtroTopico = '';
    this.filtroNivel = '';
    this.paginaAtual = 1;
  }
}