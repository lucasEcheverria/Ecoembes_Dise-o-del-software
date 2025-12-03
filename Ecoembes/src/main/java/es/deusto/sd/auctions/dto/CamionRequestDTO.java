package es.deusto.sd.auctions.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;

@Schema(description = "Datos para crear un camión con contenedores para una planta")
public class CamionRequestDTO {

    @Schema(description = "Fecha de asignación del camión", example = "2025-01-15", required = true)
    @NotNull(message = "La fecha es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fecha;

    @Schema(description = "Lista de IDs de contenedores a asignar al camión",
            example = "[1001, 1002, 1003]",
            required = true)
    @NotEmpty(message = "Debe incluir al menos un contenedor")
    @Size(min = 1, max = 100, message = "El camión debe tener entre 1 y 100 contenedores")
    private List<@Positive(message = "Los IDs de contenedores deben ser positivos") Long> contenedores;

    // Constructor vacío (requerido por Jackson para deserialización)
    public CamionRequestDTO() {
    }

    // Constructor con parámetros (útil para testing)
    public CamionRequestDTO(Date fecha, List<Long> contenedores) {
        this.fecha = fecha;
        this.contenedores = contenedores;
    }

    // Getters y Setters
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<Long> getContenedores() {
        return contenedores;
    }

    public void setContenedores(List<Long> contenedores) {
        this.contenedores = contenedores;
    }

    // toString para debugging
    @Override
    public String toString() {
        return "CamionRequestDTO{" +
                "fecha=" + fecha +
                ", contenedores=" + contenedores +
                " (total: " + (contenedores != null ? contenedores.size() : 0) + ")" +
                '}';
    }
}