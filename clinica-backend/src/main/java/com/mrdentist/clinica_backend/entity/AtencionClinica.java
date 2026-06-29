package com.mrdentist.clinica_backend.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "atenciones_clinicas")
public class AtencionClinica
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAtencion;

    @OneToOne
    @JoinColumn(name = "id_cita", nullable = false, unique = true)
    private Cita cita;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String diagnostico;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String tratamientoAplicado;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // Campos opcionales propuestos
    private Double peso;
    private Double temperatura;

    @Column(nullable = false)
    private LocalDateTime fechaAtencion = LocalDateTime.now();

    public AtencionClinica(){}

    public AtencionClinica(Long idAtencion, Cita cita, String diagnostico, String tratamientoAplicado, String observaciones, Double peso, Double temperatura, LocalDateTime fechaAtencion) {
        this.idAtencion = idAtencion;
        this.cita = cita;
        this.diagnostico = diagnostico;
        this.tratamientoAplicado = tratamientoAplicado;
        this.observaciones = observaciones;
        this.peso = peso;
        this.temperatura = temperatura;
        this.fechaAtencion = fechaAtencion;
    }

    public Long getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(Long idAtencion) {
        this.idAtencion = idAtencion;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamientoAplicado() {
        return tratamientoAplicado;
    }

    public void setTratamientoAplicado(String tratamientoAplicado) {
        this.tratamientoAplicado = tratamientoAplicado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public LocalDateTime getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(LocalDateTime fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }
}
