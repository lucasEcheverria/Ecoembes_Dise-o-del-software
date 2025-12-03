package es.deusto.sd.auctions.entity;

import java.util.Objects;

public class Personal {
	private String email;
	private String contrasena;
	
	// Token de sesión activa (timestamp del login)
	private String activeToken;
	
	// Constructor sin parámetros
	public Personal() { }
	
	// Constructor con parámetros (sin token - se genera al hacer login)
	public Personal(String email, String contrasena) {
		this.email = email;
		this.contrasena = contrasena;
		this.activeToken = null; // Sin sesión activa inicialmente
	}
	
	/**
	 * Verifica si la contraseña proporcionada coincide con la del usuario.
	 * @param password Contraseña a verificar
	 * @return true si la contraseña es correcta
	 */
	public boolean checkPassword(String password) {
		return this.contrasena != null && this.contrasena.equals(password);
	}
	
	/**
	 * Verifica si un token proporcionado es válido para este usuario.
	 * @param token Token a validar
	 * @return true si el token coincide con el token activo del usuario
	 */
	public boolean isValidToken(String token) {
		return this.activeToken != null && this.activeToken.equals(token);
	}
	
	/**
	 * Invalida el token actual (logout).
	 */
	public void invalidateToken() {
		this.activeToken = null;
	}
	
	/**
	 * Verifica si el usuario tiene una sesión activa.
	 * @return true si existe un token activo
	 */
	public boolean hasActiveSession() {
		return this.activeToken != null;
	}
	
	// Getters y Setters
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getContrasena() {
		return contrasena;
	}
	
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}
	
	public String getActiveToken() {
		return activeToken;
	}
	
	public void setActiveToken(String activeToken) {
		this.activeToken = activeToken;
	}
	
	// hashCode y equals (basados en email como identificador único)
	@Override
	public int hashCode() {
		return Objects.hash(email);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Personal other = (Personal) obj;
		return Objects.equals(email, other.email);
	}
	
	@Override
	public String toString() {
		return "Personal{" +
				"email='" + email + '\'' +
				", hasActiveSession=" + hasActiveSession() +
				'}';
	}
}