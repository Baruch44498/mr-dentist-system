package com.mrdentist.clinica_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "turnos_planificados")
public class TurnoPlanificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTurno;

    @ManyToOne
    @JoinColumn(name = "id_medico", nullable = false)
    private Medico medico;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(nullable = false)
    private Boolean activo = true;

    @PrePersist
    public void prePersist() {
        if (this.activo == null) {
            this.activo = true;
        }
    }

    public TurnoPlanificado() {
    }

    public TurnoPlanificado(Medico medico, LocalDate fecha, LocalTime hora, Boolean activo) {
        this.medico = medico;
        this.fecha = fecha;
        this.hora = hora;
        this.activo = activo;
    }

    public Long getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(Long idTurno) {
        this.idTurno = idTurno;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
