package es.deusto.sd.auctions.dto;

public class PlantaDeReciclajeDTO {
    private long id;
    private double capacidad;

    public PlantaDeReciclajeDTO(long id, double capacidad) {
        this.id = id;
        this.capacidad = capacidad;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(double capacidad) {
        this.capacidad = capacidad;
    }
}

