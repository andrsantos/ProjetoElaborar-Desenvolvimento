import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common'; 
import { ActivatedRoute, RouterLink, Router } from '@angular/router'; 
import { Observable, switchMap } from 'rxjs';
import { ProvaSalva } from '../../models/prova-entity.model';
import { ProvaService } from '../../services/prova/prova-service';
import { NotificationService } from '../../services/notification/notification-service';

@Component({
  selector: 'app-detalhe-prova',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe], 
  templateUrl: './detalhe-prova.html', 
  styleUrls: ['./detalhe-prova.scss']    
})
export class DetalheProva implements OnInit {

  public prova$!: Observable<ProvaSalva>;
  public isDeleteModalVisible = false;
  public isDeleting = false;
  public isDownloading = false;
  private provaId: string | null = null; 


  constructor(
    private provaService: ProvaService,
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.prova$ = this.route.paramMap.pipe(
      switchMap(params => {
        const id = params.get('id');
        
        if (!id) {
          console.error('ID da prova não encontrado na URL');
          throw new Error('ID da prova não encontrado');
        }
        this.provaId = id;
        return this.provaService.getDetalheProva(id);
      })
    );
  }

  temAlternativasValidas(alternativas: any): boolean {
    if (!alternativas) return false;
    return Object.values(alternativas).some((valor: any) => valor && valor.trim() !== '');
  }

  isMultiplaEscolha4(alternativas: any): boolean {
    if (!alternativas) return false;
    const alternativasValidas = Object.values(alternativas).filter((valor: any) => valor && valor.trim() !== '').length;
    if(alternativasValidas === 4){
      return true;
    } else {
      return false;
    }
  }
  
  openDeleteModal(): void {
    this.isDeleteModalVisible = true;
  }

  closeDeleteModal(): void {
    this.isDeleteModalVisible = false;
  }

  onConfirmDelete(): void {
    if (!this.provaId) return;

    this.isDeleting = true; 
    this.provaService.deleteProva(this.provaId).subscribe({
      next: () => {
        this.isDeleting = false;
        this.isDeleteModalVisible = false;
        this.notificationService.setMessage('Prova excluída com sucesso!');
        this.router.navigate(['/provas-salvas']);
      },
      error: (err) => {
        console.error('Erro ao excluir prova:', err);
        alert('Falha ao excluir a prova.');
        this.isDeleting = false;
        this.isDeleteModalVisible = false;
      }
    });
  }

  onDownloadPdf(): void {
    if (!this.provaId) return;

    this.isDownloading = true; 
    this.provaService.downloadProvaSalvaPdf(this.provaId).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `prova_salva_${this.provaId}.pdf`; 
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        a.remove();
        
        this.isDownloading = false; 
      },
      error: (err) => {
        console.error('Erro ao baixar PDF:', err);
        alert('Falha ao baixar o PDF.');
        this.isDownloading = false;
      }
    });
  }


}