import { Especialidad } from './especialidad.model';

export interface Medico {
  idMedico?: number;
  nombres: string;
  apellidos: string;
  dni: string;
  cop: string;
  especialidad?: Especialidad | Partial<Especialidad>;
  horarioTurno: string;
  telefono?: string;
  correo?: string;
  estado?: boolean;
}

export interface TurnoPlanificado {
  idTurno?: number;
  medico?: Medico;
  fecha: string;
  hora: string;
  activo: boolean;
}
