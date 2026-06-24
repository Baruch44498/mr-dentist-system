package com.mrdentist.clinica_backend.repository;
import com.mrdentist.clinica_backend.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long>
{
    // Trae solo los pacientes activos (estado = true)
    List<Paciente> findByEstadoTrue();

    // Cumple con RF16: Búsqueda dinámica para tu frontend
    List<Paciente> findByDniContainingOrNombresContainingOrApellidosContainingAndEstadoTrue(String dni, String nombres, String apellidos);
}
