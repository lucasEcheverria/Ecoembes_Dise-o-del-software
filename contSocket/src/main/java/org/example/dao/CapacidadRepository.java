package org.example.dao;

import org.example.entities.Capacidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface CapacidadRepository extends JpaRepository<Capacidad, Long> {
    Capacidad findByFecha(Date fecha);
}
