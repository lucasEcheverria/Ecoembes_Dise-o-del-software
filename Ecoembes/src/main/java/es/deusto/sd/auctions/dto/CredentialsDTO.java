package es.deusto.sd.auctions.dto;

/**
 * DTO (Data Transfer Object) para las credenciales de login.
 * Contiene el email y la contraseña del usuario.
 */
public class CredentialsDTO {
	private String email;
	private String password;
	
	// Constructor sin parámetros (requerido por Spring para deserialización JSON)
	public CredentialsDTO() {}
	
	// Constructor con parámetros
	public CredentialsDTO(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
	// Getters y Setters
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return "CredentialsDTO{" +
				"email='" + email + '\'' +
				'}'; // No incluir password por seguridad
	}
}