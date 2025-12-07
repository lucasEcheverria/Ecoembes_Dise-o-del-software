package es.deusto.sd.auctions.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Camion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @Column(name = "planta", nullable = false)
    private String planta;

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

    public void setPlanta(String planta){this.planta = planta;}

    public Camion(){}

    public Camion(List<Contenedor> contenedores, String planta, Date fecha) {
        this.contenedores = contenedores;
        this.planta = planta;
        this.fecha = fecha;
    }
}
