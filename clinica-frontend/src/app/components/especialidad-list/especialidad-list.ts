import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Especialidad } from '../../models/especialidad.model';
import { EspecialidadService } from '../../services/especialidad.service';

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
  readonly searchTerm = signal<string>('');
  readonly isModalOpen = signal<boolean>(false);
  readonly submitting = signal<boolean>(false);
  readonly errorMessage = signal<string>('');

  especialidadForm!: FormGroup;
  selectedEspecialidadId: number | null = null;

  readonly filteredEspecialidades = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const data = this.especialidades();

    if (!term) return data;

    return data.filter((especialidad) =>
      especialidad.nombre.toLowerCase().includes(term) ||
      (especialidad.descripcion ?? '').toLowerCase().includes(term)
    );
  });

  ngOnInit(): void {
    this.initForm();
    this.cargarEspecialidades();
  }

  initForm(): void {
    this.especialidadForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(80)]],
      descripcion: ['', [Validators.maxLength(250)]],
      estado: [true]
    });
  }

  cargarEspecialidades(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.especialidadService.listarEspecialidades().subscribe({
      next: (data) => {
        this.especialidades.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar especialidades:', err);
        this.errorMessage.set('No se pudieron cargar las especialidades odontológicas.');
        this.loading.set(false);
      }
    });
  }

  abrirModalNuevo(): void {
    this.selectedEspecialidadId = null;
    this.errorMessage.set('');
    this.especialidadForm.reset({
      nombre: '',
      descripcion: '',
      estado: true
    });
    this.isModalOpen.set(true);
  }

  abrirModalEditar(especialidad: Especialidad): void {
    this.selectedEspecialidadId = this.obtenerId(especialidad) ?? null;
    this.errorMessage.set('');
    this.especialidadForm.patchValue({
      nombre: especialidad.nombre,
      descripcion: especialidad.descripcion ?? '',
      estado: especialidad.estado ?? true
    });
    this.isModalOpen.set(true);
  }

  cerrarModal(): void {
    this.isModalOpen.set(false);
    this.especialidadForm.reset({
      nombre: '',
      descripcion: '',
      estado: true
    });
    this.selectedEspecialidadId = null;
  }

  guardarEspecialidad(): void {
    if (this.especialidadForm.invalid) {
      this.especialidadForm.markAllAsTouched();
      return;
    }

    const formVal = this.especialidadForm.value;
    const especialidadData: Especialidad = {
      nombre: formVal.nombre.trim(),
      descripcion: formVal.descripcion?.trim() || undefined,
      estado: formVal.estado ?? true
    };

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
        this.errorMessage.set('No se pudo guardar la especialidad. Revise los datos e intente nuevamente.');
        this.submitting.set(false);
      }
    });
  }

  eliminarEspecialidad(especialidad: Especialidad): void {
    const id = this.obtenerId(especialidad);

    if (!id) {
      alert('No se pudo identificar la especialidad seleccionada.');
      return;
    }

    if (!confirm(`¿Está seguro de que desea eliminar la especialidad "${especialidad.nombre}"?`)) {
      return;
    }

    this.especialidadService.eliminarEspecialidad(id).subscribe({
      next: () => {
        this.cargarEspecialidades();
      },
      error: (err) => {
        console.error('Error al eliminar especialidad:', err);
        alert('No se pudo eliminar la especialidad.');
      }
    });
  }

  onSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchTerm.set(input.value);
  }

  obtenerId(especialidad: Especialidad): number | undefined {
    return especialidad.idEspecialidad ?? especialidad.id;
  }
}
