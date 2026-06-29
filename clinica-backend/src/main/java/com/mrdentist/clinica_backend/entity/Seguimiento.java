package com.mrdentist.clinica_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seguimientos")
public class Seguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSeguimiento;

    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "id_cita", nullable = true)
    private Cita cita;

    @ManyToOne
    @JoinColumn(name = "id_medico", nullable = true)
    private Medico medico;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    private LocalDateTime fechaSeguimiento;

    @Column(nullable = false)
    private Boolean estado = true;

    @PrePersist
    public void prePersist() {
        if (this.fechaSeguimiento == null) {
            this.fechaSeguimiento = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = true;
        }
    }

    public Seguimiento() {}

    public Seguimiento(Long idSeguimiento, Paciente paciente, Cita cita, Medico medico, String descripcion, LocalDateTime fechaSeguimiento, Boolean estado) {
        this.idSeguimiento = idSeguimiento;
        this.paciente = paciente;
        this.cita = cita;
        this.medico = medico;
        this.descripcion = descripcion;
        this.fechaSeguimiento = fechaSeguimiento;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getIdSeguimiento() {
        return idSeguimiento;
    }

    public void setIdSeguimiento(Long idSeguimiento) {
        this.idSeguimiento = idSeguimiento;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaSeguimiento() {
        return fechaSeguimiento;
    }

    public void setFechaSeguimiento(LocalDateTime fechaSeguimiento) {
        this.fechaSeguimiento = fechaSeguimiento;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
