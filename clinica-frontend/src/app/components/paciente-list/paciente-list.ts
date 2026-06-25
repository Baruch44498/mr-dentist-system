import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PacienteService } from '../../services/paciente.service';
import { Paciente } from '../../models/paciente.model';

@Component({
  selector: 'app-paciente-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './paciente-list.html',
  styleUrl: './paciente-list.css'
})
export class PacienteListComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly pacienteService = inject(PacienteService);

  readonly pacientes = signal<Paciente[]>([]);
  readonly loading = signal<boolean>(true);
  readonly searchTerm = signal<string>('');
  readonly isModalOpen = signal<boolean>(false);
  readonly submitting = signal<boolean>(false);

  // Compute filtered patients list dynamically
  readonly filteredPacientes = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) return this.pacientes();
    return this.pacientes().filter(p => 
      p.nombres.toLowerCase().includes(term) ||
      p.apellidos.toLowerCase().includes(term) ||
      p.dni.includes(term) ||
      (p.correo && p.correo.toLowerCase().includes(term))
    );
  });

  patientForm!: FormGroup;
  selectedPacienteId: number | null = null;
  errorMessage = signal<string>('');

  ngOnInit(): void {
    this.initForm();
    this.cargarPacientes();
  }

  initForm(): void {
    this.patientForm = this.fb.group({
      nombres: ['', [Validators.required, Validators.maxLength(100)]],
      apellidos: ['', [Validators.required, Validators.maxLength(100)]],
      dni: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]],
      telefono: ['', [Validators.maxLength(15)]],
      correo: ['', [Validators.email, Validators.maxLength(100)]]
    });
  }

  cargarPacientes(): void {
    this.loading.set(true);
    this.pacienteService.listarPacientes().subscribe({
      next: (data) => {
        this.pacientes.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar pacientes:', err);
        this.loading.set(false);
      }
    });
  }

  abrirModalNuevo(): void {
    this.selectedPacienteId = null;
    this.errorMessage.set('');
    this.patientForm.reset();
    this.isModalOpen.set(true);
  }

  abrirModalEditar(paciente: Paciente): void {
    this.selectedPacienteId = paciente.idPaciente ?? null;
    this.errorMessage.set('');
    this.patientForm.patchValue({
      nombres: paciente.nombres,
      apellidos: paciente.apellidos,
      dni: paciente.dni,
      telefono: paciente.telefono ?? '',
      correo: paciente.correo ?? ''
    });
    this.isModalOpen.set(true);
  }

  cerrarModal(): void {
    this.isModalOpen.set(false);
    this.patientForm.reset();
  }

  guardarPaciente(): void {
    if (this.patientForm.invalid) {
      this.patientForm.markAllAsTouched();
      return;
    }

    const formVal = this.patientForm.value;
    const pacienteData: Paciente = {
      nombres: formVal.nombres,
      apellidos: formVal.apellidos,
      dni: formVal.dni,
      telefono: formVal.telefono || undefined,
      correo: formVal.correo || undefined
    };

    this.submitting.set(true);
    this.errorMessage.set('');

    const operation = this.selectedPacienteId 
      ? this.pacienteService.actualizarPaciente(this.selectedPacienteId, pacienteData)
      : this.pacienteService.registrarPaciente(pacienteData);

    operation.subscribe({
      next: () => {
        this.cargarPacientes();
        this.cerrarModal();
        this.submitting.set(false);
      },
      error: (err) => {
        console.error('Error al guardar paciente:', err);
        this.errorMessage.set('Ocurrió un error al guardar los datos. Verifique si el DNI o Correo ya están registrados.');
        this.submitting.set(false);
      }
    });
  }

  eliminarPaciente(id: number): void {
    if (confirm('¿Está seguro de que desea eliminar este paciente?')) {
      this.pacienteService.eliminarPaciente(id).subscribe({
        next: () => {
          this.cargarPacientes();
        },
        error: (err) => {
          console.error('Error al eliminar paciente:', err);
          alert('No se pudo eliminar el paciente.');
        }
      });
    }
  }

  onSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchTerm.set(input.value);
  }
}
