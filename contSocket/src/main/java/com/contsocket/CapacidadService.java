package com.contsocket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio que gestiona las capacidades disponibles de la planta por fecha.
 *
 * DECISIÓN DE DISEÑO:
 * - Usa HashMap para almacenamiento en memoria (prototipo)
 * - Formato de fecha: "dd-MM-yyyy" (igual que en tu proyecto Ecoembes)
 * - Inicializa con datos para los próximos 10 días
 *
 * RESPONSABILIDADES:
 * - Almacenar capacidades por fecha
 * - Consultar capacidad disponible
 * - Reducir capacidad cuando se asignan contenedores
 */
public class CapacidadService {

    // Almacena: "dd-MM-yyyy" -> capacidad en toneladas
    private Map<String, Double> capacidadesPorFecha;
    private SimpleDateFormat dateFormat;

    public CapacidadService() {
        this.capacidadesPorFecha = new HashMap<>();
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        // Inicializamos con datos de prueba
        inicializarCapacidades();
    }

    /**
     * Inicializa capacidades para los próximos 10 días.
     * Según el proyecto: "información disponible con 10 días de antelación"
     */
    private void inicializarCapacidades() {
        long unDiaEnMillis = 24 * 60 * 60 * 1000L;
        Date hoy = new Date();

        System.out.println("\n=== INICIALIZANDO CAPACIDADES ===");

        for (int i = 0; i < 10; i++) {
            Date fecha = new Date(hoy.getTime() + (i * unDiaEnMillis));
            String fechaStr = dateFormat.format(fecha);

            // Capacidad aleatoria entre 80 y 150 toneladas
            double capacidad = 80 + (Math.random() * 70);
            capacidad = Math.round(capacidad * 100.0) / 100.0; // 2 decimales

            capacidadesPorFecha.put(fechaStr, capacidad);

            System.out.println("  " + fechaStr + " -> " + capacidad + " toneladas");
        }

        System.out.println("==================================\n");
    }

    /**
     * Consulta la capacidad disponible para una fecha.
     *
     * @param fechaStr Fecha en formato "dd-MM-yyyy"
     * @return Capacidad en toneladas, o -1 si no hay capacidad para esa fecha
     */
    public double consultarCapacidad(String fechaStr) {
        Double capacidad = capacidadesPorFecha.get(fechaStr);

        if (capacidad == null) {
            System.out.println("[CAPACIDAD] No disponible para: " + fechaStr);
            return -1;
        }

        System.out.println("[CAPACIDAD] Fecha: " + fechaStr + " -> " + capacidad + " ton");
        return capacidad;
    }

    /**
     * Reduce la capacidad disponible cuando se asignan contenedores.
     *
     * @param fechaStr Fecha en formato "dd-MM-yyyy"
     * @param cantidadARestar Toneladas a restar
     * @return true si se pudo restar, false si no hay suficiente capacidad
     */
    public boolean reducirCapacidad(String fechaStr, double cantidadARestar) {
        Double capacidadActual = capacidadesPorFecha.get(fechaStr);

        if (capacidadActual == null) {
            System.out.println("[ERROR] No hay capacidad registrada para: " + fechaStr);
            return false;
        }

        if (capacidadActual < cantidadARestar) {
            System.out.println("[ERROR] Capacidad insuficiente. Disponible: " +
                    capacidadActual + ", Solicitado: " + cantidadARestar);
            return false;
        }

        double nuevaCapacidad = capacidadActual - cantidadARestar;
        capacidadesPorFecha.put(fechaStr, nuevaCapacidad);

        System.out.println("[CAPACIDAD REDUCIDA] " + fechaStr + ": " +
                capacidadActual + " -> " + nuevaCapacidad + " toneladas");
        return true;
    }

    /**
     * Método de utilidad para ver todas las capacidades (debugging)
     */
    public void mostrarTodasCapacidades() {
        System.out.println("\n=== CAPACIDADES ACTUALES ===");
        capacidadesPorFecha.forEach((fecha, capacidad) -> {
            System.out.println("  " + fecha + " -> " + capacidad + " ton");
        });
        System.out.println("============================\n");
    }
}