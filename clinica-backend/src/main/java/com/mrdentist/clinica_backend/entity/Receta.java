package com.mrdentist.clinica_backend.entity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recetas")
public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReceta;

    // Relación 0..1 (JPA lo maneja como OneToOne, la lógica de negocio decide si se crea o no)
    @OneToOne
    @JoinColumn(name = "id_atencion", nullable = false, unique = true)
    private AtencionClinica atencionClinica;

    @Column(nullable = false)
    private LocalDate fechaEmision = LocalDate.now();

    // LA MAGIA: Si guardas una receta, automáticamente se guardan sus detalles
    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleReceta> detalles = new ArrayList<>();

    public Receta(){

    }

    public Receta(Long idReceta, AtencionClinica atencionClinica, LocalDate fechaEmision, List<DetalleReceta> detalles) {
        this.idReceta = idReceta;
        this.atencionClinica = atencionClinica;
        this.fechaEmision = fechaEmision;
        this.detalles = detalles;
    }

    public Long getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(Long idReceta) {
        this.idReceta = idReceta;
    }

    public AtencionClinica getAtencionClinica() {
        return atencionClinica;
    }

    public void setAtencionClinica(AtencionClinica atencionClinica) {
        this.atencionClinica = atencionClinica;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public List<DetalleReceta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleReceta> detalles) {
        this.detalles = detalles;
    }
}
