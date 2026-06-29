package com.mrdentist.clinica_backend.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
@Entity
@Table(name = "detalles_receta")
public class DetalleReceta
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_receta", nullable = false)
    @JsonIgnore // Evita bucles infinitos al convertir a JSON
    private Receta receta;

    @Column(nullable = false)
    private String medicamento;

    @Column(nullable = false)
    private String dosis;

    @Column(nullable = false)
    private String frecuencia;

    @Column(nullable = false)
    private String duracion;

    @Column(columnDefinition = "TEXT")
    private String indicaciones;
    public DetalleReceta(){}

    public DetalleReceta(Long idDetalle, Receta receta, String medicamento, String dosis, String frecuencia, String duracion, String indicaciones) {
        this.idDetalle = idDetalle;
        this.receta = receta;
        this.medicamento = medicamento;
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.duracion = duracion;
        this.indicaciones = indicaciones;
    }

    public Long getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Long idDetalle) {
        this.idDetalle = idDetalle;
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

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }
}
