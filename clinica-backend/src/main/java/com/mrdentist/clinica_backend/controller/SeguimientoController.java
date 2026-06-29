package com.mrdentist.clinica_backend.controller;

import com.mrdentist.clinica_backend.entity.Seguimiento;
import com.mrdentist.clinica_backend.service.SeguimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class SeguimientoController {

    @Autowired
    private SeguimientoService seguimientoService;

    @GetMapping("/api/pacientes/{idPaciente}/seguimientos")
    public List<Seguimiento> listarSeguimientos(@PathVariable Long idPaciente) {
        return seguimientoService.listarPorPaciente(idPaciente);
    }

    @PostMapping("/api/pacientes/{idPaciente}/seguimientos")
    public Seguimiento registrarSeguimiento(@PathVariable Long idPaciente, @RequestBody Seguimiento seguimiento) {
        return seguimientoService.guardar(idPaciente, seguimiento);
    }

    @PutMapping("/api/seguimientos/{idSeguimiento}")
    public Seguimiento actualizarSeguimiento(@PathVariable Long idSeguimiento, @RequestBody Seguimiento seguimiento) {
        return seguimientoService.actualizar(idSeguimiento, seguimiento);
    }

    @DeleteMapping("/api/seguimientos/{idSeguimiento}")
    public void eliminarSeguimiento(@PathVariable Long idSeguimiento) {
        seguimientoService.eliminarLogico(idSeguimiento);
    }
}
