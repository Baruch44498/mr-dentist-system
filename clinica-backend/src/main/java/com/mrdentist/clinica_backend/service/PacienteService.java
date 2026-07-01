package com.mrdentist.clinica_backend.service;

import com.mrdentist.clinica_backend.entity.Paciente;
import com.mrdentist.clinica_backend.exception.ResourceNotFoundException;
import com.mrdentist.clinica_backend.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {
    @Autowired
    private PacienteRepository pacienteRepository;

    public List<Paciente> listarActivos() {
        return pacienteRepository.findByEstadoTrue();
    }

    public Paciente guardar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public Paciente actualizar(Long id, Paciente pacienteActualizado) {
        return pacienteRepository.findById(id).map(paciente -> {
            paciente.setNombres(pacienteActualizado.getNombres());
            paciente.setApellidos(pacienteActualizado.getApellidos());
            paciente.setDni(pacienteActualizado.getDni());
            paciente.setTelefono(pacienteActualizado.getTelefono());
            paciente.setCorreo(pacienteActualizado.getCorreo());
            return pacienteRepository.save(paciente);
        }).orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));
    }

    public void eliminarLogico(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));
        paciente.setEstado(false);
        pacienteRepository.save(paciente);
    }

    public List<Paciente> buscarPacientes(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarActivos();
        }
        return pacienteRepository.buscarPacientesActivos(texto.trim());
    }
}
