package com.mrdentist.clinica_backend.controller;
import com.mrdentist.clinica_backend.entity.Medico;
import com.mrdentist.clinica_backend.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/medicos")
@CrossOrigin(origins = "http://localhost:4200")

public class MedicoController
{
    @Autowired
    private MedicoService medicoService;

    @GetMapping
    public List<Medico> listarMedicos() {
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

}
