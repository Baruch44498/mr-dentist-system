import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard';
import { PacienteListComponent } from './components/paciente-list/paciente-list';
import { MedicoListComponent } from './components/medico-list/medico-list';
import { CitaListComponent } from './components/cita-list/cita-list';
import { EspecialidadListComponent } from './components/especialidad-list/especialidad-list';

export const routes: Routes = [
  { path: 'dashboard', component: DashboardComponent },
  { path: 'pacientes', component: PacienteListComponent },
  { path: 'medicos', component: MedicoListComponent },
  { path: 'especialidades', component: EspecialidadListComponent },
  { path: 'citas', component: CitaListComponent },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' }
];
