package org.example.facade;

import org.example.entities.Asignacion;
import org.example.entities.Capacidad;
import org.example.service.PlasSbService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/plasSb")
@CrossOrigin(origins = "*")
@Tag(name = "PlasSb Controller", description = "Operaciones relacionadas con la planta de reciclaje PlasSb")
public class PlasSbController {
    
    private final PlasSbService plasSbService;
    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public PlasSbController(PlasSbService plasSbService) {
        this.plasSbService = plasSbService;
        this.isoDateFormat.setLenient(false);
    }
  // Endpoints de capacidad    
    @Operation(
            summary = "Obtener detalles completos de capacidad",
            description = "Devuelve información detallada sobre la capacidad de una fecha específica",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Capacidad encontrada"),
                    @ApiResponse(responseCode = "404", description = "No se encontró capacidad para la fecha")
            }
    )
    @GetMapping("/api/capacity")
    public ResponseEntity<?> getCapacity(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        try {
            Capacidad capacidad = plasSbService.obtenerCapacidadPorFecha(date);
            
            if (capacidad == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontró capacidad para la fecha: " + isoDateFormat.format(date)));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("date", isoDateFormat.format(capacidad.getFecha()));
            response.put("totalCapacity", capacidad.getPeso());
            response.put("usedCapacity", capacidad.getPesoUsado());
            response.put("availableCapacity", capacidad.getPesoDisponible());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(
            summary = "Registrar capacidad",
            description = "Registra capacidad usando formato JSON",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Capacidad creada"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            }
    )
    @PostMapping("/api/capacity")
    public ResponseEntity<?> registerCapacity(@RequestBody Map<String, Object> request) {
        try {
            String dateStr = (String) request.get("date");
            Double capacity = ((Number) request.get("capacity")).doubleValue();
            
            Date fecha = isoDateFormat.parse(dateStr);
            Capacidad capacidad = plasSbService.registrarCapacidad(fecha, capacity);
            
            Map<String, Object> data = new HashMap<>();
            data.put("date", isoDateFormat.format(capacidad.getFecha()));
            data.put("totalCapacity", capacidad.getPeso());
            data.put("usedCapacity", capacidad.getPesoUsado());
            data.put("availableCapacity", capacidad.getPesoDisponible());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Capacidad registrada exitosamente");
            response.put("data", data);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Error interno del servidor: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "Listar capacidades futuras",
            description = "Devuelve todas las capacidades registradas para fechas futuras"
    )
    @GetMapping("/api/capacity/future")
    public ResponseEntity<?> getFutureCapacities() {
        try {
            List<Capacidad> capacidades = plasSbService.obtenerCapacidadesFuturas();
            
            List<Map<String, Object>> capacityList = capacidades.stream()
                .map(c -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", isoDateFormat.format(c.getFecha()));
                    item.put("totalCapacity", c.getPeso());
                    item.put("usedCapacity", c.getPesoUsado());
                    item.put("availableCapacity", c.getPesoDisponible());
                    return item;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of("capacities", capacityList, "total", capacityList.size()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Endpoints de Asignaciones
    
    @Operation(
            summary = "Recibir nueva asignación",
            description = "Recibe y procesa una nueva asignación de contenedores",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Asignación procesada"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            }
    )
    @PostMapping("/api/assignments")
    public ResponseEntity<?> receiveAssignment(@RequestBody Map<String, Object> request) {
        try {
            String dateStr = (String) request.get("date");
            Date fecha = isoDateFormat.parse(dateStr);
            
            Integer totalContainers = ((Number) request.get("totalContainers")).intValue();
            Integer totalPackages = ((Number) request.get("totalPackages")).intValue();
            Double totalWeight = ((Number) request.get("totalWeight")).doubleValue();
            String assignedBy = (String) request.get("assignedBy");
            
            // Extraer IDs de contenedores
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> containersList = (List<Map<String, Object>>) request.get("containers");
            List<String> containerIds = containersList.stream()
                .map(c -> (String) c.get("containerId"))
                .collect(Collectors.toList());
            
            Asignacion asignacion = plasSbService.recibirAsignacion(
                fecha, totalContainers, totalPackages, totalWeight, assignedBy, containerIds
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("assignmentId", asignacion.getId());
            response.put("accepted", asignacion.getEstado() != Asignacion.EstadoAsignacion.RECHAZADO);
            response.put("message", asignacion.getMensaje());
            response.put("date", isoDateFormat.format(asignacion.getFecha()));
            response.put("totalContainers", asignacion.getTotalContenedores());
            response.put("totalPackages", asignacion.getTotalEnvases());
            response.put("estimatedWeight", asignacion.getPesoEstimado());
            response.put("status", asignacion.getEstado().toString());
            response.put("receivedAt", asignacion.getRecibidoEn().toString());
            response.put("containerIds", asignacion.getContenedorIds());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(
            summary = "Obtener asignación por ID",
            description = "Devuelve los detalles de una asignación específica"
    )
    @GetMapping("/api/assignments/{id}")
    public ResponseEntity<?> getAssignment(@PathVariable String id) {
        try {
            Asignacion asignacion = plasSbService.obtenerAsignacionPorId(id)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada: " + id));
            
            Map<String, Object> response = new HashMap<>();
            response.put("assignmentId", asignacion.getId());
            response.put("date", isoDateFormat.format(asignacion.getFecha()));
            response.put("totalContainers", asignacion.getTotalContenedores());
            response.put("totalPackages", asignacion.getTotalEnvases());
            response.put("estimatedWeight", asignacion.getPesoEstimado());
            response.put("assignedBy", asignacion.getAsignadoPor());
            response.put("status", asignacion.getEstado().toString());
            response.put("receivedAt", asignacion.getRecibidoEn().toString());
            response.put("containerIds", asignacion.getContenedorIds());
            response.put("message", asignacion.getMensaje());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(
            summary = "Listar asignaciones",
            description = "Lista asignaciones con filtros opcionales por fecha y estado"
    )
    @GetMapping("/api/assignments")
    public ResponseEntity<?> listAssignments(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam(required = false) String status) {
        try {
            List<Asignacion> asignaciones = plasSbService.listarAsignaciones(date, status);
            
            List<Map<String, Object>> assignmentList = asignaciones.stream()
                .map(a -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("assignmentId", a.getId());
                    item.put("date", isoDateFormat.format(a.getFecha()));
                    item.put("totalContainers", a.getTotalContenedores());
                    item.put("totalPackages", a.getTotalEnvases());
                    item.put("estimatedWeight", a.getPesoEstimado());
                    item.put("status", a.getEstado().toString());
                    item.put("receivedAt", a.getRecibidoEn().toString());
                    return item;
                })
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("assignments", assignmentList);
            response.put("total", assignmentList.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(
            summary = "Actualizar estado de asignación",
            description = "Cambia el estado de una asignación existente"
    )
    @PutMapping("/api/assignments/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable String id, 
            @RequestBody Map<String, String> request) {
        try {
            String newStatus = request.get("status");
            Asignacion asignacion = plasSbService.actualizarEstadoAsignacion(id, newStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("assignmentId", asignacion.getId());
            response.put("newStatus", asignacion.getEstado().toString());
            response.put("updatedAt", asignacion.getRecibidoEn().toString());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    // Endpoint de Información de Planta
    
    @Operation(
            summary = "Información de la planta",
            description = "Devuelve información general sobre la planta de reciclaje"
    )
    @GetMapping("/api/plant/info")
    public ResponseEntity<?> getPlantInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("plantId", "PLASSB_001");
        info.put("name", "PlasSB Ltd.");
        info.put("type", "SPRING_BOOT");
        info.put("location", "Madrid, Spain");
        info.put("status", "OPERATIONAL");
        
        return ResponseEntity.ok(info);
    }
}