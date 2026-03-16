import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BancoQuestoesService {
  private readonly API_URL = 'http://187.77.240.149:82/api/banco-questoes';

  constructor(private http: HttpClient) {}

  salvarQuestao(questao: any): Observable<any> {
    return this.http.post(this.API_URL, questao);
  }
  listarTodas(): Observable<any[]> {
    return this.http.get<any[]>(this.API_URL);
  }
  buscarPorId(id: string): Observable<any> {
    return this.http.get<any>(`${this.API_URL}/${id}`);
  }
  atualizarQuestao(id: string, questao: any): Observable<any> {
    return this.http.put(`${this.API_URL}/${id}`, questao);
  }
  excluirQuestao(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
  
