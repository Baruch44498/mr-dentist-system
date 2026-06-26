import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Especialidad } from '../models/especialidad.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EspecialidadService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/especialidades`;

  listarEspecialidades(): Observable<Especialidad[]> {
    return this.http.get<Especialidad[]>(this.apiUrl);
  }

  registrarEspecialidad(especialidad: Especialidad): Observable<Especialidad> {
    return this.http.post<Especialidad>(this.apiUrl, especialidad);
  }

  actualizarEspecialidad(id: number, especialidad: Especialidad): Observable<Especialidad> {
    return this.http.put<Especialidad>(`${this.apiUrl}/${id}`, especialidad);
  }

  eliminarEspecialidad(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
