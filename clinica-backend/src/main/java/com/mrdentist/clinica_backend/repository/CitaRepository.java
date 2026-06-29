package com.mrdentist.clinica_backend.repository;
import com.mrdentist.clinica_backend.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long>
{
    List<Cita> findByEstadoTrue();
    List<Cita> findByMedicoIdMedicoAndEstadoTrue(Long idMedico);
    List<Cita> findByPacienteIdPacienteAndEstadoTrueOrderByFechaHoraDesc(Long idPaciente);
}

