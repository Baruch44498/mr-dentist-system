import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Receta } from '../models/receta.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RecetaService {
  private readonly http = inject(HttpClient);
  private readonly pacienteApiUrl = `${environment.apiUrl}/pacientes`;
  private readonly recetaApiUrl = `${environment.apiUrl}/recetas`;

  listarRecetas(pacienteId: number): Observable<Receta[]> {
    return this.http.get<Receta[]>(`${this.pacienteApiUrl}/${pacienteId}/recetas`);
  }

  registrarReceta(pacienteId: number, receta: Receta): Observable<Receta> {
    return this.http.post<Receta>(`${this.pacienteApiUrl}/${pacienteId}/recetas`, receta);
  }

  actualizarReceta(idReceta: number, receta: Receta): Observable<Receta> {
    return this.http.put<Receta>(`${this.recetaApiUrl}/${idReceta}`, receta);
  }

  eliminarReceta(idReceta: number): Observable<void> {
    return this.http.delete<void>(`${this.recetaApiUrl}/${idReceta}`);
  }
}
