package com.mrdentist.clinica_backend.service;

import com.mrdentist.clinica_backend.entity.AtencionClinica;
import com.mrdentist.clinica_backend.entity.Cita;
import com.mrdentist.clinica_backend.entity.EstadoCita;
import com.mrdentist.clinica_backend.exception.ResourceNotFoundException;
import com.mrdentist.clinica_backend.repository.AtencionClinicaRepository;
import com.mrdentist.clinica_backend.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AtencionClinicaService {

    @Autowired
    private AtencionClinicaRepository atencionClinicaRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Transactional
    public AtencionClinica registrarAtencion(AtencionClinica atencion) {
        Cita cita = atencion.getCita();
        if (cita != null && cita.getIdCita() != null) {
            Cita citaExistente = citaRepository.findById(cita.getIdCita())
                    .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + cita.getIdCita()));
            citaExistente.setEstadoCita(EstadoCita.ATENDIDA);
            citaRepository.save(citaExistente);
            atencion.setCita(citaExistente);
        }
        return atencionClinicaRepository.save(atencion);
    }

    public Optional<AtencionClinica> obtenerAtencionPorIdCita(Long idCita) {
        return atencionClinicaRepository.findByCitaIdCita(idCita);
    }
}
