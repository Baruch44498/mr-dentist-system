import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { Especialidad } from '../models/especialidad.model';

@Injectable({
  providedIn: 'root'
})
export class EspecialidadService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/especialidades`;

  private fallbackMode = false;
  private localEspecialidades: Especialidad[] = [
    { id: 1, idEspecialidad: 1, nombre: 'Odontología General', descripcion: 'Diagnóstico, prevención y atención odontológica integral.', estado: true },
    { id: 2, idEspecialidad: 2, nombre: 'Ortodoncia', descripcion: 'Corrección de posición dental y maloclusiones.', estado: true },
    { id: 3, idEspecialidad: 3, nombre: 'Endodoncia', descripcion: 'Tratamiento de conductos y patologías pulpares.', estado: true },
    { id: 4, idEspecialidad: 4, nombre: 'Odontopediatría', descripcion: 'Atención odontológica para niños y adolescentes.', estado: true },
    { id: 5, idEspecialidad: 5, nombre: 'Rehabilitación Oral', descripcion: 'Restauración funcional y estética de piezas dentales.', estado: true },
    { id: 6, idEspecialidad: 6, nombre: 'Cirugía Bucal', descripcion: 'Procedimientos quirúrgicos odontológicos ambulatorios.', estado: true },
    { id: 7, idEspecialidad: 7, nombre: 'Periodoncia', descripcion: 'Tratamiento de encías y tejidos de soporte dental.', estado: true },
    { id: 8, idEspecialidad: 8, nombre: 'Implantología', descripcion: 'Planificación y colocación de implantes dentales.', estado: true },
    { id: 9, idEspecialidad: 9, nombre: 'Cirugía Maxilofacial', descripcion: 'Atención quirúrgica especializada oral y maxilofacial.', estado: true },
    { id: 10, idEspecialidad: 10, nombre: 'Estética Dental', descripcion: 'Tratamientos enfocados en armonía y apariencia dental.', estado: true }
  ];

  listarEspecialidades(): Observable<Especialidad[]> {
    if (this.fallbackMode) {
      return of(this.clonarLocal());
    }

    return this.http.get<Especialidad[]>(this.apiUrl).pipe(
      tap((data) => {
        if (Array.isArray(data) && data.length > 0) {
          this.localEspecialidades = data.map((item, index) => this.normalizarEspecialidad(item, index));
        }
      }),
      catchError((error) => {
        console.warn('Endpoint de especialidades no disponible. Se usará fallback local temporal.', error);
        this.fallbackMode = true;
        return of(this.clonarLocal());
      })
    );
  }

  registrarEspecialidad(especialidad: Especialidad): Observable<Especialidad> {
    if (this.fallbackMode) {
      return of(this.registrarLocal(especialidad));
    }

    return this.http.post<Especialidad>(this.apiUrl, especialidad).pipe(
      catchError((error) => {
        console.warn('No se pudo registrar en API. Se registrará temporalmente en memoria local.', error);
        this.fallbackMode = true;
        return of(this.registrarLocal(especialidad));
      })
    );
  }

  actualizarEspecialidad(id: number, especialidad: Especialidad): Observable<Especialidad> {
    if (this.fallbackMode) {
      return of(this.actualizarLocal(id, especialidad));
    }

    return this.http.put<Especialidad>(`${this.apiUrl}/${id}`, especialidad).pipe(
      catchError((error) => {
        console.warn('No se pudo actualizar en API. Se actualizará temporalmente en memoria local.', error);
        this.fallbackMode = true;
        return of(this.actualizarLocal(id, especialidad));
      })
    );
  }

  eliminarEspecialidad(id: number): Observable<void> {
    if (this.fallbackMode) {
      this.eliminarLocal(id);
      return of(void 0);
    }

    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        console.warn('No se pudo eliminar en API. Se eliminará temporalmente en memoria local.', error);
        this.fallbackMode = true;
        this.eliminarLocal(id);
        return of(void 0);
      })
    );
  }

  obtenerNombresEspecialidades(): Observable<string[]> {
    return this.listarEspecialidades().pipe(
      map((especialidades) => especialidades
        .filter((item) => item.estado !== false)
        .map((item) => item.nombre)
      ),
      catchError(() => of(this.obtenerEspecialidadesBase()))
    );
  }

  obtenerEspecialidadesBase(): string[] {
    return this.localEspecialidades
      .filter((item) => item.estado !== false)
      .map((item) => item.nombre);
  }

  estaEnModoFallback(): boolean {
    return this.fallbackMode;
  }

  private clonarLocal(): Especialidad[] {
    return this.localEspecialidades.map((item) => ({ ...item }));
  }

  private normalizarEspecialidad(item: Especialidad, index: number): Especialidad {
    const id = this.obtenerId(item) ?? index + 1;
    return {
      ...item,
      id,
      idEspecialidad: item.idEspecialidad ?? id,
      estado: item.estado ?? true
    };
  }

  private registrarLocal(especialidad: Especialidad): Especialidad {
    const nextId = this.generarSiguienteId();
    const nueva: Especialidad = {
      ...especialidad,
      id: nextId,
      idEspecialidad: nextId,
      estado: especialidad.estado ?? true
    };
    this.localEspecialidades = [...this.localEspecialidades, nueva];
    return { ...nueva };
  }

  private actualizarLocal(id: number, especialidad: Especialidad): Especialidad {
    const actualizada: Especialidad = {
      ...especialidad,
      id,
      idEspecialidad: especialidad.idEspecialidad ?? id,
      estado: especialidad.estado ?? true
    };

    this.localEspecialidades = this.localEspecialidades.map((item) =>
      this.obtenerId(item) === id ? actualizada : item
    );

    return { ...actualizada };
  }

  private eliminarLocal(id: number): void {
    this.localEspecialidades = this.localEspecialidades.filter((item) => this.obtenerId(item) !== id);
  }

  private generarSiguienteId(): number {
    const ids = this.localEspecialidades
      .map((item) => this.obtenerId(item) ?? 0)
      .filter((id) => Number.isFinite(id));
    return ids.length > 0 ? Math.max(...ids) + 1 : 1;
  }

  private obtenerId(item: Especialidad): number | undefined {
    return item.idEspecialidad ?? item.id;
  }
}
