import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PacienteService } from '../../services/paciente.service';
import { CitaService } from '../../services/cita.service';
import { HistoriaClinicaService } from '../../services/historia-clinica.service';
import { RecetaService } from '../../services/receta.service';
import { SeguimientoService } from '../../services/seguimiento.service';
import { MedicoService } from '../../services/medico.service';
import { Paciente } from '../../models/paciente.model';
import { Cita } from '../../models/cita.model';
import { HistoriaClinica } from '../../models/historia-clinica.model';
import { Receta, DetalleReceta } from '../../models/receta.model';
import { Seguimiento } from '../../models/seguimiento.model';
import { Medico } from '../../models/medico.model';

@Component({
  selector: 'app-paciente-ficha',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  providers: [DatePipe],
  templateUrl: './paciente-ficha.html',
  styleUrl: './paciente-ficha.css'
})
export class PacienteFichaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly datePipe = inject(DatePipe);

  private readonly pacienteService = inject(PacienteService);
  private readonly citaService = inject(CitaService);
  private readonly historiaClinicaService = inject(HistoriaClinicaService);
  private readonly recetaService = inject(RecetaService);
  private readonly seguimientoService = inject(SeguimientoService);
  private readonly medicoService = inject(MedicoService);

  readonly pacienteId = signal<number | null>(null);
  readonly loading = signal<boolean>(true);
  readonly activeTab = signal<string>('historia'); // 'historia', 'citas', 'seguimiento', 'recetas'

  // Data signals
  readonly paciente = signal<Paciente | null>(null);
  readonly historiaClinica = signal<HistoriaClinica | null>(null);
  readonly citas = signal<Cita[]>([]);
  readonly citasDisponiblesParaReceta = computed(() =>
    this.citas().filter(cita =>
      cita.estado !== false &&
      (cita.estadoCita ?? '').toUpperCase() !== 'CANCELADA'
    )
  );
  readonly citasDisponiblesParaSeguimiento = computed(() =>
    this.citas().filter(cita =>
      cita.estado !== false &&
      (cita.estadoCita ?? '').toUpperCase() !== 'CANCELADA'
    )
  );
  readonly recetas = signal<Receta[]>([]);
  readonly seguimientos = signal<Seguimiento[]>([]);
  readonly medicos = signal<Medico[]>([]);

  // Historia Clinica Edit Mode
  readonly isHistoriaEditMode = signal<boolean>(false);
  readonly savingHistoria = signal<boolean>(false);

  // Modals signals
  readonly isRecetaModalOpen = signal<boolean>(false);
  readonly isSeguimientoModalOpen = signal<boolean>(false);
  readonly submittingReceta = signal<boolean>(false);
  readonly submittingSeguimiento = signal<boolean>(false);

  // Forms
  historiaForm!: FormGroup;
  recetaForm!: FormGroup;
  medicamentoForm!: FormGroup;
  seguimientoForm!: FormGroup;

  readonly recetaItems = signal<DetalleReceta[]>([]);

  readonly medicamentosFrecuentes = [
    'Paracetamol',
    'Ibuprofeno',
    'Amoxicilina',
    'Amoxicilina + Ácido Clavulánico',
    'Clindamicina',
    'Ketorolaco',
    'Naproxeno',
    'Dexketoprofeno',
    'Eritromicina',
    'Clorhexidina colutorio 0.12%'
  ];

  readonly presentacionesFrecuentes = [
    'Tabletas 500mg',
    'Tabletas 1g',
    'Suspensión 250mg/5ml',
    'Cápsulas 300mg',
    'Cápsulas 500mg',
    'Frasco 15ml',
    'Colutorio 250ml'
  ];

  readonly viasAdministracion = [
    'Oral',
    'Tópica Bucal',
    'Sublingual',
    'Intramuscular',
    'Intravenosa'
  ];

  errorMessage = signal<string>('');


  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) {
      this.router.navigate(['/pacientes']);
      return;
    }

    const id = parseInt(idParam, 10);
    if (isNaN(id)) {
      this.router.navigate(['/pacientes']);
      return;
    }

    this.pacienteId.set(id);
    this.initForms();
    this.cargarDatosFicha();
  }

  private initForms(): void {
    this.historiaForm = this.fb.group({
      antecedentesMedicos: ['', [Validators.maxLength(1000)]],
      antecedentesOdontologicos: ['', [Validators.maxLength(1000)]],
      observacionesGenerales: ['', [Validators.maxLength(1000)]]
    });

    this.recetaForm = this.fb.group({
      idCita: ['', [Validators.required]]
    });

    this.medicamentoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      presentacion: ['', [Validators.maxLength(100)]],
      dosis: ['', [Validators.required, Validators.maxLength(100)]],
      frecuencia: ['', [Validators.required, Validators.maxLength(100)]],
      duracionCantidad: ['', [Validators.required, Validators.min(1)]],
      duracionUnidad: ['días', [Validators.required]],
      via: ['Oral', [Validators.maxLength(50)]],
      indicaciones: ['', [Validators.maxLength(500)]]
    });

    this.seguimientoForm = this.fb.group({
      idCita: ['', [Validators.required]],
      idMedico: [''],
      descripcion: ['', [Validators.required, Validators.maxLength(2000)]]
    });

    this.seguimientoForm.get('idCita')?.valueChanges.subscribe(val => {
      if (val) {
        const citaId = parseInt(val, 10);
        const selectedCita = this.citas().find(c => c.idCita === citaId);
        if (selectedCita && selectedCita.medico) {
          this.seguimientoForm.patchValue({ idMedico: selectedCita.medico.idMedico }, { emitEvent: false });
          this.seguimientoForm.get('idMedico')?.disable({ emitEvent: false });
        } else {
          this.seguimientoForm.get('idMedico')?.enable({ emitEvent: false });
        }
      } else {
        this.seguimientoForm.get('idMedico')?.enable({ emitEvent: false });
        this.seguimientoForm.patchValue({ idMedico: '' }, { emitEvent: false });
      }
    });
  }


  cargarDatosFicha(): void {
    const id = this.pacienteId();
    if (!id) return;

    this.loading.set(true);

    // Cargar paciente general
    this.pacienteService.obtenerPacientePorId(id).subscribe({
      next: (pac) => {
        if (!pac) {
          this.router.navigate(['/pacientes']);
          return;
        }
        this.paciente.set(pac);
        
        // Cargar historia clinica
        this.historiaClinicaService.obtenerHistoriaClinica(id).subscribe({
          next: (hist) => {
            this.historiaClinica.set(hist);
            if (hist) {
              this.historiaForm.patchValue({
                antecedentesMedicos: hist.antecedentesMedicos ?? '',
                antecedentesOdontologicos: hist.antecedentesOdontologicos ?? '',
                observacionesGenerales: hist.observacionesGenerales ?? ''
              });
            }
          },
          error: (err) => console.error('Error al cargar historia clínica:', err)
        });

        // Cargar citas
        this.citaService.listarCitasPorPaciente(id).subscribe({
          next: (cits) => this.citas.set(cits),
          error: (err) => console.error('Error al cargar citas:', err)
        });

        // Cargar recetas
        this.recetaService.listarRecetas(id).subscribe({
          next: (recs) => this.recetas.set(recs),
          error: (err) => console.error('Error al cargar recetas:', err)
        });

        // Cargar seguimientos
        this.seguimientoService.listarSeguimientos(id).subscribe({
          next: (segs) => this.seguimientos.set(segs),
          error: (err) => console.error('Error al cargar seguimientos:', err)
        });

        // Cargar medicos (para los dropdowns de creación)
        this.medicoService.listarMedicos().subscribe({
          next: (meds) => this.medicos.set(meds),
          error: (err) => console.error('Error al cargar médicos:', err)
        });

        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar ficha de paciente:', err);
        this.router.navigate(['/pacientes']);
      }
    });
  }

  setTab(tab: string): void {
    this.activeTab.set(tab);
    this.errorMessage.set('');
  }

  // --- HISTORIA CLINICA ---
  activarEdicionHistoria(): void {
    this.isHistoriaEditMode.set(true);
  }

  cancelarEdicionHistoria(): void {
    this.isHistoriaEditMode.set(false);
    const hist = this.historiaClinica();
    if (hist) {
      this.historiaForm.patchValue({
        antecedentesMedicos: hist.antecedentesMedicos ?? '',
        antecedentesOdontologicos: hist.antecedentesOdontologicos ?? '',
        observacionesGenerales: hist.observacionesGenerales ?? ''
      });
    } else {
      this.historiaForm.reset();
    }
  }

  guardarHistoria(): void {
    const id = this.pacienteId();
    if (!id) return;

    this.savingHistoria.set(true);
    const formVal = this.historiaForm.value;
    const historiaData: HistoriaClinica = {
      antecedentesMedicos: formVal.antecedentesMedicos,
      antecedentesOdontologicos: formVal.antecedentesOdontologicos,
      observacionesGenerales: formVal.observacionesGenerales
    };

    this.historiaClinicaService.guardarHistoriaClinica(id, historiaData).subscribe({
      next: (savedHist) => {
        this.historiaClinica.set(savedHist);
        this.isHistoriaEditMode.set(false);
        this.savingHistoria.set(false);
      },
      error: (err) => {
        console.error('Error al guardar historia clínica:', err);
        this.savingHistoria.set(false);
        alert('Ocurrió un error al guardar la historia clínica.');
      }
    });
  }

  // --- MODALS RECETA ---
  abrirModalReceta(): void {
    this.recetaForm.reset({ idCita: '' });
    this.medicamentoForm.reset({
      nombre: '',
      presentacion: '',
      dosis: '',
      frecuencia: '',
      duracionCantidad: '',
      duracionUnidad: 'días',
      via: 'Oral',
      indicaciones: ''
    });
    this.recetaItems.set([]);
    this.errorMessage.set('');
    this.isRecetaModalOpen.set(true);
  }

  cerrarModalReceta(): void {
    this.isRecetaModalOpen.set(false);
  }

  agregarMedicamento(): void {
    if (this.medicamentoForm.invalid) {
      this.medicamentoForm.markAllAsTouched();
      return;
    }
    const medVal = this.medicamentoForm.value;
    const detalle: DetalleReceta = {
      medicamento: medVal.nombre,
      presentacion: medVal.presentacion || undefined,
      dosis: medVal.dosis,
      frecuencia: medVal.frecuencia,
      duracion: `${medVal.duracionCantidad} ${medVal.duracionUnidad}`,
      viaAdministracion: medVal.via || undefined,
      indicaciones: medVal.indicaciones || undefined,
      estado: true
    };
    
    this.recetaItems.update(items => [...items, detalle]);
    
    // Reset medicamento form with defaults
    this.medicamentoForm.reset({
      nombre: '',
      presentacion: '',
      dosis: '',
      frecuencia: '',
      duracionCantidad: '',
      duracionUnidad: 'días',
      via: 'Oral',
      indicaciones: ''
    });
  }

  eliminarMedicamentoTemporal(index: number): void {
    this.recetaItems.update(items => items.filter((_, i) => i !== index));
  }

  guardarReceta(): void {
    const id = this.pacienteId();
    if (!id) return;

    if (this.recetaForm.invalid) {
      this.recetaForm.markAllAsTouched();
      return;
    }

    if (this.recetaItems().length === 0) {
      this.errorMessage.set('Debe agregar al menos un medicamento a la receta.');
      return;
    }

    this.submittingReceta.set(true);
    this.errorMessage.set('');

    const recetaData: Receta = {
      detalles: this.recetaItems(),
      cita: this.recetaForm.value.idCita ? { idCita: parseInt(this.recetaForm.value.idCita, 10) } as Cita : undefined
    };

    this.recetaService.registrarReceta(id, recetaData).subscribe({
      next: () => {
        this.recetaService.listarRecetas(id).subscribe(recs => this.recetas.set(recs));
        this.cerrarModalReceta();
        this.submittingReceta.set(false);
      },
      error: (err) => {
        console.error('Error al registrar receta:', err);
        this.errorMessage.set('Error al guardar la receta. Verifique los datos.');
        this.submittingReceta.set(false);
      }
    });
  }



  eliminarReceta(idReceta: number): void {
    if (confirm('¿Está seguro de que desea eliminar esta receta?')) {
      const idPac = this.pacienteId();
      this.recetaService.eliminarReceta(idReceta).subscribe({
        next: () => {
          if (idPac) {
            this.recetaService.listarRecetas(idPac).subscribe(recs => this.recetas.set(recs));
          }
        },
        error: (err) => {
          console.error('Error al eliminar receta:', err);
          alert('No se pudo eliminar la receta.');
        }
      });
    }
  }

  // --- MODALS SEGUIMIENTO ---
  abrirModalSeguimiento(): void {
    this.seguimientoForm.reset({ idCita: '', idMedico: '', descripcion: '' });
    this.seguimientoForm.get('idMedico')?.enable({ emitEvent: false });
    this.errorMessage.set('');
    this.isSeguimientoModalOpen.set(true);
  }

  cerrarModalSeguimiento(): void {
    this.isSeguimientoModalOpen.set(false);
  }

  getSelectedCitaMedicoName(): string | null {
    const val = this.seguimientoForm?.get('idCita')?.value;
    if (!val) return null;
    const citaId = parseInt(val, 10);
    const selectedCita = this.citas().find(c => c.idCita === citaId);
    return selectedCita && selectedCita.medico 
      ? `${selectedCita.medico.nombres} ${selectedCita.medico.apellidos}` 
      : null;
  }

  guardarSeguimiento(): void {
    const id = this.pacienteId();
    if (!id) return;

    if (this.seguimientoForm.invalid) {
      this.seguimientoForm.markAllAsTouched();
      return;
    }

    this.submittingSeguimiento.set(true);
    const formVal = this.seguimientoForm.getRawValue();

    const seguimientoData: Seguimiento = {
      descripcion: formVal.descripcion,
      cita: formVal.idCita ? { idCita: parseInt(formVal.idCita, 10) } as Cita : undefined,
      medico: formVal.idMedico ? { idMedico: parseInt(formVal.idMedico, 10) } as Medico : undefined
    };

    this.seguimientoService.registrarSeguimiento(id, seguimientoData).subscribe({
      next: () => {
        this.seguimientoService.listarSeguimientos(id).subscribe(segs => this.seguimientos.set(segs));
        this.cerrarModalSeguimiento();
        this.submittingSeguimiento.set(false);
      },
      error: (err) => {
        console.error('Error al registrar seguimiento:', err);
        this.errorMessage.set('Error al guardar el seguimiento. Verifique los datos.');
        this.submittingSeguimiento.set(false);
      }
    });
  }

  eliminarSeguimiento(idSeguimiento: number): void {
    if (confirm('¿Está seguro de que desea eliminar este registro de seguimiento?')) {
      const idPac = this.pacienteId();
      this.seguimientoService.eliminarSeguimiento(idSeguimiento).subscribe({
        next: () => {
          if (idPac) {
            this.seguimientoService.listarSeguimientos(idPac).subscribe(segs => this.seguimientos.set(segs));
          }
        },
        error: (err) => {
          console.error('Error al eliminar seguimiento:', err);
          alert('No se pudo eliminar el seguimiento.');
        }
      });
    }
  }

  formatDate(dateStr?: string): string {
    if (!dateStr) return '-';
    try {
      const date = new Date(dateStr);
      return this.datePipe.transform(date, 'dd/MM/yyyy HH:mm') || '-';
    } catch {
      return dateStr;
    }
  }
}
