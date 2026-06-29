package com.mrdentist.clinica_backend.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false, unique = true)
    private String username; // Generalmente el correo electrónico

    @Column(nullable = false)
    private String password; // Se guardará encriptada (BCrypt)

    @Column(nullable = false)
    private String rol; // "ADMIN", "MEDICO", "SECRETARIA"

    @Column(nullable = false)
    private Boolean activo = true;

    // 🔥 Tu propuesta estrella de seguridad
    @Column(nullable = false)
    private Boolean primerInicio = true;

    // Relación Inversa (Opcional, pero muy útil para buscar al médico desde el usuario)
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Medico medico;
    public Usuario(){}

    public Usuario(Long idUsuario, String username, String password, String rol, Boolean activo, Boolean primerInicio, Medico medico) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
        this.primerInicio = primerInicio;
        this.medico = medico;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Boolean getPrimerInicio() {
        return primerInicio;
    }

    public void setPrimerInicio(Boolean primerInicio) {
        this.primerInicio = primerInicio;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }
}
