package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del servidor PlasSB.
 * Planta de reciclaje que se comunica mediante REST.
 *
 * PUERTO: 8083 (configurado en application.properties)
 *
 * Para iniciar el servidor:
 *   ./gradlew bootRun
 *   o
 *   java -jar build/libs/plassb-server.jar
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}

