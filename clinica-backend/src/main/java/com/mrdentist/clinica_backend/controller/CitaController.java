package com.mrdentist.clinica_backend.controller;
import com.mrdentist.clinica_backend.entity.Cita;
import com.mrdentist.clinica_backend.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/citas")

public class CitaController
{
    @Autowired
    private CitaService citaService;

    @GetMapping
    public List<Cita> listarCitas() {
        return citaService.listarActivas();
    }

    @PostMapping
    public Cita registrarCita(@RequestBody Cita cita) {
        return citaService.guardar(cita);
    }

    @PutMapping("/{id}")
    public Cita actualizarCita(@PathVariable Long id, @RequestBody Cita cita) {
        return citaService.actualizar(id, cita);
    }

    @DeleteMapping("/{id}")
    public void eliminarCita(@PathVariable Long id) {
        citaService.eliminarLogico(id);
    }

    @PutMapping("/{id}/cancelar")
    public Cita cancelarCita(@PathVariable Long id, @RequestParam String motivo) {
        return citaService.cancelarCita(id, motivo);
    }
}
