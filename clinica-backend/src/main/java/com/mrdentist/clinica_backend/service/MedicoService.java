package com.mrdentist.clinica_backend.service;
import com.mrdentist.clinica_backend.entity.Medico;
import com.mrdentist.clinica_backend.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MedicoService
{
    @Autowired
    private MedicoRepository medicoRepository;

    public List<Medico> listarActivos() {
        return medicoRepository.findByEstadoTrue();
    }

    public Medico guardar(Medico medico) {
        return medicoRepository.save(medico);
    }

    public Medico actualizar(Long id, Medico medicoActualizado) {
        return medicoRepository.findById(id).map(medico -> {
            medico.setNombres(medicoActualizado.getNombres());
            medico.setApellidos(medicoActualizado.getApellidos());
            medico.setDni(medicoActualizado.getDni());
            medico.setCop(medicoActualizado.getCop());
            medico.setEspecialidad(medicoActualizado.getEspecialidad());
            medico.setHorarioTurno(medicoActualizado.getHorarioTurno());
            medico.setTelefono(medicoActualizado.getTelefono());
            medico.setCorreo(medicoActualizado.getCorreo());
            return medicoRepository.save(medico);
        }).orElse(null);
    }

    // Eliminación Lógica
    public void eliminarLogico(Long id) {
        medicoRepository.findById(id).ifPresent(medico -> {
            medico.setEstado(false);
            medicoRepository.save(medico);
        });
    }
}
