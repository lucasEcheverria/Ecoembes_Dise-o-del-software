package org.example.service;

import org.example.dao.CapacidadRepository;
import org.example.entities.Capacidad;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PlasSbService {
    private CapacidadRepository capacidadRepository;

    public PlasSbService(CapacidadRepository capacidadRepository) {
        this.capacidadRepository = capacidadRepository;
    }

    public double get_capacidad_fecha(Date fecha){
        return capacidadRepository.findByFecha(fecha).getPeso();
    }

    public void post_capacidad_fecha(double peso, Date fecha){
        Capacidad c = new Capacidad(fecha, peso);
        capacidadRepository.save(c);
    }
}
