package com.mrdentist.clinica_backend.repository;

import com.mrdentist.clinica_backend.entity.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {
    Optional<HistoriaClinica> findByPacienteIdPacienteAndEstadoTrue(Long idPaciente);
}
