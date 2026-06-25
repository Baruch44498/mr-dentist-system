export interface Paciente {
  idPaciente?: number;
  nombres: string;
  apellidos: string;
  dni: string;
  telefono?: string;
  correo?: string;
  fechaRegistro?: string;
  estado?: boolean;
}
