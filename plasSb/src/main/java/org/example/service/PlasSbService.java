package org.example.service;

import org.example.dao.AsignacionRepository;
import org.example.dao.CapacidadRepository;
import org.example.entities.Asignacion;
import org.example.entities.Capacidad;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PlasSbService {
    private final CapacidadRepository capacidadRepository;
    private final AsignacionRepository asignacionRepository;

    public PlasSbService(CapacidadRepository capacidadRepository, AsignacionRepository asignacionRepository) {
        this.capacidadRepository = capacidadRepository;
        this.asignacionRepository = asignacionRepository;
    }
    
    // Operaciones de capacidad
    public double get_capacidad_fecha(Date fecha){
        Capacidad capacidad = capacidadRepository.findByFecha(fecha);
        return capacidad != null ? capacidad.getPeso() : 0.0;
    }

    public void post_capacidad_fecha(double peso, Date fecha){
        Capacidad c = new Capacidad(fecha, peso);
        capacidadRepository.save(c);
    }
    
    @Transactional
    public Capacidad registrarCapacidad(Date fecha, Double capacidad) {
        // Validar capacidad positiva
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser positiva");
        }
        
        // Si ya existe, actualizar
        Capacidad existente = capacidadRepository.findByFecha(fecha);
        Capacidad capacidadEntity;
        
        if (existente != null) {
            capacidadEntity = existente;
            // Verificar que la nueva capacidad no sea menor que la ya usada
            if (capacidad < capacidadEntity.getPesoUsado()) {
                throw new IllegalArgumentException(
                    "La nueva capacidad (" + capacidad + ") no puede ser menor que la capacidad usada (" + 
                    capacidadEntity.getPesoUsado() + ")"
                );
            }
            capacidadEntity.setPeso(capacidad);
        } else {
            capacidadEntity = new Capacidad(fecha, capacidad);
        }
        
        return capacidadRepository.save(capacidadEntity);
    }
    
    public Capacidad obtenerCapacidadPorFecha(Date fecha) {
        return capacidadRepository.findByFecha(fecha);
    }
    
    public List<Capacidad> obtenerCapacidadesFuturas() {
        return capacidadRepository.findCapacidadesFuturas();
    }
    
    public List<Capacidad> obtenerCapacidadesEntreFechas(Date fechaInicio, Date fechaFin) {
        return capacidadRepository.findByFechaBetween(fechaInicio, fechaFin);
    }
    
    @Transactional
    public void eliminarCapacidad(Date fecha) {
        capacidadRepository.deleteByFecha(fecha);
    }
    
    // ============================================
    // Operaciones de Asignaciones
    // ============================================
    
    @Transactional
    public Asignacion recibirAsignacion(
            Date fecha,
            Integer totalContenedores,
            Integer totalEnvases,
            Double pesoEstimado,
            String asignadoPor,
            List<String> contenedorIds) {
        
        // Validaciones básicas
        if (totalContenedores <= 0 || totalEnvases < 0 || pesoEstimado < 0) {
            throw new IllegalArgumentException("Parámetros de asignación inválidos");
        }
        
        // Crear asignación
        Asignacion asignacion = new Asignacion(
            fecha, totalContenedores, totalEnvases, pesoEstimado, asignadoPor, contenedorIds
        );
        
        // Verificar capacidad disponible
        Capacidad capacidadDisponible = capacidadRepository.findByFecha(fecha);
        
        if (capacidadDisponible == null) {
            asignacion.setEstado(Asignacion.EstadoAsignacion.RECHAZADO);
            asignacion.setMensaje("No hay capacidad registrada para la fecha: " + fecha);
            return asignacionRepository.save(asignacion);
        }
        
        Capacidad capacidad = capacidadDisponible;
        
        if (!capacidad.puedeAcomodar(pesoEstimado)) {
            asignacion.setEstado(Asignacion.EstadoAsignacion.RECHAZADO);
            asignacion.setMensaje("Capacidad insuficiente. Requerida: " + pesoEstimado + 
                                " toneladas, Disponible: " + capacidad.getPesoDisponible() + " toneladas");
            return asignacionRepository.save(asignacion);
        }
        
        // Aceptar asignación
        capacidad.agregarPesoUsado(pesoEstimado);
        capacidadRepository.save(capacidad);
        
        asignacion.setMensaje("Asignación aceptada exitosamente");
        return asignacionRepository.save(asignacion);
    }
    
    public Optional<Asignacion> obtenerAsignacionPorId(String id) {
        return asignacionRepository.findById(id);
    }
    
    public List<Asignacion> listarAsignaciones(Date fecha, String estadoStr) {
        Asignacion.EstadoAsignacion estado = null;
        if (estadoStr != null && !estadoStr.isEmpty()) {
            try {
                estado = Asignacion.EstadoAsignacion.valueOf(estadoStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Estado inválido: " + estadoStr);
            }
        }
        
        if (fecha != null && estado != null) {
            return asignacionRepository.findByFechaAndEstado(fecha, estado);
        } else if (fecha != null) {
            return asignacionRepository.findByFechaOrderByRecibidoEnDesc(fecha);
        } else if (estado != null) {
            return asignacionRepository.findByEstado(estado);
        } else {
            return asignacionRepository.findAllByOrderByRecibidoEnDesc();
        }
    }
    
    @Transactional
    public Asignacion actualizarEstadoAsignacion(String asignacionId, String nuevoEstadoStr) {
        Asignacion asignacion = asignacionRepository.findById(asignacionId)
            .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada: " + asignacionId));
        
        Asignacion.EstadoAsignacion nuevoEstado;
        try {
            nuevoEstado = Asignacion.EstadoAsignacion.valueOf(nuevoEstadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: " + nuevoEstadoStr);
        }
        
        // No permitir cambiar estado de RECHAZADO o COMPLETADO
        if (asignacion.getEstado() == Asignacion.EstadoAsignacion.RECHAZADO) {
            throw new IllegalStateException("No se puede cambiar el estado de una asignación rechazada");
        }
        if (asignacion.getEstado() == Asignacion.EstadoAsignacion.COMPLETADO) {
            throw new IllegalStateException("No se puede cambiar el estado de una asignación completada");
        }
        
        asignacion.setEstado(nuevoEstado);
        return asignacionRepository.save(asignacion);
    }
    
    public List<Asignacion> obtenerAsignacionesFuturas() {
        return asignacionRepository.findAsignacionesFuturas();
    }
    
    public Long contarPorEstado(Asignacion.EstadoAsignacion estado) {
        return asignacionRepository.countByEstado(estado);
    }
}