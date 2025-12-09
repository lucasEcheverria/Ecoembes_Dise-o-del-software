package es.deusto.sd.auctions.service;

import es.deusto.sd.auctions.dao.PersonalRepository;
import es.deusto.sd.auctions.entity.Personal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
	
	@Autowired
	private PersonalRepository personalRepository;
	
	// Almacenamiento para mantener la sesión de los usuarios que están logueados
	// Map<token, Personal>
	private static Map<String, Personal> tokenStore = new HashMap<>();
	
	
	//Método de login que verifica si el usuario existe en la base de datos y valida la contraseña.
	public Optional<String> login(String email, String password) {
		// Buscar usuario en la base de datos H2 usando JPA
		Optional<Personal> userOpt = personalRepository.findByEmail(email);
		
		if (userOpt.isEmpty()) {
			return Optional.empty();
		}
		
		Personal user = userOpt.get();
		
		if (user.checkPassword(password)) {
			String token = generateToken();      // Generar un token único para la sesión
			user.setActiveToken(token);          // Asignar token al usuario
			tokenStore.put(token, user);         // Almacenar el token y asociarlo con el usuario
			
			return Optional.of(token);
		} else {
			return Optional.empty();
		}
	}
	
	
	// Método de logout para eliminar el token del almacenamiento de sesiones.

	public Optional<Boolean> logout(String token) {
		if (tokenStore.containsKey(token)) {
			Personal user = tokenStore.get(token);
			user.invalidateToken();          // Invalidar token en el usuario
			tokenStore.remove(token);        // Eliminar de sesiones activas
			
			return Optional.of(true);
		} else {
			return Optional.empty();
		}
	}
	
	
	// Método para agregar un nuevo usuario al repositorio. Ahora persiste en la base de datos H2.
	public void addUser(Personal user) {
		if (user != null && !personalRepository.existsByEmail(user.getEmail())) {
			personalRepository.save(user);
		}
	}
	
	// Método para obtener el usuario basado en el token.

	public Personal getUserByToken(String token) {
		return tokenStore.get(token);
	}
	
	//Método para obtener el usuario basado en el email. Ahora consulta la base de datos H2.
	public Personal getUserByEmail(String email) {
		return personalRepository.findByEmail(email).orElse(null);
	}
	
	
	// Verifica si un token existe y es válido.
	public boolean valido(String token) {
		return tokenStore.containsKey(token);
	}
	
	/**
	 * Método sincronizado para garantizar la generación única de tokens.
	 * Genera un token basado en el timestamp actual en formato hexadecimal.
	**/
	private static synchronized String generateToken() {
		return Long.toHexString(System.currentTimeMillis());
	}
	
	
	// Obtiene el número de sesiones activas.	
	public int getActiveSessionsCount() {
		return tokenStore.size();
	}
	
	// Limpia todas las sesiones activas.
	public void clearAllSessions() {
		for (Personal user : tokenStore.values()) {
			user.invalidateToken();
		}
		tokenStore.clear();
	}
}