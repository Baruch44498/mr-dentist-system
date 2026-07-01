package com.mrdentist.clinica_backend.repository;

import com.mrdentist.clinica_backend.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    List<Paciente> findByEstadoTrue();

    @Query("SELECT p FROM Paciente p WHERE p.estado = true AND " +
           "(LOWER(p.dni) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(p.nombres) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :texto, '%')))")
    List<Paciente> buscarPacientesActivos(@Param("texto") String texto);
}
