package com.contsocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor principal de ContSocket Ltd.
 *
 * ARQUITECTURA:
 * - Servidor multi-threaded: cada cliente en su propio Thread
 * - Usa ServerSocket para escuchar conexiones
 * - Delega el procesamiento a ClientHandler
 *
 * PATRÓN DE DISEÑO:
 * - Service Gateway (desde la perspectiva del cliente)
 * - Thread-per-connection (patrón de concurrencia)
 *
 * RESPONSABILIDADES:
 * 1. Inicializar el CapacidadService
 * 2. Escuchar en un puerto TCP
 * 3. Aceptar clientes y crear handlers
 * 4. Mantener el servidor ejecutándose
 */
public class ContSocketServer {

    // Puerto en el que escucha el servidor
    // Diferente del puerto de tu servidor Ecoembes (8080)
    private static final int PUERTO = 8090;

    private ServerSocket serverSocket;
    private CapacidadService capacidadService;
    private boolean ejecutando;

    public ContSocketServer() {
        this.capacidadService = new CapacidadService();
        this.ejecutando = true;
    }

    /**
     * Inicia el servidor y comienza a aceptar clientes.
     *
     * Este método se queda en un bucle infinito aceptando conexiones.
     * Por cada cliente que se conecta, crea un nuevo Thread.
     */
    public void iniciar() {
        try {
            // Crear el ServerSocket que escucha en el puerto
            serverSocket = new ServerSocket(PUERTO);

            imprimirBanner();

            // Bucle principal: acepta clientes indefinidamente
            while (ejecutando) {
                try {
                    // accept() es BLOQUEANTE: se queda esperando hasta que un cliente se conecta
                    // Cuando un cliente se conecta, devuelve un Socket para comunicarse con él
                    Socket clientSocket = serverSocket.accept();

                    // Crear un handler para este cliente
                    ClientHandler handler = new ClientHandler(clientSocket, capacidadService);

                    // Ejecutar el handler en un nuevo Thread
                    // Esto permite que el servidor siga aceptando más clientes
                    Thread clientThread = new Thread(handler);
                    clientThread.start();

                    // PREGUNTA PARA REFLEXIONAR:
                    // ¿Qué pasaría si no usáramos Threads?
                    // Respuesta: El servidor solo podría atender a un cliente a la vez.
                    // Mientras atiende a uno, los demás quedarían esperando.

                } catch (IOException e) {
                    if (ejecutando) {
                        System.err.println("[ERROR] Error al aceptar cliente: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("[ERROR CRÍTICO] No se pudo iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            detener();
        }
    }

    /**
     * Detiene el servidor de forma ordenada.
     */
    public void detener() {
        ejecutando = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("\n[SERVIDOR] Servidor detenido correctamente");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error al detener servidor: " + e.getMessage());
        }
    }

    /**
     * Imprime el banner de bienvenida con información útil.
     */
    private void imprimirBanner() {
        System.out.println("[SERVIDOR] Escuchando en puerto: " + PUERTO);
        System.out.println("[SERVIDOR] Esperando conexiones...");
        System.out.println("\n[INFO] Puedes probar el servidor con:");
        System.out.println("       telnet localhost " + PUERTO);
        System.out.println("       Luego escribe: CAPACIDAD|05-12-2024");
        System.out.println("\n[INFO] Para detener el servidor: Ctrl+C\n");
    }

    /**
     * Punto de entrada de la aplicación.
     */
    public static void main(String[] args) {
        ContSocketServer servidor = new ContSocketServer();

        // Agregar un shutdown hook para cerrar limpiamente
        // Esto se ejecuta cuando haces Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SERVIDOR] Recibida señal de cierre...");
            servidor.detener();
        }));

        // Iniciar el servidor (esto bloqueará aquí)
        servidor.iniciar();
    }
}