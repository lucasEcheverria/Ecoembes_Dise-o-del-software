package es.deusto.sd.auctions.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.HashMap;

@Entity
public class PlantaDeReciclaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "cantidad_total", nullable = false)
    private double capacidad_total;

    @Column(name = "cantidad_actual", nullable = false)
    private double capacidad_actual;


    public PlantaDeReciclaje(double capacidad_total, long id) {
        this.id = id;
        this.capacidad_total = capacidad_total;
    }

    public PlantaDeReciclaje(){}

    public double getCapacidad_total() {
        return capacidad_total;
    }

    public void setCapacidad_total(double capacidad_total) {
        this.capacidad_total = capacidad_total;
    }

    public double getCapacidad_actual() {
        return capacidad_actual;
    }

    public void setCapacidad_actual(double capacidad_actual) {
        this.capacidad_actual = capacidad_actual;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
