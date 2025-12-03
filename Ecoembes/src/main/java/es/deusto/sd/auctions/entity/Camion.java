package es.deusto.sd.auctions.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Camion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "camion_contenedor",
            joinColumns = @JoinColumn(name = "camion_id"),
            inverseJoinColumns = @JoinColumn(name = "contenedor_id")
    )
    private List<Contenedor> contenedores;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha", nullable = false)
    private Date fecha;

    public List<Contenedor> getContenedores() {
        return contenedores;
    }

    public void setContenedores(List<Contenedor> contenedores) {
        this.contenedores = contenedores;
    }

    public Camion(){}

    public Camion(List<Contenedor> contenedores, Date fecha) {
        this.contenedores = contenedores;
        this.fecha = fecha;
    }
}
