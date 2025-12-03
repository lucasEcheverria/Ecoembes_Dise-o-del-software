package es.deusto.sd.auctions.dto;

import es.deusto.sd.auctions.entity.Estado;

public class ContenedorDTO {
    long id;
    double cantidad;

    public ContenedorDTO(long id, double ultimo_estado) {
        this.id = id;
        this.cantidad = ultimo_estado;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double ultimo_estado) {
        this.cantidad = ultimo_estado;
    }
}
