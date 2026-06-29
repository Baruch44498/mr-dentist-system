package com.mrdentist.clinica_backend.service;

import com.mrdentist.clinica_backend.entity.Receta;
import com.mrdentist.clinica_backend.repository.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecetaService {

    @Autowired
    private RecetaRepository recetaRepository;

    public Receta registrarReceta(Receta receta)
    {
        if (receta.getDetalles() != null) {
            receta.getDetalles().forEach(detalle -> detalle.setReceta(receta));
        }
        return recetaRepository.save(receta);
    }

    public Optional<Receta> obtenerRecetaPorIdAtencion(Long idAtencion) {
        return recetaRepository.findByAtencionClinicaIdAtencion(idAtencion);
    }
}
