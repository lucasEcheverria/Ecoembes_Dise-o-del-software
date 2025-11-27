package es.deusto.sd.auctions.service;

import es.deusto.sd.auctions.entity.Personal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación que mantiene el estado del servidor.
 * Gestiona tokens activos y usuarios autenticados en memoria.
 */
@Service
public class AuthService {
	
	// Repositorio de usuarios (simulación de base de datos)
	private static Map<String, Personal> userRepository = new HashMap<>();
	
	// Almacenamiento para mantener la sesión de los usuarios que están logueados
	private static Map<String, Personal> tokenStore = new HashMap<>();
	
	/**
	 * Método de login que verifica si el usuario existe en la base de datos y valida la contraseña.
	 * @param email Email del usuario
	 * @param password Contraseña del usuario
	 * @return Optional con el token si el login es exitoso, Optional.empty() en caso contrario
	 */
	public Optional<String> login(String email, String password) {
		Personal user = userRepository.get(email);
		
		if (user != null && user.checkPassword(password)) {
			String token = generateToken();      // Generar un token único para la sesión
			user.setActiveToken(token);          // Asignar token al usuario
			tokenStore.put(token, user);         // Almacenar el token y asociarlo con el usuario
			
			return Optional.of(token);
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * Método de logout para eliminar el token del almacenamiento de sesiones.
	 * @param token Token de la sesión a cerrar
	 * @return Optional con true si el logout fue exitoso, Optional.empty() si el token no existe
	 */
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
	
	/**
	 * Método para agregar un nuevo usuario al repositorio.
	 * @param user Usuario a agregar
	 */
	public void addUser(Personal user) {
		if (user != null) {
			userRepository.putIfAbsent(user.getEmail(), user);
		}
	}
	
	/**
	 * Método para obtener el usuario basado en el token.
	 * @param token Token de sesión
	 * @return Usuario asociado al token o null si no existe
	 */
	public Personal getUserByToken(String token) {
		return tokenStore.get(token);
	}
	
	/**
	 * Método para obtener el usuario basado en el email.
	 * @param email Email del usuario
	 * @return Usuario con ese email o null si no existe
	 */
	public Personal getUserByEmail(String email) {
		return userRepository.get(email);
	}
	
	/**
	 * Verifica si un token existe y es válido.
	 * @param token Token a validar
	 * @return true si el token existe en las sesiones activas, false en caso contrario
	 */
	public boolean valido(String token) {
		return tokenStore.containsKey(token);
	}
	
	/**
	 * Método sincronizado para garantizar la generación única de tokens.
	 * Genera un token basado en el timestamp actual en formato hexadecimal.
	 * @return Token único generado (timestamp en hexadecimal)
	 */
	private static synchronized String generateToken() {
		return Long.toHexString(System.currentTimeMillis());
	}
	
	/**
	 * Obtiene el número de sesiones activas.
	 * @return Cantidad de tokens activos
	 */
	public int getActiveSessionsCount() {
		return tokenStore.size();
	}
	
	/**
	 * Limpia todas las sesiones activas.
	 */
	public void clearAllSessions() {
		for (Personal user : tokenStore.values()) {
			user.invalidateToken();
		}
		tokenStore.clear();
	}
}