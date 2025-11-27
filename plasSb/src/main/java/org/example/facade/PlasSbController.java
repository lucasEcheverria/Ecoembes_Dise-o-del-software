package org.example.facade;


import jakarta.validation.Valid;
import org.example.dao.CapacidadRepository;
import org.example.service.PlasSbService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/plasSb")
@Tag(name = "PlasSb controller", description = "Operaciones relacionadas con la planta de reciclaje PlasSb")
public class PlasSbController {
    private final PlasSbService plasSbService;

    public PlasSbController(PlasSbService plasSbService) {
        this.plasSbService = plasSbService;
    }

    @Operation(
            summary = "",
            description = "",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK:capacidad devuelta exitosamente"),
                    @ApiResponse(responseCode = "204", description = "La fecha no tiene nada almacenado"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/capacidad/{fecha}")
    public ResponseEntity<Double> get_capacidad_fecha(
            @Parameter(name = "fecha", description = "Fecha de la que se requiere la capacidad.", required = true, example = "01-01-2025")
            @PathVariable("fecha") String fecha
    ){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            Date fecha_format = sdf.parse(fecha);

            return new ResponseEntity<>(plasSbService.get_capacidad_fecha(fecha_format), HttpStatus.OK);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
