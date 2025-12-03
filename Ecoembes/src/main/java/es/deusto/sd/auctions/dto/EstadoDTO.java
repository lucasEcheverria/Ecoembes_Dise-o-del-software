package es.deusto.sd.auctions.dto;

import java.util.Date;

public class EstadoDTO {
    private double cantidad;
    private Date fecha;

    public EstadoDTO(Double cantidad, Date fecha) {
        this.fecha = fecha;
        this.cantidad = cantidad;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
