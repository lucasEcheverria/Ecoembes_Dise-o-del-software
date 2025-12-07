package es.deusto.sd.auctions.service;

import es.deusto.sd.auctions.Gateway.PlantaGateway;
import es.deusto.sd.auctions.Gateway.Tipo;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EcoembesService {
    @Autowired
    private final CamionRepository camionRepository;
    @Autowired
    private final ContenedorRepository contenedorRepository;
    @Autowired
    private final EstadoRepository estadosRepository;

    private PlantsFactory factory;
    private PlantaGateway[] plantas;
    private HashMap<String, PlantaGateway> plantasMap;


    public EcoembesService(CamionRepository camionRepository, ContenedorRepository contenedorRepository,
                           EstadoRepository estadosRepository) {
        this.camionRepository = camionRepository;
        this.contenedorRepository = contenedorRepository;
        this.estadosRepository = estadosRepository;

        plantas = new  PlantaGateway[2];
        plantasMap = new HashMap();

        factory = PlantsFactory.getInstance();

        // PLANTA 1: PlasSB (REST) - Puerto 8083
        plantas[0] = factory.crear("http://localhost:8085", Tipo.HTTP, "PlasSb");
        plantasMap.put("PlasSb", plantas[0]);
        System.out.println("✓ Planta 1 (PlasSB - REST) configurada en http://localhost:8085");

        // PLANTA 2: ContSocket (TCP) - Puerto 8090
        plantas[1] = factory.crear("localhost:8090", Tipo.Sockets, "ConSockets");
        plantasMap.put("ConSocket", plantas[1]);
        System.out.println("✓ Planta 2 (ContSocket - Socket) configurada en localhost:8090");

        System.out.println("✓ EcoembesService inicializado con " + plantas.length + " plantas");


        try {
            System.out.println("PlasSb");
            // 1. Crear la fecha: 01/01/2025
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date fecha = sdf.parse("01-01-2025");

            System.out.println("📅 Consultando capacidad para: " + sdf.format(fecha));

            // 2. Consultar a través del Gateway (usa plantas[0] internamente)
            double capacidad = capacidad_planta_fecha("PlasSb", new Date(125, 00, 01));

            // 3. Mostrar resultado
            System.out.println("✅ Capacidad obtenida: " + capacidad + " toneladas");
            System.out.println("🎯 Gateway funcionando correctamente\n");

        } catch (Exception e) {
            System.err.println("❌ Error al consultar capacidad: " + e.getMessage());
            System.err.println("💡 Verifica que PlasSB esté corriendo en localhost:8085");
            e.printStackTrace();
        }

        try {
            System.out.println("ConSocket");
            // 1. Crear la fecha: 01/01/2025
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date fecha = sdf.parse("01-01-2025");

            System.out.println("📅 Consultando capacidad para: " + sdf.format(fecha));

            // 2. Consultar a través del Gateway (usa plantas[0] internamente)
            double capacidad = capacidad_planta_fecha("ConSocket", new Date(125, 00, 01));

            // 3. Mostrar resultado
            System.out.println("✅ Capacidad obtenida: " + capacidad + " toneladas");
            System.out.println("🎯 Gateway funcionando correctamente\n");

        } catch (Exception e) {
            System.err.println("❌ Error al consultar capacidad: " + e.getMessage());
            System.err.println("💡 Verifica que ContSocket esté corriendo en localhost:8085");
            e.printStackTrace();
        }
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
    /*
    Si hubiese más plantas lo que haríamos sería buscar antes por el nombre en un Map cual es la que se referencia
    y después el mismo switch
     */
    public double capacidad_planta_fecha(String planta, Date fecha){
        double result = plantasMap.get(planta).consultarCapacidadDisponible(fecha);
        return result;
    }

    //Post crear un camión
    @Transactional
    public void crear_camion(CamionRequestDTO dto, String planta) {
        if(!plantasMap.containsKey(planta) || planta == null){
            throw new IllegalArgumentException("La planta" + planta + " no existe; o no estas añadiendo ninguna");
        }

        List<Contenedor> contenedores = contenedorRepository.findAllById(dto.getContenedores());

        Camion camion = new Camion(contenedores, planta, dto.getFecha());
        camionRepository.save(camion);
    }

    public List<ContenedorDTO> getContenedores(){
        ArrayList<ContenedorDTO> result = new ArrayList<>();

        contenedorRepository.findAll().forEach(c ->{result.add(new ContenedorDTO(c.getId(), c.getEstado().getCantidad())); System.out.println(c.toString());});

        return result;
    }

    @Transactional
    public List<PlantaDeReciclajeDTO> getPlantas(){
        List<PlantaDeReciclajeDTO> result = new ArrayList<>();

        for (String planta : plantasMap.keySet()) {
            result.add(new PlantaDeReciclajeDTO(plantasMap.get(planta).getTipo(), plantasMap.get(planta).getNombre()));
        }
        return result;
    }

}