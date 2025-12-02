import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tarefa } from '../models/tarefa.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TarefaService {

  private base = environment.production
  ? `${environment.apiUrl}/tarefas`
  : '/api/tarefas';


  constructor(private http: HttpClient) {}

  listar(): Observable<Tarefa[]> {
    return this.http.get<Tarefa[]>(this.base);
  }

  criar(dto: { nome: string; custo: number; dataLimite: string }) {
    return this.http.post<Tarefa>(this.base, dto);
  }

  editar(id: number, dto: { nome: string; custo: number; dataLimite: string }) {
    return this.http.put<Tarefa>(`${this.base}/${id}`, dto);
  }

  excluir(id: number) {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  subir(id: number) {
    return this.http.post<void>(`${this.base}/${id}/up`, {});
  }

  descer(id: number) {
    return this.http.post<void>(`${this.base}/${id}/down`, {});
  }

  totalCusto(): Observable<string> {
    return this.http.get<string>(`${this.base}/total-custo`);
  }

  updateOrdem(id: number, ordem: number) {
    return this.http.patch<void>(`${this.base}/${id}/ordem`, { ordem });
  }

}
