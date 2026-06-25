export interface Medico {
  idMedico?: number;
  nombres: string;
  apellidos: string;
  dni: string;
  cop: string;
  especialidad: string;
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
