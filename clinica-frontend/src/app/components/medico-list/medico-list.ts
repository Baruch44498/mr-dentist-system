import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MedicoService } from '../../services/medico.service';
import { Medico, TurnoPlanificado } from '../../models/medico.model';

@Component({
  selector: 'app-medico-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './medico-list.html',
  styleUrl: './medico-list.css'
})
export class MedicoListComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly medicoService = inject(MedicoService);

  readonly medicos = signal<Medico[]>([]);
  readonly loading = signal<boolean>(true);
  readonly searchTerm = signal<string>('');
  readonly isModalOpen = signal<boolean>(false);
  readonly submitting = signal<boolean>(false);

  // Planning Signals
  readonly isPlanningOpen = signal<boolean>(false);
  readonly planningDate = signal<string>(''); // YYYY-MM-DD
  readonly savingPlanning = signal<boolean>(false);
  selectedMedico: Medico | null = null;
  selectedHours = new Set<string>();

  readonly availableHourSlots = [
    '08:00', '09:00', '10:00', '11:00', '12:00', '13:00',
    '14:00', '15:00', '16:00', '17:00', '18:00', '19:00', '20:00'
  ];

  // Specialty options
  readonly especialidades = [
    'Odontología General',
    'Ortodoncia',
    'Endodoncia',
    'Odontopediatría',
    'Rehabilitación Oral',
    'Cirugía Bucal'
  ];

  // Shift options
  readonly turnos = [
    'Mañana (08:00 - 13:00)',
    'Tarde (14:00 - 20:00)',
    'Jornada Completa (08:00 - 20:00)'
  ];

  readonly filteredMedicos = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) return this.medicos();
    return this.medicos().filter(m => 
      m.nombres.toLowerCase().includes(term) ||
      m.apellidos.toLowerCase().includes(term) ||
      m.dni.includes(term) ||
      m.especialidad.toLowerCase().includes(term) ||
      m.cop.toLowerCase().includes(term)
    );
  });

  medicoForm!: FormGroup;
  selectedMedicoId: number | null = null;
  errorMessage = signal<string>('');

  ngOnInit(): void {
    this.initForm();
    this.cargarMedicos();
    this.initPlanningDate();
  }

  initForm(): void {
    this.medicoForm = this.fb.group({
      nombres: ['', [Validators.required, Validators.maxLength(100)]],
      apellidos: ['', [Validators.required, Validators.maxLength(100)]],
      dni: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]],
      cop: ['', [Validators.required, Validators.maxLength(15)]],
      especialidad: ['', [Validators.required]],
      horarioTurno: ['', [Validators.required]],
      telefono: ['', [Validators.maxLength(15)]],
      correo: ['', [Validators.email, Validators.maxLength(100)]]
    });
  }

  initPlanningDate(): void {
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    this.planningDate.set(`${yyyy}-${mm}-${dd}`);
  }

  cargarMedicos(): void {
    this.loading.set(true);
    this.medicoService.listarMedicos().subscribe({
      next: (data) => {
        this.medicos.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar médicos:', err);
        this.loading.set(false);
      }
    });
  }

  abrirModalNuevo(): void {
    this.selectedMedicoId = null;
    this.errorMessage.set('');
    this.medicoForm.reset({
      especialidad: '',
      horarioTurno: ''
    });
    this.isModalOpen.set(true);
  }

  abrirModalEditar(medico: Medico): void {
    this.selectedMedicoId = medico.idMedico ?? null;
    this.errorMessage.set('');
    this.medicoForm.patchValue({
      nombres: medico.nombres,
      apellidos: medico.apellidos,
      dni: medico.dni,
      cop: medico.cop,
      especialidad: medico.especialidad,
      horarioTurno: medico.horarioTurno,
      telefono: medico.telefono ?? '',
      correo: medico.correo ?? ''
    });
    this.isModalOpen.set(true);
  }

  cerrarModal(): void {
    this.isModalOpen.set(false);
    this.medicoForm.reset();
  }

  guardarMedico(): void {
    if (this.medicoForm.invalid) {
      this.medicoForm.markAllAsTouched();
      return;
    }

    const formVal = this.medicoForm.value;
    const medicoData: Medico = {
      nombres: formVal.nombres,
      apellidos: formVal.apellidos,
      dni: formVal.dni,
      cop: formVal.cop,
      especialidad: formVal.especialidad,
      horarioTurno: formVal.horarioTurno,
      telefono: formVal.telefono || undefined,
      correo: formVal.correo || undefined
    };

    this.submitting.set(true);
    this.errorMessage.set('');

    const operation = this.selectedMedicoId 
      ? this.medicoService.actualizarMedico(this.selectedMedicoId, medicoData)
      : this.medicoService.registrarMedico(medicoData);

    operation.subscribe({
      next: () => {
        this.cargarMedicos();
        this.cerrarModal();
        this.submitting.set(false);
      },
      error: (err) => {
        console.error('Error al guardar médico:', err);
        this.errorMessage.set('Ocurrió un error al guardar los datos. Verifique si el COP, DNI o Correo ya están registrados.');
        this.submitting.set(false);
      }
    });
  }

  eliminarMedico(id: number): void {
    if (confirm('¿Está seguro de que desea eliminar este médico?')) {
      this.medicoService.eliminarMedico(id).subscribe({
        next: () => {
          this.cargarMedicos();
        },
        error: (err) => {
          console.error('Error al eliminar médico:', err);
          alert('No se pudo eliminar el médico.');
        }
      });
    }
  }

  onSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchTerm.set(input.value);
  }

  // --- PLANNING SHIFTS WORKFLOW ---
  abrirPlanificacion(medico: Medico): void {
    this.selectedMedico = medico;
    this.selectedHours.clear();
    this.isPlanningOpen.set(true);
    this.cargarPlanificacionDia();
  }

  cerrarPlanificacion(): void {
    this.isPlanningOpen.set(false);
    this.selectedMedico = null;
    this.selectedHours.clear();
  }

  onPlanningDateChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.planningDate.set(input.value);
    this.cargarPlanificacionDia();
  }

  cargarPlanificacionDia(): void {
    if (!this.selectedMedico || !this.selectedMedico.idMedico) return;
    
    const id = this.selectedMedico.idMedico;
    const fecha = this.planningDate();

    this.medicoService.obtenerPlanificacion(id, fecha).subscribe({
      next: (data) => {
        this.selectedHours.clear();
        if (data && data.length > 0) {
          // Usar la planificación cargada de BD
          data.forEach(t => {
            if (t.activo) {
              // Cortar a formato HH:mm
              const horaHhmm = t.hora.substring(0, 5);
              this.selectedHours.add(horaHhmm);
            }
          });
        } else {
          // Si no hay planificación previa, usar el turno base por defecto
          this.cargarTurnoPorDefecto();
        }
      },
      error: (err) => {
        console.error('Error al cargar planificación:', err);
        this.cargarTurnoPorDefecto();
      }
    });
  }

  cargarTurnoPorDefecto(): void {
    if (!this.selectedMedico) return;
    const turno = this.selectedMedico.horarioTurno.toLowerCase();
    this.selectedHours.clear();

    if (turno.includes('mañana')) {
      this.selectedHours.add('08:00');
      this.selectedHours.add('09:00');
      this.selectedHours.add('10:00');
      this.selectedHours.add('11:00');
      this.selectedHours.add('12:00');
      this.selectedHours.add('13:00');
    } else if (turno.includes('tarde')) {
      this.selectedHours.add('14:00');
      this.selectedHours.add('15:00');
      this.selectedHours.add('16:00');
      this.selectedHours.add('17:00');
      this.selectedHours.add('18:00');
      this.selectedHours.add('19:00');
      this.selectedHours.add('20:00');
    } else {
      // Jornada Completa
      this.availableHourSlots.forEach(h => this.selectedHours.add(h));
    }
  }

  toggleHour(hour: string): void {
    if (this.selectedHours.has(hour)) {
      this.selectedHours.delete(hour);
    } else {
      this.selectedHours.add(hour);
    }
  }

  aplicarPreset(tipo: 'mañana' | 'tarde' | 'completa' | 'limpiar'): void {
    this.selectedHours.clear();
    if (tipo === 'mañana') {
      this.selectedHours.add('08:00');
      this.selectedHours.add('09:00');
      this.selectedHours.add('10:00');
      this.selectedHours.add('11:00');
      this.selectedHours.add('12:00');
      this.selectedHours.add('13:00');
    } else if (tipo === 'tarde') {
      this.selectedHours.add('14:00');
      this.selectedHours.add('15:00');
      this.selectedHours.add('16:00');
      this.selectedHours.add('17:00');
      this.selectedHours.add('18:00');
      this.selectedHours.add('19:00');
      this.selectedHours.add('20:00');
    } else if (tipo === 'completa') {
      this.availableHourSlots.forEach(h => this.selectedHours.add(h));
    }
  }

  guardarPlanificacion(): void {
    if (!this.selectedMedico || !this.selectedMedico.idMedico) return;

    this.savingPlanning.set(true);
    const id = this.selectedMedico.idMedico;
    const fecha = this.planningDate();

    // Enviar las horas en formato HH:mm:ss o HH:mm
    const horas = Array.from(this.selectedHours).map(h => `${h}:00`);

    this.medicoService.guardarPlanificacion(id, fecha, horas).subscribe({
      next: () => {
        alert('Planificación de turnos guardada correctamente para el día: ' + fecha);
        this.savingPlanning.set(false);
        this.cerrarPlanificacion();
      },
      error: (err) => {
        console.error('Error al guardar planificacion:', err);
        alert('Hubo un error al guardar la planificación.');
        this.savingPlanning.set(false);
      }
    });
  }
}
