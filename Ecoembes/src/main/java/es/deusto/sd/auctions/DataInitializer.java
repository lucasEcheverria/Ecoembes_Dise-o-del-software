package es.deusto.sd.auctions;

import es.deusto.sd.auctions.entity.Personal;
import es.deusto.sd.auctions.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Inicialización de datos de prueba para el sistema Ecoembes.
 * Se ejecuta automáticamente al arrancar la aplicación.
 */
@Configuration
public class DataInitializer {
    
    /**
     * Bean que se ejecuta al inicio para cargar usuarios de prueba.
     * Alternativa a data.sql, más flexible y con control programático.
     */
    @Bean
    CommandLineRunner initDatabase(AuthService authService) {
        return args -> {
            // Verificar si ya existen usuarios para no duplicar en cada reinicio
            if (authService.getUserByEmail("admin@ecoembes.com") == null) {
                
                System.out.println("🔄 Inicializando datos de prueba...");
                
                // Usuario administrador
                Personal admin = new Personal("admin@ecoembes.com", "admin123");
                authService.addUser(admin);
                
                // Usuarios operadores
                Personal operador1 = new Personal("operador1@ecoembes.com", "pass123");
                authService.addUser(operador1);
                
                Personal operador2 = new Personal("operador2@ecoembes.com", "pass123");
                authService.addUser(operador2);
                
                // Usuario supervisor
                Personal supervisor = new Personal("supervisor@ecoembes.com", "super123");
                authService.addUser(supervisor);
                
                // Usuario para testing
                Personal test = new Personal("test@ecoembes.com", "test123");
                authService.addUser(test);
                
                System.out.println("✅ Datos de prueba cargados correctamente");
                System.out.println("   - admin@ecoembes.com / admin123");
                System.out.println("   - operador1@ecoembes.com / pass123");
                System.out.println("   - operador2@ecoembes.com / pass123");
                System.out.println("   - supervisor@ecoembes.com / super123");
                System.out.println("   - test@ecoembes.com / test123");
            } else {
                System.out.println("ℹ️  Datos ya existentes en la base de datos");
            }
        };
    }
}