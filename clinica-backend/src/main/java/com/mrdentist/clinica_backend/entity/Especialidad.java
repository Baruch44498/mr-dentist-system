package com.mrdentist.clinica_backend.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "especialidades")

public class Especialidad
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialidad")
    private Long idEspecialidad; // Mantenemos consistencia con tipos BIGINT de tu DB

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Boolean estado = true; // Para el borrado lógico requerido (RNF11)

    public Especialidad(Long idEspecialidad, String nombre, String descripcion, Boolean estado)
    {
        this.idEspecialidad = idEspecialidad;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
    }
    public Especialidad(){}

    public Long getIdEspecialidad() {
        return idEspecialidad;
    }


    public void setIdEspecialidad(Long idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
