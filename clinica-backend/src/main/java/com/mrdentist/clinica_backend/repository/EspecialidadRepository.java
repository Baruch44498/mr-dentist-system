package com.mrdentist.clinica_backend.repository;
import com.mrdentist.clinica_backend.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long>
{
    List<Especialidad> findByEstadoTrue();
}
