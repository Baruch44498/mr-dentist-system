package com.mrdentist.clinica_backend.service;

import com.mrdentist.clinica_backend.entity.Cita;
import com.mrdentist.clinica_backend.entity.Medico;
import com.mrdentist.clinica_backend.entity.Paciente;
import com.mrdentist.clinica_backend.entity.Seguimiento;
import com.mrdentist.clinica_backend.repository.CitaRepository;
import com.mrdentist.clinica_backend.repository.MedicoRepository;
import com.mrdentist.clinica_backend.repository.PacienteRepository;
import com.mrdentist.clinica_backend.repository.SeguimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SeguimientoService {

    @Autowired
    private SeguimientoRepository seguimientoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    public List<Seguimiento> listarPorPaciente(Long pacienteId) {
        return seguimientoRepository.findByPacienteIdPacienteAndEstadoTrueOrderByFechaSeguimientoDesc(pacienteId);
    }

    public Seguimiento guardar(Long pacienteId, Seguimiento seguimiento) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .filter(Paciente::getEstado)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        seguimiento.setPaciente(paciente);

        if (seguimiento.getCita() == null || seguimiento.getCita().getIdCita() == null) {
            throw new IllegalArgumentException("La cita es obligatoria para registrar notas de evolución.");
        }

        Cita cita = citaRepository.findById(seguimiento.getCita().getIdCita())
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        if (!cita.getPaciente().getIdPaciente().equals(pacienteId)) {
            throw new IllegalArgumentException("La cita seleccionada no pertenece al paciente actual.");
        }

        if (Boolean.FALSE.equals(cita.getEstado()) || "CANCELADA".equalsIgnoreCase(cita.getEstadoCita())) {
            throw new IllegalArgumentException("La cita está cancelada o inactiva y no se puede asociar.");
        }

        seguimiento.setCita(cita);
        if (cita.getMedico() != null) {
            seguimiento.setMedico(cita.getMedico());
        } else {
            seguimiento.setMedico(null);
        }

        seguimiento.setEstado(true);
        return seguimientoRepository.save(seguimiento);
    }

    public Seguimiento actualizar(Long idSeguimiento, Seguimiento seguimientoActualizado) {
        return seguimientoRepository.findById(idSeguimiento).map(seguimiento -> {
            seguimiento.setDescripcion(seguimientoActualizado.getDescripcion());
            
            if (seguimientoActualizado.getCita() == null || seguimientoActualizado.getCita().getIdCita() == null) {
                throw new IllegalArgumentException("La cita es obligatoria para actualizar el seguimiento.");
            }

            Cita cita = citaRepository.findById(seguimientoActualizado.getCita().getIdCita())
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            if (!cita.getPaciente().getIdPaciente().equals(seguimiento.getPaciente().getIdPaciente())) {
                throw new IllegalArgumentException("La cita no pertenece al paciente actual.");
            }

            if (Boolean.FALSE.equals(cita.getEstado()) || "CANCELADA".equalsIgnoreCase(cita.getEstadoCita())) {
                throw new IllegalArgumentException("La cita está cancelada o inactiva.");
            }

            seguimiento.setCita(cita);
            if (cita.getMedico() != null) {
                seguimiento.setMedico(cita.getMedico());
            } else {
                seguimiento.setMedico(null);
            }

            return seguimientoRepository.save(seguimiento);
        }).orElse(null);
    }

    public void eliminarLogico(Long idSeguimiento) {
        seguimientoRepository.findById(idSeguimiento).ifPresent(seguimiento -> {
            seguimiento.setEstado(false);
            seguimientoRepository.save(seguimiento);
        });
    }
}
