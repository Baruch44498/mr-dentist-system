package com.mrdentist.clinica_backend.service;
import com.mrdentist.clinica_backend.entity.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mrdentist.clinica_backend.repository.PacienteRepository;

import java.util.List;

@Service
public class PacienteService
{
    @Autowired
    private PacienteRepository pacienteRepository;
    // Listar activos
    public List<Paciente> listarActivos() {
        return pacienteRepository.findByEstadoTrue();
    }

    // Registrar
    public Paciente guardar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    // Actualizar
    public Paciente actualizar(Long id, Paciente pacienteActualizado) {
        return pacienteRepository.findById(id).map(paciente -> {
            paciente.setNombres(pacienteActualizado.getNombres());
            paciente.setApellidos(pacienteActualizado.getApellidos());
            paciente.setDni(pacienteActualizado.getDni());
            paciente.setTelefono(pacienteActualizado.getTelefono());
            paciente.setCorreo(pacienteActualizado.getCorreo());
            return pacienteRepository.save(paciente);
        }).orElse(null); // Retorna nulo si no encuentra el ID
    }

    // Eliminación Lógica (Desactivar)
    public void eliminarLogico(Long id) {
        pacienteRepository.findById(id).ifPresent(paciente -> {
            paciente.setEstado(false);
            pacienteRepository.save(paciente);
        });
    }
}
