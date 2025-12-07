package es.deusto.sd.auctions.Gateway;

import es.deusto.sd.auctions.Gateway.PlantaGateway;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * PROTOCOLO:
 * - CAPACIDAD|dd-MM-yyyy -> OK|capacidad o ERROR|mensaje
 * - NOTIFICAR|numContenedores|numEnvases -> OK|mensaje o ERROR|mensaje
 */
public class ConSocketGateway implements PlantaGateway {

    private final String host;
    private final int puerto;
    private final SimpleDateFormat dateFormat;
    private  final Tipo tipo;
    private final String nombre;

    public ConSocketGateway(String url, Tipo tipo, String nombre) {
        this.tipo = tipo;
        this.nombre = nombre;
        // Parsear "localhost:8090" en host y puerto
        String[] partes = url.split(":");
        if (partes.length != 2) {
            throw new IllegalArgumentException("URL debe tener formato 'host:puerto'. Recibido: " + url);
        }

        this.host = partes[0];

        try {
            this.puerto = Integer.parseInt(partes[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Puerto inválido: " + partes[1]);
        }

        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        System.out.println("[SOCKET GATEWAY] Configurado para " + host + ":" + puerto);
    }

    private String enviarComando(String comando) {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            // 1. Conectar al servidor
            System.out.println("[SOCKET] Conectando a " + host + ":" + puerto);
            socket = new Socket(host, puerto);
            socket.setSoTimeout(5000); // Timeout de 5 segundos

            // 2. Configurar streams
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 3. Enviar comando
            System.out.println("[SOCKET] → Enviando: " + comando);
            out.println(comando);

            // 4. Leer respuesta
            String respuesta = in.readLine();

            System.out.println("[SOCKET] ← Recibido: " + respuesta);

            return respuesta;

        } catch (SocketTimeoutException e) {
            System.out.println("[SOCKET] Socket timeout");
        } catch (IOException e) {
            System.out.println("[SOCKET] Socket timeout");
        } finally {
            // 5. IMPORTANTE: Cerrar recursos
            cerrarRecursos(socket, out, in);
        }
        return comando;
    }

    double parsearRespuestaCapacidad(String respuesta) {
        String[] partes = respuesta.split("\\|");
        String estado = partes[0].trim();
        String valor = partes[1].trim();

        if (estado.equals("OK")) {
            double capacidad = Double.parseDouble(valor);
            System.out.println("[SOCKET] Capacidad obtenida: " + capacidad + " toneladas");
            return capacidad;
        }
        return 0;
    }

        private void cerrarRecursos (Socket socket, PrintWriter out, BufferedReader in){
            // Cerrar PrintWriter
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    System.err.println("[SOCKET] Error al cerrar PrintWriter: " + e.getMessage());
                }
            }

            // Cerrar BufferedReader
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    System.err.println("[SOCKET] Error al cerrar BufferedReader: " + e.getMessage());
                }
            }

            // Cerrar Socket
            if (socket != null) {
                try {
                    if (!socket.isClosed()) {
                        socket.close();
                        System.out.println("[SOCKET] Conexión cerrada");
                    }
                } catch (Exception e) {
                    System.err.println("[SOCKET] Error al cerrar Socket: " + e.getMessage());
                }
            }
        }

        @Override
        public double consultarCapacidadDisponible (Date fecha){
            // Validación de entrada
            if (fecha == null) {
                throw new IllegalArgumentException("La fecha no puede ser null");
            }

            // Convertir Date a String en formato "dd-MM-yyyy"
            String fechaStr = dateFormat.format(fecha);

            // Enviar comando: CAPACIDAD|05-12-2024
            String comando = "CAPACIDAD|" + fechaStr;
            String respuesta = enviarComando(comando);

            // Parsear respuesta: OK|123.45 o ERROR|mensaje
            return parsearRespuestaCapacidad(respuesta);
        }

    @Override
    public Tipo getTipo() {
        return this.tipo;
    }

    public String getNombre() {
        return nombre;
    }
}