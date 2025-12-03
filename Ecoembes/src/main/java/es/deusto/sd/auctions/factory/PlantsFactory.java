package es.deusto.sd.auctions.factory;

import es.deusto.sd.auctions.Gateway.ConSocketGateway;
import es.deusto.sd.auctions.Gateway.PlantaGateway;
import es.deusto.sd.auctions.Gateway.PlasSbGateway;

public class PlantsFactory {
    private static PlantsFactory instance;

    private PlantsFactory() {}

    public static PlantsFactory getInstance() {
        if (instance == null) {
            instance = new PlantsFactory();
        }
        return instance;
    }

    public static PlantaGateway crear(String tipo, String url, String token) {
        if (tipo.equals("SpringBoot")) {
            return new PlasSbGateway(url, token);
        } else {
            return new ConSocketGateway(url, token);
        }
    }
}
