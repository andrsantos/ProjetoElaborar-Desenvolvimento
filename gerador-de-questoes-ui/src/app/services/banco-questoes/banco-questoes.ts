import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BancoQuestoesService {
  private readonly API_URL = 'http://localhost:8080/api/banco-questoes';

  constructor(private http: HttpClient) {}

  salvarQuestao(questao: any): Observable<any> {
    return this.http.post(this.API_URL, questao);
  }
}
  
