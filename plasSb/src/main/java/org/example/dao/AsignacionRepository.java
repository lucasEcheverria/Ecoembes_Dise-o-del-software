package org.example.dao;

import java.util.Date;
import java.util.List;

import org.example.entities.Asignacion;
import org.example.entities.Asignacion.EstadoAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, String> {
    
    List<Asignacion> findByFecha(Date fecha);
    
    List<Asignacion> findByEstado(EstadoAsignacion estado);
    
    List<Asignacion> findByFechaAndEstado(Date fecha, EstadoAsignacion estado);
    
    List<Asignacion> findByFechaBetween(Date startDate, Date endDate);
    
    List<Asignacion> findByAsignadoPor(String asignadoPor);
    
    List<Asignacion> findAllByOrderByRecibidoEnDesc();
    
    List<Asignacion> findByFechaOrderByRecibidoEnDesc(Date fecha);
    
    @Query("SELECT COUNT(a) FROM Asignacion a WHERE a.estado = ?1")
    Long countByEstado(EstadoAsignacion estado);
    
    @Query("SELECT SUM(a.pesoEstimado) FROM Asignacion a WHERE a.fecha = ?1 AND a.estado != 'RECHAZADO'")
    Double getPesoTotalParaFecha(Date fecha);
    
    @Query("SELECT a FROM Asignacion a WHERE a.fecha >= CURRENT_DATE ORDER BY a.fecha, a.recibidoEn DESC")
    List<Asignacion> findAsignacionesFuturas();
}