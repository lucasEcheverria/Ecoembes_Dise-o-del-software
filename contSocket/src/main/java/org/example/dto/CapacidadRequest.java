package org.example.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class CapacidadRequest{
    @NotNull(message = "La fecha no puede ser nula")
    @Pattern(regexp = "\\d{2}-\\d{2}-\\d{4}", message = "Formato de fecha inválido. Use dd-MM-yyyy")
    private String fecha;

    @NotNull(message = "La capacidad no puede ser nula")
    @Positive(message = "La capacidad debe ser positiva")
    private Double capacidad;

    public CapacidadRequest(String fecha, Double capacidad) {
        this.fecha = fecha;
        this.capacidad = capacidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Double getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Double capacidad) {
        this.capacidad = capacidad;
    }
}
