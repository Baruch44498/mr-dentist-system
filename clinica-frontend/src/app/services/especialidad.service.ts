import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { Especialidad } from '../models/especialidad.model';

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

  obtenerNombresEspecialidades(): Observable<string[]> {
    return this.listarEspecialidades().pipe(
      map((especialidades) => especialidades
        .filter((item) => item.estado !== false)
        .map((item) => item.nombre)
      )
    );
  }
}
