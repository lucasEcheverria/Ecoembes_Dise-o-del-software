package es.deusto.sd.auctions.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "personal")
public class Personal {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;
	
	@Column(name = "contrasena", nullable = false, length = 255)
	private String contrasena;

	// Token de sesión activa (timestamp del login)
	// Este campo NO se persiste en BD, solo se mantiene en memoria en el servidor
	@Transient
	private String activeToken;

	// Constructor sin parámetros (requerido por JPA)
	public Personal() { }

	// Constructor con parámetros (sin token - se genera al hacer login)
	public Personal(String email, String contrasena) {
		this.email = email;
		this.contrasena = contrasena;
		this.activeToken = null; // Sin sesión activa inicialmente
	}

	public boolean checkPassword(String password) {
		return this.contrasena != null && this.contrasena.equals(password);
	}

	public boolean isValidToken(String token) {
		return this.activeToken != null && this.activeToken.equals(token);
	}

	public void invalidateToken() {
		this.activeToken = null;
	}

	public boolean hasActiveSession() {
		return this.activeToken != null;
	}

	// Getters y Setters
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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
				"id=" + id +
				", email='" + email + '\'' +
				", hasActiveSession=" + hasActiveSession() +
				'}';
	}
}