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
    private double peso; // Capacidad total en toneladas
    
    @Column(name = "peso_usado", nullable = false)
    private double pesoUsado = 0.0; // Capacidad ya asignada
    
    @Column(name = "planta_id", nullable = false)
    private String plantaId = "PLASSB_001";
    
    @Column(name = "planta_nombre", nullable = false)
    private String plantaNombre = "PlasSB Ltd.";
    
    // Constructores
    public Capacidad() {}
    
    public Capacidad(Date fecha, double peso) {
        this.fecha = fecha;
        this.peso = peso;
        this.pesoUsado = 0.0;
    }
    
    // Métodos de negocio
    public double getPesoDisponible() {
        return peso - pesoUsado;
    }
    
    public boolean puedeAcomodar(double pesoRequerido) {
        return getPesoDisponible() >= pesoRequerido;
    }
    
    public void agregarPesoUsado(double pesoAgregar) {
        if (pesoAgregar < 0) {
            throw new IllegalArgumentException("El peso no puede ser negativo");
        }
        if (this.pesoUsado + pesoAgregar > this.peso) {
            throw new IllegalStateException("Excede la capacidad total");
        }
        this.pesoUsado += pesoAgregar;
    }
    
    public void liberarPeso(double pesoLiberar) {
        if (pesoLiberar < 0) {
            throw new IllegalArgumentException("El peso no puede ser negativo");
        }
        this.pesoUsado = Math.max(0, this.pesoUsado - pesoLiberar);
    }
    
    // Getters y Setters
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
    
    public double getPesoUsado() {
        return pesoUsado;
    }
    
    public void setPesoUsado(double pesoUsado) {
        this.pesoUsado = pesoUsado;
    }
    
    public String getPlantaId() {
        return plantaId;
    }
    
    public void setPlantaId(String plantaId) {
        this.plantaId = plantaId;
    }
    
    public String getPlantaNombre() {
        return plantaNombre;
    }
    
    public void setPlantaNombre(String plantaNombre) {
        this.plantaNombre = plantaNombre;
    }
    
    @Override
    public String toString() {
        return "Capacidad{" +
                "fecha=" + fecha +
                ", peso=" + peso +
                ", pesoUsado=" + pesoUsado +
                ", pesoDisponible=" + getPesoDisponible() +
                '}';
    }
}