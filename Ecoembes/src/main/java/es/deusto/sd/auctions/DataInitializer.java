package es.deusto.sd.auctions;

import es.deusto.sd.auctions.dao.ContenedorRepository;
import es.deusto.sd.auctions.dao.EstadoRepository;
import es.deusto.sd.auctions.entity.Contenedor;
import es.deusto.sd.auctions.entity.Estado;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ContenedorRepository contenedorRepository;
    private final EstadoRepository estadoRepository;

    public DataInitializer(ContenedorRepository contenedorRepository,
                           EstadoRepository estadoRepository) {
        this.contenedorRepository = contenedorRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (contenedorRepository.count() > 0) {
            System.out.println("⚠️ Ya existen contenedores en la BD");
            return;
        }

        System.out.println("🚀 Inicializando datos de prueba...");

        // Contenedor 1: Llenado progresivo normal (Verde → Verde)
        crearContenedorConHistorico(0.30, 0.55);

        // Contenedor 2: Transición a alerta (Verde → Naranja)
        crearContenedorConHistorico(0.75, 0.85);

        // Contenedor 3: Situación crítica (Naranja → Rojo)
        crearContenedorConHistorico(0.88, 1.0);

        System.out.println("✅ Datos de prueba cargados correctamente");
        System.out.println("   - 3 contenedores creados");
        System.out.println("   - 6 estados históricos registrados");
    }

    private void crearContenedorConHistorico(double cantidadInicial,
                                             double cantidadActual) {
        // Paso 1: Crear el contenedor vacío primero
        Contenedor contenedor = new Contenedor();
        contenedor = contenedorRepository.save(contenedor); // Guardar para obtener ID

        // Paso 2: Crear estado histórico (hace 2 días)
        Date fechaPasada = obtenerFechaRelativa(-2);
        Estado estadoPasado = new Estado(fechaPasada, cantidadInicial, contenedor);
        estadoRepository.save(estadoPasado);

        // Paso 3: Crear estado actual (ayer)
        Date fechaActual = obtenerFechaRelativa(-1);
        Estado estadoActual = new Estado(fechaActual, cantidadActual, contenedor);
        estadoRepository.save(estadoActual);

        // Paso 4: Actualizar el contenedor con el estado actual
        contenedor.setEstado(estadoActual);
        contenedorRepository.save(contenedor);

        // Log informativo
        System.out.println("📦 Contenedor #" + contenedor.getId() +
                " | Histórico: " + estadoPasado.getLlenado() +
                " → Actual: " + estadoActual.getLlenado());
    }

    private Date obtenerFechaRelativa(int diasOffset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, diasOffset);
        return cal.getTime();
    }
}
