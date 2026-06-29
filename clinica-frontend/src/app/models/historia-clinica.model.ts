import { Paciente } from './paciente.model';

export interface HistoriaClinica {
  idHistoriaClinica?: number;
  paciente?: Paciente;
  antecedentesMedicos?: string;
  antecedentesOdontologicos?: string;
  observacionesGenerales?: string;
  fechaActualizacion?: string;
  estado?: boolean;
}
