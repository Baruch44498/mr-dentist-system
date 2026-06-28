package com.mrdentist.clinica_backend.controller;

import com.mrdentist.clinica_backend.entity.Medico;
import com.mrdentist.clinica_backend.entity.TurnoPlanificado;
import com.mrdentist.clinica_backend.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController
{
    @Autowired
    private MedicoService medicoService;

    @GetMapping
    public List<Medico> listarMedicos(@RequestParam(required = false) String especialidad) {
        if (especialidad != null && !especialidad.trim().isEmpty()) {
            return medicoService.listarPorEspecialidad(especialidad);
        }
        return medicoService.listarActivos();
    }

    @PostMapping
    public Medico registrarMedico(@RequestBody Medico medico) {
        return medicoService.guardar(medico);
    }

    @PutMapping("/{id}")
    public Medico actualizarMedico(@PathVariable Long id, @RequestBody Medico medico) {
        return medicoService.actualizar(id, medico);
    }

    @DeleteMapping("/{id}")
    public void eliminarMedico(@PathVariable Long id) {
        medicoService.eliminarLogico(id);
    }

    @GetMapping("/{id}/disponibilidad")
    public List<LocalTime> obtenerDisponibilidad(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return medicoService.obtenerDisponibilidad(id, fecha);
    }

    @GetMapping("/{id}/planificacion")
    public List<TurnoPlanificado> obtenerPlanificacion(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return medicoService.obtenerPlanificacion(id, fecha);
    }

    @PostMapping("/{id}/planificacion")
    public void guardarPlanificacion(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestBody List<LocalTime> horasActivas) {
        medicoService.guardarPlanificacion(id, fecha, horasActivas);
    }
}
