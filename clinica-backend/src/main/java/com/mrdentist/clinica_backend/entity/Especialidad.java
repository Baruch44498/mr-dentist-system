package com.mrdentist.clinica_backend.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "especialidades")

public class Especialidad
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialidad")
    private Long idInmueble; // Mantenemos consistencia con tipos BIGINT de tu DB

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private boolean estado = true; // Para el borrado lógico requerido (RNF11)

    public Especialidad(Long idInmueble, String nombre, String descripcion, boolean estado)
    {
        this.idInmueble = idInmueble;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
    }
    public Especialidad(){}

    public Long getIdInmueble() {
        return idInmueble;
    }


    public void setIdInmueble(Long idInmueble) {
        this.idInmueble = idInmueble;
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

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
