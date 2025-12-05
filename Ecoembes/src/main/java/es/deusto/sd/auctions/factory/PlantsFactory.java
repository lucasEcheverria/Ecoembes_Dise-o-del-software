package es.deusto.sd.auctions.factory;

import es.deusto.sd.auctions.Gateway.ConSocketGateway;
import es.deusto.sd.auctions.Gateway.PlantaGateway;
import es.deusto.sd.auctions.Gateway.PlasSbGateway;
import es.deusto.sd.auctions.Gateway.Tipo;

public class PlantsFactory {
    private static PlantsFactory instance;

    private PlantsFactory() {}

    public static PlantsFactory getInstance() {
        if (instance == null) {
            instance = new PlantsFactory();
        }
        return instance;
    }

    public static PlantaGateway crear(String url, Tipo tipo, String nombre) {
        switch (tipo) {
            case HTTP -> {
                return new PlasSbGateway(url, tipo,nombre);
            }
            case Sockets -> {
                return new ConSocketGateway(url, tipo, nombre);
            }
        }
        return null;
    }
}
