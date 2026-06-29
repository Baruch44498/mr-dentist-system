import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cita } from '../models/cita.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CitaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/citas`;

  listarCitas(): Observable<Cita[]> {
    return this.http.get<Cita[]>(this.apiUrl);
  }

  listarCitasPorPaciente(pacienteId: number): Observable<Cita[]> {
    return this.http.get<Cita[]>(`${this.apiUrl}/paciente/${pacienteId}`);
  }


  registrarCita(cita: Cita): Observable<Cita> {
    return this.http.post<Cita>(this.apiUrl, cita);
  }

  actualizarCita(id: number, cita: Cita): Observable<Cita> {
    return this.http.put<Cita>(`${this.apiUrl}/${id}`, cita);
  }

  eliminarCita(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  cancelarCita(id: number, motivo: string): Observable<Cita> {
    const params = new HttpParams().set('motivo', motivo);
    return this.http.put<Cita>(`${this.apiUrl}/${id}/cancelar`, {}, { params });
  }
}
