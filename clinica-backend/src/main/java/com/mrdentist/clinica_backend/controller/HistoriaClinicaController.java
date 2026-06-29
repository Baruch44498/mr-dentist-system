package com.mrdentist.clinica_backend.controller;

import com.mrdentist.clinica_backend.entity.HistoriaClinica;
import com.mrdentist.clinica_backend.service.HistoriaClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes/{idPaciente}/historia-clinica")
public class HistoriaClinicaController {

    @Autowired
    private HistoriaClinicaService historiaClinicaService;

    @GetMapping
    public HistoriaClinica obtenerHistoriaClinica(@PathVariable Long idPaciente) {
        return historiaClinicaService.obtenerPorPacienteId(idPaciente);
    }

    @PostMapping
    public HistoriaClinica registrarHistoriaClinica(@PathVariable Long idPaciente, @RequestBody HistoriaClinica historia) {
        return historiaClinicaService.guardarOActualizar(idPaciente, historia);
    }

    @PutMapping
    public HistoriaClinica actualizarHistoriaClinica(@PathVariable Long idPaciente, @RequestBody HistoriaClinica historia) {
        return historiaClinicaService.guardarOActualizar(idPaciente, historia);
    }
}
