package com.mrdentist.clinica_backend.service;

import com.mrdentist.clinica_backend.entity.Medico;
import com.mrdentist.clinica_backend.entity.TurnoPlanificado;
import com.mrdentist.clinica_backend.entity.Cita;
import com.mrdentist.clinica_backend.repository.MedicoRepository;
import com.mrdentist.clinica_backend.repository.TurnoPlanificadoRepository;
import com.mrdentist.clinica_backend.repository.CitaRepository;
import com.mrdentist.clinica_backend.repository.EspecialidadRepository;
import com.mrdentist.clinica_backend.entity.Especialidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MedicoService
{
    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private TurnoPlanificadoRepository turnoPlanificadoRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    public List<Medico> listarActivos() {
        return medicoRepository.findByEstadoTrue();
    }

    public List<Medico> listarPorEspecialidad(String especialidad) {
        return medicoRepository.findByEspecialidadNombreContainingAndEstadoTrue(especialidad);
    }

    public Medico guardar(Medico medico) {
        if (medico.getEspecialidad() != null && medico.getEspecialidad().getIdEspecialidad() != null) {
            Especialidad esp = especialidadRepository.findById(medico.getEspecialidad().getIdEspecialidad()).orElse(null);
            if (esp == null) {
                return null;
            }
            medico.setEspecialidad(esp);
        }
        return medicoRepository.save(medico);
    }

    public Medico actualizar(Long id, Medico medicoActualizado) {
        return medicoRepository.findById(id).map(medico -> {
            medico.setNombres(medicoActualizado.getNombres());
            medico.setApellidos(medicoActualizado.getApellidos());
            medico.setDni(medicoActualizado.getDni());
            medico.setCop(medicoActualizado.getCop());
            if (medicoActualizado.getEspecialidad() != null && medicoActualizado.getEspecialidad().getIdEspecialidad() != null) {
                Especialidad esp = especialidadRepository.findById(medicoActualizado.getEspecialidad().getIdEspecialidad()).orElse(null);
                if (esp == null) {
                    return null;
                }
                medico.setEspecialidad(esp);
            }
            medico.setHorarioTurno(medicoActualizado.getHorarioTurno());
            medico.setTelefono(medicoActualizado.getTelefono());
            medico.setCorreo(medicoActualizado.getCorreo());
            return medicoRepository.save(medico);
        }).orElse(null);
    }

    // Eliminación Lógica
    public void eliminarLogico(Long id) {
        medicoRepository.findById(id).ifPresent(medico -> {
            medico.setEstado(false);
            medicoRepository.save(medico);
        });
    }

    public List<LocalTime> obtenerDisponibilidad(Long idMedico, LocalDate fecha) {
        Medico medico = medicoRepository.findById(idMedico).orElse(null);
        if (medico == null || !medico.getEstado()) {
            return new ArrayList<>();
        }

        List<LocalTime> turnosBase = new ArrayList<>();
        List<TurnoPlanificado> planificaciones = turnoPlanificadoRepository.findByMedicoIdMedicoAndFecha(idMedico, fecha);

        if (!planificaciones.isEmpty()) {
            for (TurnoPlanificado tp : planificaciones) {
                if (tp.getActivo()) {
                    turnosBase.add(tp.getHora());
                }
            }
        } else {
            String turno = medico.getHorarioTurno() != null ? medico.getHorarioTurno().toLowerCase() : "";
            if (turno.contains("mañana")) {
                turnosBase.add(LocalTime.of(8, 0));
                turnosBase.add(LocalTime.of(9, 0));
                turnosBase.add(LocalTime.of(10, 0));
                turnosBase.add(LocalTime.of(11, 0));
                turnosBase.add(LocalTime.of(12, 0));
                turnosBase.add(LocalTime.of(13, 0));
            } else if (turno.contains("tarde")) {
                turnosBase.add(LocalTime.of(14, 0));
                turnosBase.add(LocalTime.of(15, 0));
                turnosBase.add(LocalTime.of(16, 0));
                turnosBase.add(LocalTime.of(17, 0));
                turnosBase.add(LocalTime.of(18, 0));
                turnosBase.add(LocalTime.of(19, 0));
                turnosBase.add(LocalTime.of(20, 0));
            } else {
                for (int h = 8; h <= 20; h++) {
                    turnosBase.add(LocalTime.of(h, 0));
                }
            }
        }

        List<Cita> citas = citaRepository.findByMedicoIdMedicoAndEstadoTrue(idMedico);
        Set<LocalTime> horasOcupadas = citas.stream()
                .filter(c -> c.getFechaHora().toLocalDate().isEqual(fecha) && !"CANCELADA".equalsIgnoreCase(c.getEstadoCita()))
                .map(c -> c.getFechaHora().toLocalTime())
                .collect(Collectors.toSet());

        return turnosBase.stream()
                .filter(t -> !horasOcupadas.contains(t))
                .sorted()
                .collect(Collectors.toList());
    }

    @Transactional
    public void guardarPlanificacion(Long idMedico, LocalDate fecha, List<LocalTime> horasActivas) {
        Medico medico = medicoRepository.findById(idMedico).orElse(null);
        if (medico == null) return;

        turnoPlanificadoRepository.deleteByMedicoIdMedicoAndFecha(idMedico, fecha);

        for (int h = 8; h <= 20; h++) {
            LocalTime hora = LocalTime.of(h, 0);
            boolean activo = horasActivas.contains(hora);
            TurnoPlanificado tp = new TurnoPlanificado(medico, fecha, hora, activo);
            turnoPlanificadoRepository.save(tp);
        }
    }

    public List<TurnoPlanificado> obtenerPlanificacion(Long idMedico, LocalDate fecha) {
        return turnoPlanificadoRepository.findByMedicoIdMedicoAndFecha(idMedico, fecha);
    }
}
