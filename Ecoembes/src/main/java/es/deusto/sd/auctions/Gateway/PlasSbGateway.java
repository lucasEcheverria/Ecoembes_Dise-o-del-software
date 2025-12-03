package es.deusto.sd.auctions.Gateway;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlasSbGateway implements PlantaGateway{

    private final String baseUrl;
    private final String token;
    private final RestTemplate restTemplate;

    public PlasSbGateway(String url, String token) {
        this.baseUrl = url;
        this.token = token;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public double consultarCapacidadDisponible(Date fecha) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String fechaFormateada = formatter.format(fecha);

        String url = baseUrl + "/plasSb/capacidad?fecha=" + fechaFormateada;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Double> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Double.class
        );

        return response.getBody();
    }
}
