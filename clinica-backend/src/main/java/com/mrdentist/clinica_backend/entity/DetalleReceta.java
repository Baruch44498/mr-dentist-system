package com.mrdentist.clinica_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "detalle_receta")
public class DetalleReceta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalleReceta;

    @ManyToOne
    @JoinColumn(name = "id_receta", nullable = false)
    @JsonBackReference
    private Receta receta;

    @Column(nullable = false)
    private String medicamento;

    private String presentacion;

    @Column(nullable = false)
    private String dosis;

    @Column(nullable = false)
    private String frecuencia;

    @Column(nullable = false)
    private String duracion;

    private String viaAdministracion;

    private String indicaciones;

    @Column(nullable = false)
    private Boolean estado = true;

    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = true;
        }
    }

    public DetalleReceta() {}

    public DetalleReceta(Long idDetalleReceta, Receta receta, String medicamento, String presentacion, String dosis, String frecuencia, String duracion, String viaAdministracion, String indicaciones, Boolean estado) {
        this.idDetalleReceta = idDetalleReceta;
        this.receta = receta;
        this.medicamento = medicamento;
        this.presentacion = presentacion;
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.duracion = duracion;
        this.viaAdministracion = viaAdministracion;
        this.indicaciones = indicaciones;
        this.estado = estado;
    }

    // Getters and Setters
    public Long getIdDetalleReceta() {
        return idDetalleReceta;
    }

    public void setIdDetalleReceta(Long idDetalleReceta) {
        this.idDetalleReceta = idDetalleReceta;
    }

    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        this.receta = receta;
    }

    public String getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getViaAdministracion() {
        return viaAdministracion;
    }

    public void setViaAdministracion(String viaAdministracion) {
        this.viaAdministracion = viaAdministracion;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
