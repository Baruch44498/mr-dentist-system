package com.mrdentist.clinica_backend.entity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pacientes")
public class Paciente
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaciente;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(length = 15)
    private String telefono;

    @Column(unique = true, length = 100)
    private String correo;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    // Cumple con RNF11: Eliminación lógica
    @Column(nullable = false)
    private Boolean estado = true;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDate.now(); // Asigna la fecha actual automáticamente
        if (this.estado == null) {
            this.estado = true;
        }
    }

    public Paciente(Long idPaciente, String nombres, String apellidos, String dni, String telefono, String correo, LocalDate fechaRegistro, Boolean estado) {
        this.idPaciente = idPaciente;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.telefono = telefono;
        this.correo = correo;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Paciente() {
    }
}
