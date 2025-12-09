package org.example.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ASIGNACION")
public class Asignacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "fecha", nullable = false)
    private Date fecha;
    
    @Column(name = "total_contenedores", nullable = false)
    private Integer totalContenedores;
    
    @Column(name = "total_envases", nullable = false)
    private Integer totalEnvases;
    
    @Column(name = "peso_estimado", nullable = false)
    private Double pesoEstimado;
    
    @Column(name = "asignado_por", nullable = false, length = 100)
    private String asignadoPor;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "recibido_en", nullable = false)
    private Date recibidoEn;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoAsignacion estado;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ASIGNACION_CONTENEDORES", 
                     joinColumns = @JoinColumn(name = "asignacion_id"))
    @Column(name = "contenedor_id")
    private List<String> contenedorIds = new ArrayList<>();
    
    @Column(name = "mensaje", length = 500)
    private String mensaje;
    
    // Enum para estados
    public enum EstadoAsignacion {
        PENDIENTE,
        PROCESANDO,
        COMPLETADO,
        RECHAZADO
    }
    
    // Constructores
    public Asignacion() {
        this.recibidoEn = new Date();
        this.estado = EstadoAsignacion.PENDIENTE;
    }
    
    public Asignacion(Date fecha, Integer totalContenedores, 
                      Integer totalEnvases, Double pesoEstimado, 
                      String asignadoPor, List<String> contenedorIds) {
        this();
        this.fecha = fecha;
        this.totalContenedores = totalContenedores;
        this.totalEnvases = totalEnvases;
        this.pesoEstimado = pesoEstimado;
        this.asignadoPor = asignadoPor;
        this.contenedorIds = contenedorIds != null ? new ArrayList<>(contenedorIds) : new ArrayList<>();
    }
    
    // Getters y Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Date getFecha() {
        return fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    public Integer getTotalContenedores() {
        return totalContenedores;
    }
    
    public void setTotalContenedores(Integer totalContenedores) {
        this.totalContenedores = totalContenedores;
    }
    
    public Integer getTotalEnvases() {
        return totalEnvases;
    }
    
    public void setTotalEnvases(Integer totalEnvases) {
        this.totalEnvases = totalEnvases;
    }
    
    public Double getPesoEstimado() {
        return pesoEstimado;
    }
    
    public void setPesoEstimado(Double pesoEstimado) {
        this.pesoEstimado = pesoEstimado;
    }
    
    public String getAsignadoPor() {
        return asignadoPor;
    }
    
    public void setAsignadoPor(String asignadoPor) {
        this.asignadoPor = asignadoPor;
    }
    
    public Date getRecibidoEn() {
        return recibidoEn;
    }
    
    public void setRecibidoEn(Date recibidoEn) {
        this.recibidoEn = recibidoEn;
    }
    
    public EstadoAsignacion getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoAsignacion estado) {
        this.estado = estado;
    }
    
    public List<String> getContenedorIds() {
        return contenedorIds;
    }
    
    public void setContenedorIds(List<String> contenedorIds) {
        this.contenedorIds = contenedorIds;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    @Override
    public String toString() {
        return "Asignacion{" +
                "id='" + id + '\'' +
                ", fecha=" + fecha +
                ", totalContenedores=" + totalContenedores +
                ", totalEnvases=" + totalEnvases +
                ", estado=" + estado +
                '}';
    }
}