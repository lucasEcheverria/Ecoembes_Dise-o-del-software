/**
 * Puede haber partes de código creadas con asistencia de la IA Claude Sonnet 4.5, estas están más
 * que verificadas su correcto funcionamiento junto con un trabajo por parte de los desarrolladores para
 * la comprensión de su funcionamiento
 */
package es.deusto.sd.auctions.facade;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

import es.deusto.sd.auctions.dto.CamionRequestDTO;
import es.deusto.sd.auctions.dto.ContenedorDTO;
import es.deusto.sd.auctions.dto.EstadoDTO;
import es.deusto.sd.auctions.dto.PlantaDeReciclajeDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.deusto.sd.auctions.service.EcoembesService;
import es.deusto.sd.auctions.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


//Hay que cambiar el /auctions y el blique entero
@RestController
@RequestMapping("/ecoembes")
@Tag(name = "Auctions Controller", description = "Operaciones relacionadas con contenedores, plantas de reciclaje y camiones")
public class EcoembesController {

	private final EcoembesService ecoembesService;
	private final AuthService authService;

	public EcoembesController(EcoembesService ecoembesService, AuthService authService) {
		this.ecoembesService = ecoembesService;
		this.authService = authService;
	}

    //Get estado de los contenedores por entre fechas
    @Operation(
            summary = "Get estado de los contenedores entre unas fechas.",
            description = "Devuelve todos los estados entre unas fechas ordenados cronológicamente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: lista de los estados ordenados devuleto exitosamente"),
                    @ApiResponse(responseCode = "204", description = "No Content: Contenedor no encontrado || El contenedor no tiene estados"),
                    @ApiResponse(responseCode = "404", description = "No existe ese contenedor"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/contenedores/estado/{id_contenedor}/{fecha_inicio}/{fecha_fin}")
    public ResponseEntity<List<EstadoDTO>> get_estado_fechas(
            @Parameter(name = "id_contenedor", description = "id del contenedor referido", required = true, example = "00001")
            @PathVariable("id_contenedor") String contenedor,
            @Parameter(name = "fecha_inicio", description = "fecha de inicio de los estados", required = true, example = "01-01-2025")
            @PathVariable("fecha_inicio") String fecha_inicio,
            @Parameter(name = "fecha_fin", description = "fecha de fin de los estados", required = true, example = "04-01-2025")
            @PathVariable("fecha_fin") String fecha_fin,
            @Parameter(description = "Token de autenticación del usuario", required = true, example = "abc123xyz")
            @RequestParam (name = "token") String token_usuario){
            try {
                /*if(!authService.valido(token_usuario)){
                    return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
                }*/

                String decoded_id_contenedor = URLDecoder.decode(contenedor, StandardCharsets.UTF_8);
                long id = Long.parseLong(decoded_id_contenedor);

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                sdf.setLenient(false);


                Date fecha_inicio_format = sdf.parse(fecha_inicio);
                Date fecha_fin_format = sdf.parse(fecha_fin);


                List<EstadoDTO> dtos = ecoembesService.consulta_entre_fechas(id, fecha_inicio_format, fecha_fin_format);

                return new ResponseEntity<>(dtos, HttpStatus.OK);
            } catch (RuntimeException e){
                System.out.println(e);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e){
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }

    //Get contenedores
    @Operation(
            summary = "Get contendores.",
            description = "Devuelve todos los contenedores con su estado actual",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: lista de los estados ordenados devuleto exitosamente"),
                    @ApiResponse(responseCode = "204", description = "No hay contenedores"),
                    @ApiResponse(responseCode = "404", description = "No hay contenedores, o están mal creados"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/contenedores")
    public ResponseEntity<List<ContenedorDTO>> get_contenedores(
            @Parameter(description = "Token de autenticación del usuario", required = true, example = "abc123xyz")
            @RequestParam (name = "token") String token_usuario){
        try {
            /*if(!authService.valido(token_usuario)){
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
            }*/
            return new ResponseEntity<>(ecoembesService.getContenedores(), HttpStatus.OK);
        } catch (RuntimeException e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Get plantas de reciclaje
    @Operation(
            summary = "Get todas las plantas de reciclaje",
            description = "Devuelve todas las plantas de reciclaje con su capadidad actual",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: lista de las plantas de reciclaje devuelta exitosamente"),
                    @ApiResponse(responseCode = "204", description = "No Content: No hay plantas de reciclaje"),
                    @ApiResponse(responseCode = "404", description = ""),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/pantas_de_reciclaje")
    public ResponseEntity<List<PlantaDeReciclajeDTO>> get_plantas(
            @Parameter(description = "Token de autenticación del usuario", required = true, example = "abc123xyz")
            @RequestParam (name = "token") String token_usuario){
        try {
            /*if(!authService.valido(token_usuario)){
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
            }*/

            return new ResponseEntity<>(ecoembesService.getPlantas(), HttpStatus.OK);
        } catch (RuntimeException e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //Get capacidad de una planta en una fecha dada
    @Operation(
            summary = "Get capacidad de una planta en una fecha determinada",
            description = "Devuelve la capacidad de una planta en una fecha determinada",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK:capacidad devuelta exitosamente"),
                    @ApiResponse(responseCode = "204", description = "No Content: planta inexistente"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/plantas/{id_planta}/{fecha}")
    public ResponseEntity<Double> get_capacidad_planta_fecha(
            @Parameter(name = "id_planta", description = "id de la planta", required = true, example = "00001")
            @PathVariable("id_planta") int planta,
            @Parameter(name = "fecha", description = "fecha de la que quiero la capacidad ", required = true, example = "01-01-2025")
            @PathVariable("fecha") String fecha,
            @Parameter(description = "Token de autenticación del usuario", required = true, example = "abc123xyz")
            @RequestParam (name = "token") String token_usuario){
        try {
            /*if(!authService.valido(token_usuario)){
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
            }*/

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            Date fecha_format = sdf.parse(fecha);

            return new ResponseEntity<>(ecoembesService.capacidad_planta_fecha(planta, fecha_format), HttpStatus.OK);
        } catch (RuntimeException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Post crea camiones que contienen los contenedores que irán a cada planta
    @Operation(
            summary = "Asigna a una planta contenedores sin superar su capacidad",
            description = "Asigna a una planta contenedores que pueden ir para no superar la capadicad total de esta, creando de forma significativa un camión",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content: Planta de reciclaje no encontrada"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: No puedes crear esto"),
                    @ApiResponse(responseCode = "404", description = "Not Found: Planta no encontrada"),
                    @ApiResponse(responseCode = "409", description = "Conflict: La planta ya está llena"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PostMapping("/plantas/{id_planta}/camiones_nuevo")
    public ResponseEntity<Object> post_contenedores_plantas(
            @Parameter(description = "ID de la planta de reciclaje", required = true, example = "1")
            @PathVariable("id_planta") Long idPlanta,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del camión: fecha y lista de contenedores",
                    required = true
            )
            @RequestBody @Valid CamionRequestDTO camionDTO,
            @Parameter(description = "Token de autenticación del usuario", required = true, example = "abc123xyz")
            @RequestParam (name = "token") String token_usuario){
        try {
            /*if(!authService.valido(token_usuario)){
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Este usuario no tiene permitido realizar este tipo de consultas.");
            }*/

            ecoembesService.crear_camion(camionDTO, idPlanta);

            return ResponseEntity.status(HttpStatus.CREATED).body("Camión creado exitosamente con " /*+ camionDTO.getContenedores().size()*/ + " contenedores");

        } catch (IllegalArgumentException e) {
            System.err.println("✗ ERROR de validación: " + e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body("Error de validación: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("✗ ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el camión: " + e.getMessage());
        }
    }
}