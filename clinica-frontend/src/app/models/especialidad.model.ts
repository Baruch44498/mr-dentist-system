export interface Especialidad {
  idEspecialidad?: number;
  id?: number; // Resiliencia para compatibilidad con backend genérico
  nombre: string;
  descripcion?: string;
  estado?: boolean;
}
