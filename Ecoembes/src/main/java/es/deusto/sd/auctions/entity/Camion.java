package es.deusto.sd.auctions.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Camion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planta_id")
    private PlantaDeReciclaje planta;

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

    public void setPlanta(PlantaDeReciclaje planta){this.planta = planta;}

    public Camion(){}

    public Camion(List<Contenedor> contenedores, PlantaDeReciclaje planta, Date fecha) {
        this.contenedores = contenedores;
        this.planta = planta;
        this.fecha = fecha;
    }
}
