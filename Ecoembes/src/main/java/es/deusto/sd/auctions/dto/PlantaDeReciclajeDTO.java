package es.deusto.sd.auctions.dto;

import es.deusto.sd.auctions.Gateway.Tipo;

public class PlantaDeReciclajeDTO {
    private Tipo tipo;
    private String nombre;

    public PlantaDeReciclajeDTO(Tipo tipo, String nombre) {
        this.tipo = tipo;
        this.nombre = nombre;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo id) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

