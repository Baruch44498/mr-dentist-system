package com.mrdentist.clinica_backend.repository;

import com.mrdentist.clinica_backend.entity.DetalleReceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleRecetaRepository extends JpaRepository<DetalleReceta, Long> {
}
