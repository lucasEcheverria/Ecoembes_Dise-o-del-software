package main.proxies;

import main.data.Planta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

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
}
