package com.mrdentist.clinica_backend.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "citas")
public class Cita
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCita;

    // Relación: Muchas citas pueden pertenecer a UN paciente
    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    // Relación: Muchas citas pueden ser atendidas por UN médico
    @ManyToOne
    @JoinColumn(name = "id_medico", nullable = false)
    private Medico medico;

    @Column(nullable = false)
    private LocalDateTime fechaHora; // Guarda fecha y hora exacta

    @Column(length = 255)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCita estadoCita = EstadoCita.PENDIENTE; // PENDIENTE, ATENDIDA, CANCELADA

    // Eliminación lógica
    @Column(nullable = false)
    private Boolean estado = true;

    @PrePersist
    public void prePersist() {
        if (this.estadoCita == null) {
            this.estadoCita = EstadoCita.PENDIENTE;
        }
        if (this.estado == null) {
            this.estado = true;
        }
    }

    public Cita(Long idCita, Paciente paciente, Medico medico, LocalDateTime fechaHora, String motivo, EstadoCita estadoCita, Boolean estado) {
        this.idCita = idCita;
        this.paciente = paciente;
        this.medico = medico;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estadoCita = estadoCita;
        this.estado = estado;
    }
    // 1. Constructor vacío obligatorio para JPA
    public Cita() {
    }

    public Long getIdCita() {
        return idCita;
    }

    public void setIdCita(Long idCita) {
        this.idCita = idCita;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoCita getEstadoCita() {
        return estadoCita;
    }

    public void setEstadoCita(EstadoCita estadoCita) {
        this.estadoCita = estadoCita;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
