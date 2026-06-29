import { Paciente } from './paciente.model';
import { Cita } from './cita.model';

export interface DetalleReceta {
  idDetalleReceta?: number;
  medicamento: string;
  presentacion?: string;
  dosis: string;
  frecuencia: string;
  duracion: string;
  viaAdministracion?: string;
  indicaciones?: string;
  estado?: boolean;
}

export interface Receta {
  idReceta?: number;
  paciente?: Paciente;
  cita?: Cita;
  medicamentos?: string;
  fechaEmision?: string;
  estado?: boolean;
  detalles?: DetalleReceta[];
}

