package org.example.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "CAPACIDAD")
public class Capacidad {
    @Id
    @Temporal(TemporalType.DATE)
    @Column(name = "fecha", nullable = false, unique = true)
    private Date fecha;

    @Column(name = "peso", nullable = false)
    private double peso;

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public Capacidad(Date fecha, double peso) {
        this.fecha = fecha;
        this.peso = peso;
    }

    public Capacidad(){}
}
