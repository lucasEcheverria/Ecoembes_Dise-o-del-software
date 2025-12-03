package es.deusto.sd.auctions.Gateway;

import java.util.Date;

public class ConSocketGateway implements PlantaGateway{
    public ConSocketGateway(String url, String token) {
    }

    @Override
    public double consultarCapacidadDisponible(Date fecha) {
        return 0;
    }
}
