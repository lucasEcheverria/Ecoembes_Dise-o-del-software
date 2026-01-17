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

    @Value("${api.base.url")
    private String apiBaseUrl;

    public RestTemplateServiceProxy(RestTemplate restTemplate){this.restTemplate = restTemplate;}

    @Override
    public List<Planta> getPlantas(String token) {
        String url = apiBaseUrl + "/ecoembes/pantas_de_reciclaje?token=" + token;

        try {
            Planta[] array = restTemplate.getForObject(url, Planta[].class);
            return Arrays.asList(array);
        }
        catch (HttpStatusCodeException e) {
            switch (e.getStatusCode().value()) {
                case 404 -> throw new RuntimeException("No categories found.");
                default -> throw new RuntimeException("Failed to retrieve categories: " + e.getStatusText());
            }
        }
    }
}
