
/**
 * Este script con datos de prueba ha sido creado en su totalidad por claude sonnet 4.5
 * y acto seguido ha sido comprobado y testeado para asegurar que funcionasen
 *
 *
 * Inicialización de datos de prueba para el sistema Ecoembes.
 * Se ejecuta automáticamente al arrancar la aplicación.
 */
package es.deusto.sd.auctions;

import es.deusto.sd.auctions.dao.ContenedorRepository;
import es.deusto.sd.auctions.dao.EstadoRepository;
import es.deusto.sd.auctions.entity.Contenedor;
import es.deusto.sd.auctions.entity.Estado;
import es.deusto.sd.auctions.entity.Personal;
import es.deusto.sd.auctions.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Calendar;
import java.util.Date;

/**
 * Inicialización de datos de prueba para el sistema Ecoembes.
 * Se ejecuta automáticamente al arrancar la aplicación.
 */
@Configuration
public class DataInitializer {

    /**
     * Bean que se ejecuta al inicio para cargar datos de prueba.
     */
    @Bean
    CommandLineRunner initDatabase(AuthService authService,
                                   ContenedorRepository contenedorRepo,
                                   EstadoRepository estadoRepo) {
        return args -> {
            System.out.println("🔄 Inicializando datos de prueba...");

            // ============================================
            // USUARIOS
            // ============================================
            if (authService.getUserByEmail("admin@ecoembes.com") == null) {
                Personal admin = new Personal("admin@ecoembes.com", "admin123");
                authService.addUser(admin);

                Personal operador1 = new Personal("operador1@ecoembes.com", "pass123");
                authService.addUser(operador1);

                Personal operador2 = new Personal("operador2@ecoembes.com", "pass123");
                authService.addUser(operador2);

                Personal supervisor = new Personal("supervisor@ecoembes.com", "super123");
                authService.addUser(supervisor);

                Personal test = new Personal("test@ecoembes.com", "test123");
                authService.addUser(test);

                System.out.println("✅ Usuarios creados:");
                System.out.println("   - admin@ecoembes.com / admin123");
                System.out.println("   - operador1@ecoembes.com / pass123");
                System.out.println("   - operador2@ecoembes.com / pass123");
                System.out.println("   - supervisor@ecoembes.com / super123");
                System.out.println("   - test@ecoembes.com / test123");
            } else {
                System.out.println("ℹ️  Usuarios ya existentes en la BD");
            }

            // ============================================
            // CONTENEDORES Y ESTADOS
            // ============================================
            if (contenedorRepo.count() == 0) {
                Calendar cal = Calendar.getInstance();

                // Contenedor 1 - Nivel BAJO (45.5 kg - Verde)
                Contenedor c1 = new Contenedor();
                contenedorRepo.save(c1);

                cal.set(2025, Calendar.JANUARY, 15, 10, 0, 0);
                Estado e1 = new Estado(cal.getTime(), 45.5, c1);
                estadoRepo.save(e1);

                c1.setEstado(e1);
                contenedorRepo.save(c1);

                // Contenedor 2 - Nivel MEDIO-ALTO (85 kg - Naranja)
                Contenedor c2 = new Contenedor();
                contenedorRepo.save(c2);

                cal.set(2025, Calendar.JANUARY, 16, 11, 30, 0);
                Estado e2 = new Estado(cal.getTime(), 85.0, c2);
                estadoRepo.save(e2);

                c2.setEstado(e2);
                contenedorRepo.save(c2);

                // Contenedor 3 - Nivel ALTO (95 kg - Rojo)
                Contenedor c3 = new Contenedor();
                contenedorRepo.save(c3);

                cal.set(2025, Calendar.JANUARY, 17, 14, 0, 0);
                Estado e3 = new Estado(cal.getTime(), 95.0, c3);
                estadoRepo.save(e3);

                c3.setEstado(e3);
                contenedorRepo.save(c3);

                // Contenedor 4 - Nivel BAJO (30 kg - Verde)
                Contenedor c4 = new Contenedor();
                contenedorRepo.save(c4);

                cal.set(2025, Calendar.JANUARY, 18, 9, 0, 0);
                Estado e4 = new Estado(cal.getTime(), 30.0, c4);
                estadoRepo.save(e4);

                c4.setEstado(e4);
                contenedorRepo.save(c4);

                // Contenedor 5 - Nivel MEDIO-ALTO (88.5 kg - Naranja)
                Contenedor c5 = new Contenedor();
                contenedorRepo.save(c5);

                cal.set(2025, Calendar.JANUARY, 18, 16, 0, 0);
                Estado e5 = new Estado(cal.getTime(), 88.5, c5);
                estadoRepo.save(e5);

                c5.setEstado(e5);
                contenedorRepo.save(c5);

                System.out.println("✅ Contenedores creados:");
                System.out.println("   - Contenedor #" + c1.getId() + " → 45.5 kg (Verde)");
                System.out.println("   - Contenedor #" + c2.getId() + " → 85.0 kg (Naranja)");
                System.out.println("   - Contenedor #" + c3.getId() + " → 95.0 kg (Rojo)");
                System.out.println("   - Contenedor #" + c4.getId() + " → 30.0 kg (Verde)");
                System.out.println("   - Contenedor #" + c5.getId() + " → 88.5 kg (Naranja)");

                // Estados adicionales para el Contenedor 1 (para probar historial)
                cal.set(2025, Calendar.JANUARY, 10, 8, 0, 0);
                Estado e1_hist1 = new Estado(cal.getTime(), 20.0, c1);
                estadoRepo.save(e1_hist1);

                cal.set(2025, Calendar.JANUARY, 12, 10, 0, 0);
                Estado e1_hist2 = new Estado(cal.getTime(), 35.5, c1);
                estadoRepo.save(e1_hist2);

                cal.set(2025, Calendar.JANUARY, 14, 15, 0, 0);
                Estado e1_hist3 = new Estado(cal.getTime(), 42.0, c1);
                estadoRepo.save(e1_hist3);

                // El estado actual (e1 con 45.5 kg del 15 de enero) ya lo tienes

                System.out.println("✅ Historial del contenedor #1:");
                System.out.println("   - 10/01/2025 → 20.0 kg");
                System.out.println("   - 12/01/2025 → 35.5 kg");
                System.out.println("   - 14/01/2025 → 42.0 kg");
                System.out.println("   - 15/01/2025 → 45.5 kg (actual)");

            } else {
                System.out.println("ℹ️  Contenedores ya existentes en la BD");
            }
            // Añadir historial al contenedor 1 si no existe
            if (estadoRepo.count() <= 5) {
                Contenedor c1 = contenedorRepo.findById(1L).orElse(null);
                if (c1 != null) {
                    Calendar cal = Calendar.getInstance();

                    cal.set(2025, Calendar.JANUARY, 10, 8, 0, 0);
                    Estado e1_hist1 = new Estado(cal.getTime(), 20.0, c1);
                    estadoRepo.save(e1_hist1);

                    cal.set(2025, Calendar.JANUARY, 12, 10, 0, 0);
                    Estado e1_hist2 = new Estado(cal.getTime(), 35.5, c1);
                    estadoRepo.save(e1_hist2);

                    cal.set(2025, Calendar.JANUARY, 14, 15, 0, 0);
                    Estado e1_hist3 = new Estado(cal.getTime(), 42.0, c1);
                    estadoRepo.save(e1_hist3);

                    System.out.println("✅ Historial añadido al contenedor #1");
                }
            }

            System.out.println("✅ Inicialización completada");
        };
    }
}
