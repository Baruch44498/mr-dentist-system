package com.mrdentist.clinica_backend.service;

import com.mrdentist.clinica_backend.entity.Cita;
import com.mrdentist.clinica_backend.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CitaService {
    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private EmailService emailService;

    public List<Cita> listarActivas() {
        return citaRepository.findByEstadoTrue();
    }

    public List<Cita> listarPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteIdPacienteAndEstadoTrueOrderByFechaHoraDesc(pacienteId);
    }


    public Cita guardar(Cita cita) {
        return citaRepository.save(cita);
    }

    public Cita actualizar(Long id, Cita citaActualizada) {
        return citaRepository.findById(id).map(cita -> {
            cita.setPaciente(citaActualizada.getPaciente());
            cita.setMedico(citaActualizada.getMedico());
            cita.setFechaHora(citaActualizada.getFechaHora());
            cita.setMotivo(citaActualizada.getMotivo());
            cita.setEstadoCita(citaActualizada.getEstadoCita());
            return citaRepository.save(cita);
        }).orElse(null);
    }

    public void eliminarLogico(Long id) {
        citaRepository.findById(id).ifPresent(cita -> {
            cita.setEstado(false);
            citaRepository.save(cita);
        });
    }

    public Cita cancelarCita(Long id, String motivo) {
        return citaRepository.findById(id).map(cita -> {
            cita.setEstadoCita("CANCELADA");
            Cita citaCancelada = citaRepository.save(cita);
            try {
                emailService.enviarCorreoCancelacion(citaCancelada, motivo);
            } catch (Exception e) {
                System.out.println("[CitaService] Error no crítico al enviar correo: " + e.getMessage());
            }
            return citaCancelada;
        }).orElse(null);
    }
}
