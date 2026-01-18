package main.proxies;

import main.data.Contenedor;
import main.data.Estado;
import main.data.Planta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestTemplateServiceProxy implements IServiceProxy{
    private final RestTemplate restTemplate;

    @Value("${api.base.url}")
    private String apiBaseUrl;

    public RestTemplateServiceProxy(RestTemplate restTemplate){this.restTemplate = restTemplate;}

    @Override
    public List<Planta> getPlantas(String token) {
        String url = apiBaseUrl + "/ecoembes/pantas_de_reciclaje?token=" + token;
        System.out.println("URL: " + url);
        try {
            Planta[] array = restTemplate.getForObject(url, Planta[].class);
            System.out.println("info solicitada" + Arrays.toString(array));
            return Arrays.asList(array);
        }
        catch (Exception e) {
            System.err.println("ERROR: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve plantas: " + e.getMessage());
        }
    }

    @Override
    public Double consultarCapacidadFecha(String token, String plantaId, String fecha) {
        String url = apiBaseUrl + "/ecoembes/plantas/" + plantaId + "/" + fecha + "?token=" + token;

        try{
            return restTemplate.getForObject(url, Double.class);
        }catch (Exception e) {
            System.err.println("ERROR: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve plantas: " + e.getMessage());
        }
    }

    @Override
    public List<Contenedor> getContenedores(String token) {
        String url = apiBaseUrl + "/ecoembes/contenedores?token=" + token;
        try {
            Contenedor[] array = restTemplate.getForObject(url, Contenedor[].class);
            return Arrays.asList(array);
        }catch (Exception e) {
            System.err.println("ERROR: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve contenedores: " + e.getMessage());
        }
    }

    @Override
    public List<Estado> getHistorialContenedor(Long id, String fechaInicio, String fechaFin, String token) {
        String url = apiBaseUrl + "/ecoembes/contenedores/estado/" + id
                + "?fecha_inicio=" + fechaInicio
                + "&fecha_fin=" + fechaFin
                + "&token=" + token;
        try {
            Estado[] array = restTemplate.getForObject(url, Estado[].class);
            return Arrays.asList(array);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("No hay datos");
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener historial");
        }
    }

    @Override
    public void crearNuevoCamion(List<Long> contenedores, String planta, String fecha, String token) {
        String url = apiBaseUrl + "/ecoembes/plantas/" + planta + "/camiones_nuevo?token=" + token;

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("contenedores", contenedores);
            body.put("fecha", fecha);

            restTemplate.postForEntity(url, body, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear camión: " + e.getMessage());
        }
    }

}
