package com.mrdentist.clinica_backend.repository;

import com.mrdentist.clinica_backend.entity.TurnoPlanificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TurnoPlanificadoRepository extends JpaRepository<TurnoPlanificado, Long> {
    List<TurnoPlanificado> findByMedicoIdMedicoAndFecha(Long idMedico, LocalDate fecha);

    @Transactional
    void deleteByMedicoIdMedicoAndFecha(Long idMedico, LocalDate fecha);
}
