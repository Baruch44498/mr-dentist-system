package com.mrdentist.clinica_backend.repository;

import com.mrdentist.clinica_backend.entity.AtencionClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AtencionClinicaRepository extends JpaRepository<AtencionClinica, Long> {
    Optional<AtencionClinica> findByCitaIdCita(Long idCita);
}
