import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Medico, TurnoPlanificado } from '../models/medico.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MedicoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/medicos`;

  listarMedicos(especialidad?: string): Observable<Medico[]> {
    let params = new HttpParams();
    if (especialidad) {
      params = params.set('especialidad', especialidad);
    }
    return this.http.get<Medico[]>(this.apiUrl, { params });
  }

  registrarMedico(medico: Medico): Observable<Medico> {
    return this.http.post<Medico>(this.apiUrl, medico);
  }

  actualizarMedico(id: number, medico: Medico): Observable<Medico> {
    return this.http.put<Medico>(`${this.apiUrl}/${id}`, medico);
  }

  eliminarMedico(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  obtenerDisponibilidad(idMedico: number, fecha: string): Observable<string[]> {
    const params = new HttpParams().set('fecha', fecha);
    return this.http.get<string[]>(`${this.apiUrl}/${idMedico}/disponibilidad`, { params });
  }

  obtenerPlanificacion(idMedico: number, fecha: string): Observable<TurnoPlanificado[]> {
    const params = new HttpParams().set('fecha', fecha);
    return this.http.get<TurnoPlanificado[]>(`${this.apiUrl}/${idMedico}/planificacion`, { params });
  }

  guardarPlanificacion(idMedico: number, fecha: string, horasActivas: string[]): Observable<void> {
    const params = new HttpParams().set('fecha', fecha);
    return this.http.post<void>(`${this.apiUrl}/${idMedico}/planificacion`, horasActivas, { params });
  }
}
