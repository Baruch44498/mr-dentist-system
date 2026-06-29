package com.mrdentist.clinica_backend.controller;

import com.mrdentist.clinica_backend.entity.Receta;
import com.mrdentist.clinica_backend.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    @Autowired
    private RecetaService recetaService;

    @PostMapping
    public ResponseEntity<Receta> registrarReceta(@RequestBody Receta receta) {
        Receta nuevaReceta = recetaService.registrarReceta(receta);
        return ResponseEntity.ok(nuevaReceta);
    }

    @GetMapping("/atencion/{idAtencion}")
    public ResponseEntity<Receta> obtenerRecetaPorIdAtencion(@PathVariable Long idAtencion) {
        return recetaService.obtenerRecetaPorIdAtencion(idAtencion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
