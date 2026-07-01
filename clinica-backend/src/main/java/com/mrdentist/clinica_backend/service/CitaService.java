package com.mrdentist.clinica_backend.service;

import com.mrdentist.clinica_backend.entity.Cita;
import com.mrdentist.clinica_backend.entity.EstadoCita;
import com.mrdentist.clinica_backend.entity.Medico;
import com.mrdentist.clinica_backend.entity.Paciente;
import com.mrdentist.clinica_backend.exception.BadRequestException;
import com.mrdentist.clinica_backend.exception.ConflictException;
import com.mrdentist.clinica_backend.exception.ResourceNotFoundException;
import com.mrdentist.clinica_backend.repository.CitaRepository;
import com.mrdentist.clinica_backend.repository.MedicoRepository;
import com.mrdentist.clinica_backend.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class CitaService {
    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private EmailService emailService;

    public List<Cita> listarActivas() {
        return citaRepository.findByEstadoTrue();
    }

    public Cita guardar(Cita cita) {
        validarCita(cita, null);
        cita.setEstadoCita(EstadoCita.PENDIENTE);
        cita.setEstado(true);
        return citaRepository.save(cita);
    }

    public Cita actualizar(Long id, Cita citaActualizada) {
        return citaRepository.findById(id).map(cita -> {
            validarCita(citaActualizada, id);
            
            cita.setPaciente(citaActualizada.getPaciente());
            cita.setMedico(citaActualizada.getMedico());
            cita.setFechaHora(citaActualizada.getFechaHora());
            cita.setMotivo(citaActualizada.getMotivo());
            cita.setEstadoCita(citaActualizada.getEstadoCita());
            return citaRepository.save(cita);
        }).orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + id));
    }

    public void eliminarLogico(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + id));
        cita.setEstado(false);
        citaRepository.save(cita);
    }

    public Cita cancelarCita(Long id, String motivo) {
        return citaRepository.findById(id).map(cita -> {
            cita.setEstadoCita(EstadoCita.CANCELADA);
            Cita citaCancelada = citaRepository.save(cita);
            try {
                emailService.enviarCorreoCancelacion(citaCancelada, motivo);
            } catch (Exception e) {
                System.out.println("[CitaService] Error no crítico al enviar correo: " + e.getMessage());
            }
            return citaCancelada;
        }).orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + id));
    }

    private void validarCita(Cita cita, Long excludeCitaId) {
        if (cita.getFechaHora() == null) {
            throw new BadRequestException("La fecha y hora de la cita es obligatoria.");
        }

        if (cita.getPaciente() == null || cita.getPaciente().getIdPaciente() == null) {
            throw new BadRequestException("El paciente es obligatorio.");
        }
        Paciente paciente = pacienteRepository.findById(cita.getPaciente().getIdPaciente())
                .orElseThrow(() -> new ResourceNotFoundException("El paciente especificado no existe."));
        if (!Boolean.TRUE.equals(paciente.getEstado())) {
            throw new BadRequestException("El paciente no esta activo.");
        }

        if (cita.getMedico() == null || cita.getMedico().getIdMedico() == null) {
            throw new BadRequestException("El medico es obligatorio.");
        }
        Medico medico = medicoRepository.findById(cita.getMedico().getIdMedico())
                .orElseThrow(() -> new ResourceNotFoundException("El medico especificado no existe."));
        if (!Boolean.TRUE.equals(medico.getEstado())) {
            throw new BadRequestException("El medico no esta activo.");
        }

        LocalDate fecha = cita.getFechaHora().toLocalDate();
        LocalTime hora = cita.getFechaHora().toLocalTime();

        // Si se esta actualizando y el medico y fechaHora son identicos a los actuales, omitimos validaciones de disponibilidad
        boolean medicoOFechaHoraCambio = true;
        if (excludeCitaId != null) {
            Cita citaExistente = citaRepository.findById(excludeCitaId).orElse(null);
            if (citaExistente != null 
                    && citaExistente.getMedico().getIdMedico().equals(medico.getIdMedico()) 
                    && citaExistente.getFechaHora().equals(cita.getFechaHora())) {
                medicoOFechaHoraCambio = false;
            }
        }

        if (medicoOFechaHoraCambio) {
            // Verificar disponibilidad en MedicoService
            List<LocalTime> disponibles = medicoService.obtenerDisponibilidad(medico.getIdMedico(), fecha);
            LocalTime horaNormalizada = hora.withSecond(0).withNano(0);
            boolean disponible = disponibles.stream()
                    .anyMatch(h -> h.withSecond(0).withNano(0).equals(horaNormalizada));
            
            if (!disponible) {
                throw new ConflictException("El medico no tiene disponibilidad para la fecha y hora seleccionadas o el horario ya esta ocupado.");
            }

            // Validar que no exista otra cita activa
            boolean existeConflicto;
            if (excludeCitaId != null) {
                existeConflicto = citaRepository.existsActiveCitaForMedicoAtFechaHoraExcludeCita(
                        medico.getIdMedico(), cita.getFechaHora(), excludeCitaId, EstadoCita.CANCELADA);
            } else {
                existeConflicto = citaRepository.existsActiveCitaForMedicoAtFechaHora(
                        medico.getIdMedico(), cita.getFechaHora(), EstadoCita.CANCELADA);
            }
            if (existeConflicto) {
                throw new ConflictException("Ya existe una cita programada para este medico en la fecha y hora seleccionadas.");
            }
        }

        // Asegurar consistencia de referencias cargadas desde base de datos
        cita.setPaciente(paciente);
        cita.setMedico(medico);
    }
}
