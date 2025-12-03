package es.deusto.sd.auctions.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
public class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private tipo llenado;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @Column(name = "cantidad", nullable = false)
    private double cantidad;

    // ⭐ ESTO FALTA - La FK hacia Contenedor
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contenedor_id", nullable = false)
    private Contenedor contenedor;

    public enum tipo {
        Verde,   // 0% - 80%
        Naranja, // 80%-100%
        Rojo     // 100%
    }

    // Constructor actualizado
    public Estado(Date fecha, double cantidad, Contenedor contenedor) {
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.contenedor = contenedor;

        // Calcular el tipo según la cantidad
        if (cantidad >= 0 && cantidad <= 0.80) {
            this.llenado = tipo.Verde;
        } else if (cantidad > 0.80 && cantidad < 1.0) {
            this.llenado = tipo.Naranja;
        } else {
            this.llenado = tipo.Rojo;
        }
    }

    public Estado() {}

    // Getters y setters
    public Contenedor getContenedor() {
        return contenedor;
    }

    public void setContenedor(Contenedor contenedor) {
        this.contenedor = contenedor;
    }

    public tipo getLlenado() {
        return llenado;
    }

    public void setLlenado(tipo llenado) {
        this.llenado = llenado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return fecha.equals(((Estado) o).fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(llenado, fecha);
    }
}
