package es.deusto.sd.auctions.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "registro_residuos_planta")
public class RegistroResiduosPlanta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "planta_id", nullable = false)
    private PlantaDeReciclaje planta;

    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @Column(name = "toneladas", nullable = false, precision = 10, scale = 3)
    private BigDecimal toneladas;

    public RegistroResiduosPlanta() {}

    public RegistroResiduosPlanta(PlantaDeReciclaje planta, Date fecha, BigDecimal toneladas) {
        this.planta = planta;
        this.fecha = fecha;
        this.toneladas = toneladas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlantaDeReciclaje getPlanta() {
        return planta;
    }

    public void setPlanta(PlantaDeReciclaje planta) {
        this.planta = planta;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getToneladas() {
        return toneladas;
    }

    public void setToneladas(BigDecimal toneladas) {
        this.toneladas = toneladas;
    }
}