package org.example.service;

import org.example.dao.CapacidadRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PlasSbService {
    private CapacidadRepository capacidadRepository;

    public PlasSbService(CapacidadRepository capacidadRepository) {
        this.capacidadRepository = capacidadRepository;
    }

    public double get_capacidad_fecha(Date fecha ){
        System.out.println(capacidadRepository.count());

        System.out.println(capacidadRepository.findAll());

        return capacidadRepository.findByFecha(fecha).getPeso();
    }
}
