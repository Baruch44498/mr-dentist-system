package com.mrdentist.clinica_backend.repository;

import com.mrdentist.clinica_backend.entity.Cita;
import com.mrdentist.clinica_backend.entity.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByEstadoTrue();
    List<Cita> findByMedicoIdMedicoAndEstadoTrue(Long idMedico);

    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE c.medico.idMedico = :idMedico AND c.fechaHora = :fechaHora AND c.estado = true AND c.estadoCita <> :estadoCita")
    boolean existsActiveCitaForMedicoAtFechaHora(@Param("idMedico") Long idMedico, @Param("fechaHora") LocalDateTime fechaHora, @Param("estadoCita") EstadoCita estadoCita);

    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE c.medico.idMedico = :idMedico AND c.fechaHora = :fechaHora AND c.estado = true AND c.estadoCita <> :estadoCita AND c.idCita <> :excludeCitaId")
    boolean existsActiveCitaForMedicoAtFechaHoraExcludeCita(@Param("idMedico") Long idMedico, @Param("fechaHora") LocalDateTime fechaHora, @Param("excludeCitaId") Long excludeCitaId, @Param("estadoCita") EstadoCita estadoCita);
}
