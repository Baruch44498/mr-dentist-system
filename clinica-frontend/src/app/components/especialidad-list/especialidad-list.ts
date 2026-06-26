import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EspecialidadService } from '../../services/especialidad.service';
import { Especialidad } from '../../models/especialidad.model';

@Component({
  selector: 'app-especialidad-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './especialidad-list.html',
  styleUrl: './especialidad-list.css'
})
export class EspecialidadListComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly especialidadService = inject(EspecialidadService);

  readonly especialidades = signal<Especialidad[]>([]);
  readonly loading = signal<boolean>(true);
  readonly apiError = signal<boolean>(false);
  readonly searchTerm = signal<string>('');
  readonly isModalOpen = signal<boolean>(false);
  readonly submitting = signal<boolean>(false);

  // Filtrado reactivo dinámico de especialidades
  readonly filteredEspecialidades = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) return this.especialidades();
    return this.especialidades().filter(e => 
      e.nombre.toLowerCase().includes(term) ||
      (e.descripcion && e.descripcion.toLowerCase().includes(term))
    );
  });

  especialidadForm!: FormGroup;
  selectedEspecialidadId: number | null = null;
  errorMessage = signal<string>('');

  ngOnInit(): void {
    this.initForm();
    this.cargarEspecialidades();
  }

  initForm(): void {
    this.especialidadForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(50)]],
      descripcion: ['', [Validators.maxLength(250)]]
    });
  }

  cargarEspecialidades(): void {
    this.loading.set(true);
    this.apiError.set(false);
    this.especialidadService.listarEspecialidades().subscribe({
      next: (data) => {
        this.especialidades.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar especialidades del backend:', err);
        this.apiError.set(true);
        this.loading.set(false);
      }
    });
  }

  abrirModalNuevo(): void {
    this.selectedEspecialidadId = null;
    this.errorMessage.set('');
    this.especialidadForm.reset();
    this.isModalOpen.set(true);
  }

  abrirModalEditar(especialidad: Especialidad): void {
    this.selectedEspecialidadId = especialidad.idEspecialidad ?? especialidad.id ?? null;
    this.errorMessage.set('');
    this.especialidadForm.patchValue({
      nombre: especialidad.nombre,
      descripcion: especialidad.descripcion ?? ''
    });
    this.isModalOpen.set(true);
  }

  cerrarModal(): void {
    this.isModalOpen.set(false);
    this.especialidadForm.reset();
  }

  guardarEspecialidad(): void {
    if (this.especialidadForm.invalid) {
      this.especialidadForm.markAllAsTouched();
      return;
    }

    const formVal = this.especialidadForm.value;
    const especialidadData: Especialidad = {
      nombre: formVal.nombre,
      descripcion: formVal.descripcion || undefined
    };

    // Asignar ID si es edición para resiliencia
    if (this.selectedEspecialidadId) {
      especialidadData.idEspecialidad = this.selectedEspecialidadId;
      especialidadData.id = this.selectedEspecialidadId;
    }

    this.submitting.set(true);
    this.errorMessage.set('');

    const operation = this.selectedEspecialidadId 
      ? this.especialidadService.actualizarEspecialidad(this.selectedEspecialidadId, especialidadData)
      : this.especialidadService.registrarEspecialidad(especialidadData);

    operation.subscribe({
      next: () => {
        this.cargarEspecialidades();
        this.cerrarModal();
        this.submitting.set(false);
      },
      error: (err) => {
        console.error('Error al guardar especialidad:', err);
        this.errorMessage.set('Ocurrió un error al guardar los datos. Verifique si la especialidad ya existe.');
        this.submitting.set(false);
      }
    });
  }

  eliminarEspecialidad(especialidad: Especialidad): void {
    const id = especialidad.idEspecialidad ?? especialidad.id;
    if (!id) return;

    if (confirm(`¿Está seguro de que desea eliminar la especialidad "${especialidad.nombre}"?`)) {
      this.especialidadService.eliminarEspecialidad(id).subscribe({
        next: () => {
          this.cargarEspecialidades();
        },
        error: (err) => {
          console.error('Error al eliminar especialidad:', err);
          alert('No se pudo eliminar la especialidad. Podría estar vinculada a un médico.');
        }
      });
    }
  }

  onSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchTerm.set(input.value);
  }
}
