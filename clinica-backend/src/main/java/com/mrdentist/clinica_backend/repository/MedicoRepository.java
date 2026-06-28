package com.mrdentist.clinica_backend.repository;
import com.mrdentist.clinica_backend.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long>
{
    // Traer solo los médicos que siguen trabajando en la clínica
    List<Medico> findByEstadoTrue();

    // Búsqueda por especialidad para cuando hagamos el CRUD de Citas
    List<Medico> findByEspecialidadNombreContainingAndEstadoTrue(String especialidad);

}
