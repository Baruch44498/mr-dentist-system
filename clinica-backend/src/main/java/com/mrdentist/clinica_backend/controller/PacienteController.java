package com.mrdentist.clinica_backend.controller;
import com.mrdentist.clinica_backend.entity.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mrdentist.clinica_backend.service.PacienteService;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController
{
    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public List<Paciente> listarPacientes() {
        return pacienteService.listarActivos();
    }

    @PostMapping
    public Paciente registrarPaciente(@RequestBody Paciente paciente) {
        return pacienteService.guardar(paciente);
    }

    @PutMapping("/{id}")
    public Paciente actualizarPaciente(@PathVariable Long id, @RequestBody Paciente paciente) {
        return pacienteService.actualizar(id, paciente);
    }

    @DeleteMapping("/{id}")
    public void eliminarPaciente(@PathVariable Long id) {
        pacienteService.eliminarLogico(id);
    }

    @GetMapping("/buscar")
    public List<Paciente> buscarPacientes(@RequestParam String texto) {
        return pacienteService.buscarPacientes(texto);
    }
}
