package com.contsocket;

import java.io.*;
import java.net.Socket;

/**
 * Maneja la comunicación con UN cliente conectado.
 * Cada cliente tiene su propia instancia ejecutándose en su propio Thread.
 *
 * PATRÓN DE DISEÑO APLICADO:
 * - Command Pattern (implícito): Cada línea recibida es un comando que se parsea y ejecuta
 * - Strategy Pattern (implícito): Diferentes estrategias de procesamiento según el comando
 *
 * PROTOCOLO DE COMUNICACIÓN:
 * 1. CAPACIDAD|dd-MM-yyyy          -> OK|123.45 o ERROR|mensaje
 * 2. NOTIFICAR|numContenedores|numEnvases -> OK|mensaje o ERROR|mensaje
 * 3. SALIR                         -> ADIOS
 */
public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private CapacidadService capacidadService;
    private BufferedReader in;
    private PrintWriter out;
    private String clientId;

    public ClientHandler(Socket socket, CapacidadService capacidadService) {
        this.clientSocket = socket;
        this.capacidadService = capacidadService;
        // Identificador único para debugging
        this.clientId = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    @Override
    public void run() {
        try {
            configurarStreams();

            System.out.println("[CONEXIÓN] Cliente conectado: " + clientId);

            // Bucle principal: leer y procesar comandos
            procesarComandos();

        } catch (IOException e) {
            System.err.println("[ERROR] Cliente " + clientId + ": " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    /**
     * Configura los streams de entrada/salida.
     *
     * BufferedReader: Para leer líneas completas de texto
     * PrintWriter: Para escribir respuestas (con auto-flush activado)
     */
    private void configurarStreams() throws IOException {
        in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream())
        );

        out = new PrintWriter(
                clientSocket.getOutputStream(),
                true  // auto-flush: envía datos inmediatamente sin esperar
        );
    }

    /**
     * Bucle principal que lee comandos del cliente y los procesa.
     */
    private void procesarComandos() throws IOException {
        String lineaRecibida;

        // readLine() es BLOQUEANTE: espera hasta que llegue una línea completa
        while ((lineaRecibida = in.readLine()) != null) {
            System.out.println("[" + clientId + "] Recibido: " + lineaRecibida);

            // Procesar el comando y obtener respuesta
            String respuesta = procesarComando(lineaRecibida);

            // Enviar respuesta al cliente
            out.println(respuesta);
            System.out.println("[" + clientId + "] Enviado: " + respuesta);

            // Si el cliente dice SALIR, terminamos la conexión
            if (lineaRecibida.trim().equalsIgnoreCase("SALIR")) {
                break;
            }
        }
    }

    /**
     * Procesa un comando recibido y devuelve la respuesta apropiada.
     *
     * Este es el corazón del protocolo de comunicación.
     *
     * @param comando Comando recibido del cliente
     * @return Respuesta a enviar al cliente
     */
    private String procesarComando(String comando) {
        // Validación básica
        if (comando == null || comando.trim().isEmpty()) {
            return "ERROR|Comando vacío";
        }

        // Parsear el comando separando por |
        String[] partes = comando.split("\\|");
        String tipoComando = partes[0].trim().toUpperCase();

        // Delegar según el tipo de comando
        switch (tipoComando) {
            case "CAPACIDAD":
                return procesarConsultaCapacidad(partes);

            case "NOTIFICAR":
                return procesarNotificacion(partes);

            case "SALIR":
                return "ADIOS";

            default:
                return "ERROR|Comando desconocido: " + tipoComando +
                        ". Comandos válidos: CAPACIDAD, NOTIFICAR, SALIR";
        }
    }

    /**
     * Procesa el comando: CAPACIDAD|dd-MM-yyyy
     *
     * Responde: OK|123.45 si hay capacidad
     *           ERROR|mensaje si no hay o formato incorrecto
     */
    private String procesarConsultaCapacidad(String[] partes) {
        // Validar formato
        if (partes.length != 2) {
            return "ERROR|Formato incorrecto. Use: CAPACIDAD|dd-MM-yyyy";
        }

        String fecha = partes[1].trim();

        // Validación básica del formato de fecha (opcional pero recomendable)
        if (!fecha.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return "ERROR|Formato de fecha inválido. Use: dd-MM-yyyy";
        }

        // Consultar capacidad
        double capacidad = capacidadService.consultarCapacidad(fecha);

        if (capacidad < 0) {
            return "ERROR|No hay capacidad disponible para la fecha: " + fecha;
        }

        return "OK|" + capacidad;
    }

    /**
     * Procesa el comando: NOTIFICAR|numContenedores|numEnvases
     *
     * Responde: OK|mensaje si se procesó correctamente
     *           ERROR|mensaje si hay algún problema
     */
    private String procesarNotificacion(String[] partes) {
        // Validar formato
        if (partes.length != 3) {
            return "ERROR|Formato incorrecto. Use: NOTIFICAR|numContenedores|numEnvases";
        }

        try {
            int numContenedores = Integer.parseInt(partes[1].trim());
            int numEnvases = Integer.parseInt(partes[2].trim());

            // Validar que sean números positivos
            if (numContenedores <= 0 || numEnvases <= 0) {
                return "ERROR|Los números deben ser positivos";
            }

            // Log de la notificación
            System.out.println("[NOTIFICACIÓN] " + numContenedores +
                    " contenedores con " + numEnvases + " envases");

            // En una implementación real, aquí se haría algo con esta información
            // Por ejemplo: actualizar base de datos, enviar emails, etc.

            return "OK|Notificación recibida: " + numContenedores +
                    " contenedores, " + numEnvases + " envases";

        } catch (NumberFormatException e) {
            return "ERROR|Los valores de contenedores y envases deben ser números enteros";
        }
    }

    /**
     * Cierra todos los recursos de forma segura.
     * Se ejecuta siempre al terminar, gracias al bloque finally.
     */
    private void cerrarRecursos() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            System.out.println("[DESCONEXIÓN] Cliente desconectado: " + clientId);
        } catch (IOException e) {
            System.err.println("[ERROR] Al cerrar recursos: " + e.getMessage());
        }
    }
}