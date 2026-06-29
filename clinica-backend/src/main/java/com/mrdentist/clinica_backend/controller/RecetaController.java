package com.mrdentist.clinica_backend.controller;

import com.mrdentist.clinica_backend.entity.Receta;
import com.mrdentist.clinica_backend.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class RecetaController {

    @Autowired
    private RecetaService recetaService;

    @GetMapping("/api/pacientes/{idPaciente}/recetas")
    public List<Receta> listarRecetas(@PathVariable Long idPaciente) {
        return recetaService.listarPorPaciente(idPaciente);
    }

    @PostMapping("/api/pacientes/{idPaciente}/recetas")
    public Receta registrarReceta(@PathVariable Long idPaciente, @RequestBody Receta receta) {
        return recetaService.guardar(idPaciente, receta);
    }

    @PutMapping("/api/recetas/{idReceta}")
    public Receta actualizarReceta(@PathVariable Long idReceta, @RequestBody Receta receta) {
        return recetaService.actualizar(idReceta, receta);
    }

    @DeleteMapping("/api/recetas/{idReceta}")
    public void eliminarReceta(@PathVariable Long idReceta) {
        recetaService.eliminarLogico(idReceta);
    }
}
