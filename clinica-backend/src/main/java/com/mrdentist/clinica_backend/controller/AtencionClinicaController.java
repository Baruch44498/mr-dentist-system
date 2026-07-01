package com.mrdentist.clinica_backend.controller;
import com.mrdentist.clinica_backend.entity.AtencionClinica;
import com.mrdentist.clinica_backend.service.AtencionClinicaService;
import com.mrdentist.clinica_backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/atenciones")
public class AtencionClinicaController {

    @Autowired
    private AtencionClinicaService atencionClinicaService;

    @PostMapping
    public ResponseEntity<AtencionClinica> registrarAtencion(@RequestBody AtencionClinica atencion) {
        AtencionClinica nuevaAtencion = atencionClinicaService.registrarAtencion(atencion);
        return ResponseEntity.ok(nuevaAtencion);
    }

    @GetMapping("/cita/{idCita}")
    public AtencionClinica obtenerAtencionPorIdCita(@PathVariable Long idCita) {
        return atencionClinicaService.obtenerAtencionPorIdCita(idCita)
                .orElseThrow(() -> new ResourceNotFoundException("Atencion no encontrada para la cita ID: " + idCita));
    }
}
