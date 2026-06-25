import { Component, OnInit, signal, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PacienteService } from '../../services/paciente.service';
import { MedicoService } from '../../services/medico.service';
import { CitaService } from '../../services/cita.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardComponent implements OnInit {
  private readonly pacienteService = inject(PacienteService);
  private readonly medicoService = inject(MedicoService);
  private readonly citaService = inject(CitaService);

  readonly totalPacientes = signal<number>(0);
  readonly totalMedicos = signal<number>(0);
  readonly totalCitas = signal<number>(0);
  readonly citasPendientes = signal<number>(0);
  readonly loading = signal<boolean>(true);

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.loading.set(true);
    forkJoin({
      pacientes: this.pacienteService.listarPacientes(),
      medicos: this.medicoService.listarMedicos(),
      citas: this.citaService.listarCitas()
    }).subscribe({
      next: (res) => {
        this.totalPacientes.set(res.pacientes.length);
        this.totalMedicos.set(res.medicos.length);
        this.totalCitas.set(res.citas.length);
        
        const pendientes = res.citas.filter(c => c.estadoCita === 'PENDIENTE').length;
        this.citasPendientes.set(pendientes);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar datos del dashboard:', err);
        this.loading.set(false);
      }
    });
  }
}
