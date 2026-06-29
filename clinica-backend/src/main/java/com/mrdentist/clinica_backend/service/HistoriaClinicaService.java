package com.mrdentist.clinica_backend.service;

import com.mrdentist.clinica_backend.entity.HistoriaClinica;
import com.mrdentist.clinica_backend.entity.Paciente;
import com.mrdentist.clinica_backend.repository.HistoriaClinicaRepository;
import com.mrdentist.clinica_backend.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class HistoriaClinicaService {

    @Autowired
    private HistoriaClinicaRepository historiaClinicaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    public HistoriaClinica obtenerPorPacienteId(Long pacienteId) {
        return historiaClinicaRepository.findByPacienteIdPacienteAndEstadoTrue(pacienteId).orElse(null);
    }

    public HistoriaClinica guardarOActualizar(Long pacienteId, HistoriaClinica historia) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .filter(Paciente::getEstado)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        HistoriaClinica historiaExistente = historiaClinicaRepository.findByPacienteIdPacienteAndEstadoTrue(pacienteId).orElse(null);

        if (historiaExistente != null) {
            historiaExistente.setAntecedentesMedicos(historia.getAntecedentesMedicos());
            historiaExistente.setAntecedentesOdontologicos(historia.getAntecedentesOdontologicos());
            historiaExistente.setObservacionesGenerales(historia.getObservacionesGenerales());
            historiaExistente.setFechaActualizacion(LocalDateTime.now());
            return historiaClinicaRepository.save(historiaExistente);
        } else {
            historia.setPaciente(paciente);
            historia.setEstado(true);
            historia.setFechaActualizacion(LocalDateTime.now());
            return historiaClinicaRepository.save(historia);
        }
    }
}
