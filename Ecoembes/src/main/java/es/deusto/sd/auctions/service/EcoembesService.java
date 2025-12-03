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
import es.deusto.sd.auctions.entity.PlantaDeReciclaje;
import es.deusto.sd.auctions.factory.PlantsFactory;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EcoembesService {
    private final CamionRepository camionRepository;
    private final ContenedorRepository contenedorRepository;
    private final EstadoRepository estadosRepository;

    private PlantsFactory factory;
    private PlantaGateway[] plantas;


    public EcoembesService(CamionRepository camionRepository, ContenedorRepository contenedorRepository, PlantaDeReciclajeRepository plantasRepository,
                           EstadoRepository estadosRepository, RegistroResiduosPlantaRepository registrosResiduosPlantaRepository) {
        this.camionRepository = camionRepository;
        this.contenedorRepository = contenedorRepository;
        this.estadosRepository = estadosRepository;

        plantas = new  PlantaGateway[2];

        factory = PlantsFactory.getInstance();

        // PLANTA 1: PlasSB (REST) - Puerto 8083
        plantas[0] = factory.crear("SpringBoot", "http://localhost:8083", "token-plassb-123");
        System.out.println("✓ Planta 1 (PlasSB - REST) configurada en http://localhost:8083");

        // PLANTA 2: ContSocket (TCP) - Puerto 8090
        plantas[1] = factory.crear("Socket", "localhost:8090", "token-contsocket-456");
        System.out.println("✓ Planta 2 (ContSocket - Socket) configurada en localhost:8090");

        System.out.println("✓ EcoembesService inicializado con " + plantas.length + " plantas");
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
    public double capacidad_planta_fecha(int  id, Date fecha){
        if(id == 0){
            return plantas[0].consultarCapacidadDisponible(fecha);
        }else{
            return plantas[1].consultarCapacidadDisponible(fecha);
        }
    }

    //Post crear un camión
    @Transactional
    public void crear_camion(CamionRequestDTO dto, long id_planta) {
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