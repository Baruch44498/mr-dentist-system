package com.mrdentist.clinica_backend.repository;

import com.mrdentist.clinica_backend.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {
    Optional<Receta> findByAtencionClinicaIdAtencion(Long idAtencion);
}
