package com.mrdentist.clinica_backend.service;

import com.mrdentist.clinica_backend.entity.Cita;
import com.mrdentist.clinica_backend.entity.Paciente;
import com.mrdentist.clinica_backend.entity.Receta;
import com.mrdentist.clinica_backend.entity.DetalleReceta;
import com.mrdentist.clinica_backend.repository.CitaRepository;
import com.mrdentist.clinica_backend.repository.PacienteRepository;
import com.mrdentist.clinica_backend.repository.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RecetaService {

    @Autowired
    private RecetaRepository recetaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private CitaRepository citaRepository;

    public List<Receta> listarPorPaciente(Long pacienteId) {
        return recetaRepository.findByPacienteIdPacienteAndEstadoTrueOrderByFechaEmisionDesc(pacienteId);
    }

    public Receta guardar(Long pacienteId, Receta receta) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .filter(Paciente::getEstado)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        receta.setPaciente(paciente);

        if (receta.getCita() == null || receta.getCita().getIdCita() == null) {
            throw new IllegalArgumentException("La cita es obligatoria para emitir una receta.");
        }

        Cita cita = citaRepository.findById(receta.getCita().getIdCita())
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        if (!cita.getPaciente().getIdPaciente().equals(pacienteId)) {
            throw new IllegalArgumentException("La cita seleccionada no pertenece al paciente actual.");
        }

        if (Boolean.FALSE.equals(cita.getEstado()) || "CANCELADA".equalsIgnoreCase(cita.getEstadoCita())) {
            throw new IllegalArgumentException("La cita está cancelada o inactiva y no se puede asociar.");
        }

        receta.setCita(cita);

        // Asociar bidireccionalmente detalles
        if (receta.getDetalles() != null) {
            for (DetalleReceta detalle : receta.getDetalles()) {
                detalle.setReceta(receta);
                detalle.setEstado(true);
            }
        }

        // Generar texto concatenado compatible secundario
        if (receta.getDetalles() != null && !receta.getDetalles().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int idx = 1;
            for (DetalleReceta item : receta.getDetalles()) {
                sb.append(idx++).append(". Medicamento: ").append(item.getMedicamento()).append("\n")
                  .append("   Presentación: ").append(item.getPresentacion() != null ? item.getPresentacion() : "-").append("\n")
                  .append("   Dosis: ").append(item.getDosis()).append("\n")
                  .append("   Frecuencia: ").append(item.getFrecuencia()).append("\n")
                  .append("   Duración: ").append(item.getDuracion()).append("\n")
                  .append("   Vía: ").append(item.getViaAdministracion() != null ? item.getViaAdministracion() : "-").append("\n")
                  .append("   Indicaciones: ").append(item.getIndicaciones() != null ? item.getIndicaciones() : "-").append("\n\n");
            }
            receta.setMedicamentos(sb.toString().trim());
        }

        receta.setEstado(true);
        return recetaRepository.save(receta);
    }

    public Receta actualizar(Long idReceta, Receta recetaActualizada) {
        return recetaRepository.findById(idReceta).map(receta -> {
            if (recetaActualizada.getCita() == null || recetaActualizada.getCita().getIdCita() == null) {
                throw new IllegalArgumentException("La cita es obligatoria para actualizar la receta.");
            }

            Cita cita = citaRepository.findById(recetaActualizada.getCita().getIdCita())
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            if (!cita.getPaciente().getIdPaciente().equals(receta.getPaciente().getIdPaciente())) {
                throw new IllegalArgumentException("La cita no pertenece al paciente actual.");
            }

            if (Boolean.FALSE.equals(cita.getEstado()) || "CANCELADA".equalsIgnoreCase(cita.getEstadoCita())) {
                throw new IllegalArgumentException("La cita está cancelada o inactiva.");
            }

            receta.setCita(cita);

            // Actualizar detalles con cascada y orphan removal
            receta.getDetalles().clear();
            if (recetaActualizada.getDetalles() != null) {
                for (DetalleReceta det : recetaActualizada.getDetalles()) {
                    det.setReceta(receta);
                    det.setEstado(true);
                    receta.getDetalles().add(det);
                }
            }

            // Generar texto concatenado compatible secundario
            if (receta.getDetalles() != null && !receta.getDetalles().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                int idx = 1;
                for (DetalleReceta item : receta.getDetalles()) {
                    sb.append(idx++).append(". Medicamento: ").append(item.getMedicamento()).append("\n")
                      .append("   Presentación: ").append(item.getPresentacion() != null ? item.getPresentacion() : "-").append("\n")
                      .append("   Dosis: ").append(item.getDosis()).append("\n")
                      .append("   Frecuencia: ").append(item.getFrecuencia()).append("\n")
                      .append("   Duración: ").append(item.getDuracion()).append("\n")
                      .append("   Vía: ").append(item.getViaAdministracion() != null ? item.getViaAdministracion() : "-").append("\n")
                      .append("   Indicaciones: ").append(item.getIndicaciones() != null ? item.getIndicaciones() : "-").append("\n\n");
                }
                receta.setMedicamentos(sb.toString().trim());
            } else {
                receta.setMedicamentos(null);
            }

            return recetaRepository.save(receta);
        }).orElse(null);
    }

    public void eliminarLogico(Long idReceta) {
        recetaRepository.findById(idReceta).ifPresent(receta -> {
            receta.setEstado(false);
            if (receta.getDetalles() != null) {
                for (DetalleReceta det : receta.getDetalles()) {
                    det.setEstado(false);
                }
            }
            recetaRepository.save(receta);
        });
    }

}
