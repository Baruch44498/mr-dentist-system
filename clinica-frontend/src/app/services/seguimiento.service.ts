import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Seguimiento } from '../models/seguimiento.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SeguimientoService {
  private readonly http = inject(HttpClient);
  private readonly pacienteApiUrl = `${environment.apiUrl}/pacientes`;
  private readonly seguimientoApiUrl = `${environment.apiUrl}/seguimientos`;

  listarSeguimientos(pacienteId: number): Observable<Seguimiento[]> {
    return this.http.get<Seguimiento[]>(`${this.pacienteApiUrl}/${pacienteId}/seguimientos`);
  }

  registrarSeguimiento(pacienteId: number, seguimiento: Seguimiento): Observable<Seguimiento> {
    return this.http.post<Seguimiento>(`${this.pacienteApiUrl}/${pacienteId}/seguimientos`, seguimiento);
  }

  actualizarSeguimiento(idSeguimiento: number, seguimiento: Seguimiento): Observable<Seguimiento> {
    return this.http.put<Seguimiento>(`${this.seguimientoApiUrl}/${idSeguimiento}`, seguimiento);
  }

  eliminarSeguimiento(idSeguimiento: number): Observable<void> {
    return this.http.delete<void>(`${this.seguimientoApiUrl}/${idSeguimiento}`);
  }
}
