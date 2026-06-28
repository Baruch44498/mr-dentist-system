import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CitaService } from '../../services/cita.service';
import { Cita } from '../../models/cita.model';
import { PacienteService } from '../../services/paciente.service';
import { Paciente } from '../../models/paciente.model';
import { MedicoService } from '../../services/medico.service';
import { Medico } from '../../models/medico.model';
import { EspecialidadService } from '../../services/especialidad.service';
import { Especialidad } from '../../models/especialidad.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-cita-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cita-list.html',
  styleUrl: './cita-list.css'
})
export class CitaListComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly citaService = inject(CitaService);
  private readonly pacienteService = inject(PacienteService);
  private readonly medicoService = inject(MedicoService);
  private readonly especialidadService = inject(EspecialidadService);

  readonly citas = signal<Cita[]>([]);
  readonly pacientes = signal<Paciente[]>([]);
  readonly medicos = signal<Medico[]>([]); // Doctors filtered by specialty
  readonly disponiblesSlots = signal<string[]>([]);
  readonly selectedHour = signal<string>('');

  readonly loading = signal<boolean>(true);
  readonly filterTerm = signal<string>('');
  readonly isBookingModalOpen = signal<boolean>(false);
  readonly isCancelModalOpen = signal<boolean>(false);
  readonly submitting = signal<boolean>(false);
  readonly canceling = signal<boolean>(false);

  // Dynamic specialties loaded from backend
  readonly especialidades = signal<Especialidad[]>([]);

  // Dynamic filter for active appointments
  readonly filteredCitas = computed(() => {
    const term = this.filterTerm().toLowerCase().trim();
    if (!term) return this.citas();
    return this.citas().filter(c => 
      c.paciente.nombres.toLowerCase().includes(term) ||
      c.paciente.apellidos.toLowerCase().includes(term) ||
      c.medico.nombres.toLowerCase().includes(term) ||
      c.medico.apellidos.toLowerCase().includes(term) ||
      (c.medico.especialidad?.nombre || '').toLowerCase().includes(term) ||
      (c.estadoCita && c.estadoCita.toLowerCase().includes(term))
    );
  });

  citaForm!: FormGroup;
  cancelForm!: FormGroup;
  selectedCitaId: number | null = null;
  errorMessage = signal<string>('');

  ngOnInit(): void {
    this.initForms();
    this.cargarDatosBase();
  }

  initForms(): void {
    this.citaForm = this.fb.group({
      idPaciente: ['', Validators.required],
      especialidad: ['', Validators.required],
      idMedico: [{ value: '', disabled: true }, Validators.required],
      fecha: ['', Validators.required],
      motivo: ['', Validators.maxLength(250)]
    });

    this.cancelForm = this.fb.group({
      motivoCancelacion: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(250)]]
    });

    // Watch specialty change to filter doctors
    this.citaForm.get('especialidad')?.valueChanges.subscribe(esp => {
      this.onEspecialidadCambiada(esp);
    });

    // Watch doctor or date change to check available slots
    this.citaForm.get('idMedico')?.valueChanges.subscribe(() => this.consultarDisponibilidad());
    this.citaForm.get('fecha')?.valueChanges.subscribe(() => this.consultarDisponibilidad());
  }

  cargarDatosBase(): void {
    this.loading.set(true);
    forkJoin({
      citas: this.citaService.listarCitas(),
      pacientes: this.pacienteService.listarPacientes(),
      especialidades: this.especialidadService.listarEspecialidades()
    }).subscribe({
      next: (res) => {
        this.citas.set(res.citas);
        this.pacientes.set(res.pacientes);
        this.especialidades.set(res.especialidades.filter(e => e.estado !== false));
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar datos base de citas:', err);
        this.loading.set(false);
      }
    });
  }

  onEspecialidadCambiada(especialidad: string): void {
    const medicoCtrl = this.citaForm.get('idMedico');
    medicoCtrl?.reset('');
    this.disponiblesSlots.set([]);
    this.selectedHour.set('');

    if (!especialidad) {
      medicoCtrl?.disable();
      this.medicos.set([]);
      return;
    }

    this.medicoService.listarMedicos(especialidad).subscribe({
      next: (data) => {
        this.medicos.set(data);
        medicoCtrl?.enable();
      },
      error: (err) => {
        console.error('Error al listar médicos por especialidad:', err);
      }
    });
  }

  consultarDisponibilidad(): void {
    const idMedico = this.citaForm.get('idMedico')?.value;
    const fecha = this.citaForm.get('fecha')?.value;
    this.disponiblesSlots.set([]);
    this.selectedHour.set('');

    if (!idMedico || !fecha) return;

    this.medicoService.obtenerDisponibilidad(+idMedico, fecha).subscribe({
      next: (slots) => {
        // Formatear HH:mm:ss a HH:mm
        this.disponiblesSlots.set(slots.map(s => s.substring(0, 5)));
      },
      error: (err) => {
        console.error('Error al obtener disponibilidad:', err);
      }
    });
  }

  seleccionarHora(hora: string): void {
    this.selectedHour.set(hora);
  }

  abrirBookingModal(): void {
    this.errorMessage.set('');
    this.citaForm.reset({
      idPaciente: '',
      especialidad: '',
      idMedico: '',
      fecha: '',
      motivo: ''
    });
    this.disponiblesSlots.set([]);
    this.selectedHour.set('');
    this.isBookingModalOpen.set(true);
  }

  cerrarBookingModal(): void {
    this.isBookingModalOpen.set(false);
    this.citaForm.reset();
  }

  guardarCita(): void {
    if (this.citaForm.invalid) {
      this.citaForm.markAllAsTouched();
      return;
    }

    if (!this.selectedHour()) {
      this.errorMessage.set('Debe seleccionar un horario disponible para la cita.');
      return;
    }

    const val = this.citaForm.value;
    const paciente = this.pacientes().find(p => p.idPaciente === +val.idPaciente)!;
    const medico = this.medicos().find(m => m.idMedico === +val.idMedico)!;
    
    // Unir Fecha y Hora en formato LocalDateTime ISO: YYYY-MM-DDT_HH:MM:00
    const fechaHora = `${val.fecha}T${this.selectedHour()}:00`;

    const nuevaCita: Cita = {
      paciente,
      medico,
      fechaHora,
      motivo: val.motivo || undefined,
      estadoCita: 'PENDIENTE'
    };

    this.submitting.set(true);
    this.errorMessage.set('');

    this.citaService.registrarCita(nuevaCita).subscribe({
      next: () => {
        this.citaService.listarCitas().subscribe(citas => {
          this.citas.set(citas);
        });
        this.cerrarBookingModal();
        this.submitting.set(false);
      },
      error: (err) => {
        console.error('Error al registrar cita:', err);
        this.errorMessage.set('Error al guardar la cita. Verifique que el horario no haya sido tomado.');
        this.submitting.set(false);
      }
    });
  }

  // --- CANCELLATION FLOW ---
  abrirCancelarModal(cita: Cita): void {
    this.selectedCitaId = cita.idCita ?? null;
    this.cancelForm.reset();
    this.isCancelModalOpen.set(true);
  }

  cerrarCancelarModal(): void {
    this.isCancelModalOpen.set(false);
    this.cancelForm.reset();
    this.selectedCitaId = null;
  }

  confirmarCancelacion(): void {
    if (this.cancelForm.invalid || !this.selectedCitaId) {
      this.cancelForm.markAllAsTouched();
      return;
    }

    const motivo = this.cancelForm.value.motivoCancelacion;
    this.canceling.set(true);

    this.citaService.cancelarCita(this.selectedCitaId, motivo).subscribe({
      next: () => {
        // Recargar citas
        this.citaService.listarCitas().subscribe(data => {
          this.citas.set(data);
        });
        alert('Cita cancelada con éxito. Se ha impreso la notificación de correo en el servidor backend.');
        this.cerrarCancelarModal();
        this.canceling.set(false);
      },
      error: (err) => {
        console.error('Error al cancelar cita:', err);
        alert('Hubo un error al cancelar la cita.');
        this.canceling.set(false);
      }
    });
  }

  onFilterChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.filterTerm.set(input.value);
  }

  obtenerNombreEspecialidad(cita: any): string {
    if (!cita) return 'Sin especialidad';

    const espMedico = cita.medico?.especialidad;
    if (espMedico) {
      if (typeof espMedico === 'string') return espMedico;
      if (espMedico.nombre) return espMedico.nombre;
    }

    const espCita = cita.especialidad;
    if (espCita) {
      if (typeof espCita === 'string') return espCita;
      if (espCita.nombre) return espCita.nombre;
    }

    return 'Sin especialidad';
  }
}
