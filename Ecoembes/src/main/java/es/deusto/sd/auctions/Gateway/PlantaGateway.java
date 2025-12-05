package es.deusto.sd.auctions.Gateway;

import java.util.Date;

public interface PlantaGateway {
    double consultarCapacidadDisponible(Date fecha);
    Tipo getTipo();
    String getNombre();
}
