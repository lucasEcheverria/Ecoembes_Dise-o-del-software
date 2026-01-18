package main.proxies;

import main.data.Contenedor;
import main.data.Estado;
import main.data.Planta;

import java.util.List;

public interface IServiceProxy {
    List<Planta> getPlantas(String token);
    Double consultarCapacidadFecha(String token, String plantaId, String fecha);
    List<Contenedor>  getContenedores(String token);
    List<Estado> getHistorialContenedor(Long id, String fechaInicio, String fechaFin, String token);
}
