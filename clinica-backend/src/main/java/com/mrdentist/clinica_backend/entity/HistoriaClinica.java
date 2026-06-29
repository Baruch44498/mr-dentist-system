package com.mrdentist.clinica_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historia_clinica")
public class HistoriaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistoriaClinica;

    @OneToOne
    @JoinColumn(name = "id_paciente", nullable = false, unique = true)
    private Paciente paciente;

    @Column(columnDefinition = "TEXT")
    private String antecedentesMedicos;

    @Column(columnDefinition = "TEXT")
    private String antecedentesOdontologicos;

    @Column(columnDefinition = "TEXT")
    private String observacionesGenerales;

    private LocalDateTime fechaActualizacion;

    @Column(nullable = false)
    private Boolean estado = true;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        this.fechaActualizacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = true;
        }
    }

    // Constructor vacío
    public HistoriaClinica() {}

    // Constructor con campos
    public HistoriaClinica(Long idHistoriaClinica, Paciente paciente, String antecedentesMedicos, String antecedentesOdontologicos, String observacionesGenerales, LocalDateTime fechaActualizacion, Boolean estado) {
        this.idHistoriaClinica = idHistoriaClinica;
        this.paciente = paciente;
        this.antecedentesMedicos = antecedentesMedicos;
        this.antecedentesOdontologicos = antecedentesOdontologicos;
        this.observacionesGenerales = observacionesGenerales;
        this.fechaActualizacion = fechaActualizacion;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getIdHistoriaClinica() {
        return idHistoriaClinica;
    }

    public void setIdHistoriaClinica(Long idHistoriaClinica) {
        this.idHistoriaClinica = idHistoriaClinica;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getAntecedentesMedicos() {
        return antecedentesMedicos;
    }

    public void setAntecedentesMedicos(String antecedentesMedicos) {
        this.antecedentesMedicos = antecedentesMedicos;
    }

    public String getAntecedentesOdontologicos() {
        return antecedentesOdontologicos;
    }

    public void setAntecedentesOdontologicos(String antecedentesOdontologicos) {
        this.antecedentesOdontologicos = antecedentesOdontologicos;
    }

    public String getObservacionesGenerales() {
        return observacionesGenerales;
    }

    public void setObservacionesGenerales(String observacionesGenerales) {
        this.observacionesGenerales = observacionesGenerales;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
