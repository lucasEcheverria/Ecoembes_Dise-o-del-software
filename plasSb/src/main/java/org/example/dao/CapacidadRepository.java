package org.example.dao;

import org.example.entities.Capacidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.entities.Capacidad;
import org.example.entities.Asignacion;
import org.example.entities.Asignacion.EstadoAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CapacidadRepository extends JpaRepository<Capacidad, Date> {
    Capacidad findByFecha(Date fecha);
    List<Capacidad> findByFechaBetween(Date startDate, Date endDate);
    
    List<Capacidad> findByFechaAfter(Date fecha);
    
    List<Capacidad> findByFechaGreaterThanEqualOrderByFechaAsc(Date fecha);
    
    boolean existsByFecha(Date fecha);
    
    void deleteByFecha(Date fecha);
    
    @Query("SELECT c FROM Capacidad c WHERE c.fecha >= CURRENT_DATE ORDER BY c.fecha")
    List<Capacidad> findCapacidadesFuturas();
    
    @Query("SELECT SUM(c.peso) FROM Capacidad c WHERE c.fecha BETWEEN ?1 AND ?2")
    Double getCapacidadTotalEntreFechas(Date startDate, Date endDate); 
}