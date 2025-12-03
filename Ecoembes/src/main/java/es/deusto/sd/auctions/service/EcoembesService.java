package es.deusto.sd.auctions.service;

import es.deusto.sd.auctions.Gateway.PlantaGateway;
import es.deusto.sd.auctions.dao.*;
import es.deusto.sd.auctions.dto.CamionRequestDTO;
import es.deusto.sd.auctions.dto.ContenedorDTO;
import es.deusto.sd.auctions.dto.EstadoDTO;
import es.deusto.sd.auctions.dto.PlantaDeReciclajeDTO;
import es.deusto.sd.auctions.entity.Camion;
import es.deusto.sd.auctions.entity.Contenedor;
import es.deusto.sd.auctions.entity.Estado;
import es.deusto.sd.auctions.factory.PlantsFactory;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.http.HttpHeaders;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EcoembesService {
    private final CamionRepository camionRepository;
    private final ContenedorRepository contenedorRepository;
    private final EstadoRepository estadosRepository;
    private PlantsFactory factory;

    private PlantaGateway[] plantas;


    public EcoembesService(CamionRepository camionRepository, ContenedorRepository contenedorRepository,
                           EstadoRepository estadosRepository) {
        this.camionRepository = camionRepository;
        this.contenedorRepository = contenedorRepository;
        this.estadosRepository = estadosRepository;

        plantas = new PlantaGateway[2];

        factory = PlantsFactory.getInstance();
        plantas[0] = factory.crear("SpringBoot", "http://localhost:8083", "token-123");
        //plantas[1] = factory.crear("Sockets", "http://localhost:8084", "token-123");
    }

    //Get estado de los contenedores entre fechas
    public List<EstadoDTO> consulta_entre_fechas(long id, Date inicio, Date fin){
        /**
         * Este metodo devolverá la lista con un treemap de fecha-estado de un contenedor en concreto.
         */

        Contenedor contenedor = contenedorRepository.findById(id).orElse(null);

        if (contenedor == null) {
            return Collections.emptyList();
        }

        List<Estado> estados = estadosRepository.findByContenedorAndFechaBetween(contenedor, inicio, fin);

        List<EstadoDTO> result = new ArrayList<>();

        estados.forEach(estado -> {result.add(new EstadoDTO(estado.getCantidad(), estado.getFecha()));});

        return result;
    }

    //Get estado de una planta en una fecha determinada
    public double capacidad_planta_fecha(int  planta, Date fecha){

        if(planta == 1){
            return plantas[0].consultarCapacidadDisponible(fecha);
        }
        return 00;
    }

    //Post crear un camión
    @Transactional
    public void crear_camion(CamionRequestDTO dto, long id_planta) {
        /**
         * Con este metodo podemos añadir un camión, estos están organizados en un hashMap que tiene como clave las plantas de reciclaje,
         * y como valor un arraylist de todos los camiones que van a esta. Los camiones tienen dentro un array con todos los ids de los
         * contenedores que conteienen, además de un id de la planta y una fecha que indican cuando y donde van.
         */

    }

    public List<ContenedorDTO> getContenedores(){
        ArrayList<ContenedorDTO> result = new ArrayList<>();

        contenedorRepository.findAll().forEach(c ->{result.add(new ContenedorDTO(c.getId(), c.getEstado().getCantidad())); System.out.println(c.toString());});

        return result;
    }

    @Transactional
    public List<PlantaDeReciclajeDTO> getPlantas(){
        List<PlantaDeReciclajeDTO> result = new ArrayList<>();

        for(int i = 0; i < plantas.length; i++){
            result.add(new PlantaDeReciclajeDTO(i));
        }

        return result;
    }

}