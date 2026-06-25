import { Paciente } from './paciente.model';
import { Medico } from './medico.model';

export interface Cita {
  idCita?: number;
  paciente: Paciente;
  medico: Medico;
  fechaHora: string; // ISO date-time '2026-06-25T14:00:00'
  motivo?: string;
  estadoCita?: string; // PENDIENTE, REALIZADA, CANCELADA
  estado?: boolean;
}
