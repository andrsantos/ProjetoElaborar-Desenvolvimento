import { HttpClient, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AlimentacaoService {

  private readonly API_URL = 'http://localhost:8080/api/alimentacao';

  constructor(private http: HttpClient) { }


  uploadPdf(file: File, topico: string): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);
    formData.append('topico', topico); 

    return this.http.post(`${this.API_URL}/upload-pdf`, formData, {
      reportProgress: true,
      observe: 'events' ,
      responseType:'text'
    });
  }
  
}
