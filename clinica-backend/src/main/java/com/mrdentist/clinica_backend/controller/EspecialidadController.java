package com.mrdentist.clinica_backend.controller;
import com.mrdentist.clinica_backend.entity.Especialidad;
import com.mrdentist.clinica_backend.repository.EspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/especialidades")

public class EspecialidadController
{
    @Autowired
    private EspecialidadRepository especialidadRepository;

    // 1. LISTAR (Solo las activas para el Frontend)
    @GetMapping
    public List<Especialidad> listarEspecialidades() {
        return especialidadRepository.findByEstadoTrue();
    }

    // 2. GUARDAR / CREAR
    @PostMapping
    public Especialidad registrarEspecialidad(@RequestBody java.util.Map<String, String> body) {
        Especialidad nueva = new Especialidad();
        nueva.setNombre(body.get("especialidad")); // Mapea con el campo del JSON del front
        nueva.setDescripcion(body.get("descripcion"));
        nueva.setEstado(true);
        return especialidadRepository.save(nueva);
    }

    // 3. ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<Especialidad> actualizarEspecialidad(@PathVariable Long id, @RequestBody Especialidad datosActualizados) {
        return especialidadRepository.findById(id)
                .map(esp -> {
                    esp.setNombre(datosActualizados.getNombre());
                    esp.setDescripcion(datosActualizados.getDescripcion());
                    return ResponseEntity.ok(especialidadRepository.save(esp));
                }).orElse(ResponseEntity.notFound().build());
    }

    // 4. ELIMINACIÓN LÓGICA (Cambiar estado a false en vez de borrar de la DB)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLogico(@PathVariable Long id) {
        return especialidadRepository.findById(id)
                .map(esp -> {
                    esp.setEstado(false); // Eliminación lógica según requerimiento RNF11
                    especialidadRepository.save(esp);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}
