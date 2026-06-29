import { Paciente } from './paciente.model';
import { Cita } from './cita.model';
import { Medico } from './medico.model';

export interface Seguimiento {
  idSeguimiento?: number;
  paciente?: Paciente;
  cita?: Cita;
  medico?: Medico;
  descripcion: string;
  fechaSeguimiento?: string;
  estado?: boolean;
}
