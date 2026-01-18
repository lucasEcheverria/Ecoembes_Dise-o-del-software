package main.proxies;

import main.data.Planta;

import java.util.List;

public interface IServiceProxy {
    List<Planta> getPlantas(String token);
    Double consultarCapacidadFecha(String token, String plantaId, String fecha);
}
