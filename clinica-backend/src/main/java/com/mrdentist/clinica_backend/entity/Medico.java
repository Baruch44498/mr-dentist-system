package com.mrdentist.clinica_backend.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "medicos")

public class Medico
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMedico;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    // COP: Colegio Odontológico del Perú (Dato de colegiatura)
    @Column(nullable = false, unique = true, length = 15)
    private String cop;

    @ManyToOne
    @JoinColumn(name = "id_especialidad", nullable = false)
    private Especialidad especialidad;

    @Column(nullable = false, length = 50)
    private String horarioTurno; // Ej: "Lunes a Viernes - Mañana", "Martes y Jueves - Tarde"

    @Column(length = 15)
    private String telefono;

    @Column(unique = true, length = 100)
    private String correo;

    // Cumple con RNF11: Eliminación lógica
    @Column(nullable = false)
    private Boolean estado = true;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_usuario", referencedColumnName = "idUsuario")
    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = true;
        }
    }

    public Medico(Long idMedico, String nombres, String apellidos, String dni, String cop, Especialidad especialidad, String horarioTurno, String telefono, String correo, Boolean estado, Usuario usuario) {
        this.idMedico = idMedico;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.cop = cop;
        this.especialidad = especialidad;
        this.horarioTurno = horarioTurno;
        this.telefono = telefono;
        this.correo = correo;
        this.estado = estado;
        this.usuario = usuario;
    }

    public Medico() {
    }

    public Long getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Long idMedico) {
        this.idMedico = idMedico;
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

    public String getCop() {
        return cop;
    }

    public void setCop(String cop) {
        this.cop = cop;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public String getHorarioTurno() {
        return horarioTurno;
    }

    public void setHorarioTurno(String horarioTurno) {
        this.horarioTurno = horarioTurno;
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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
