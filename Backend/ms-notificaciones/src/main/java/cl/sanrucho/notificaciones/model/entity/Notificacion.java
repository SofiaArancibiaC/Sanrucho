package cl.sanrucho.notificaciones.model.entity;

import java.time.LocalDateTime;
import java.util.Map;
 
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Notificacion {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
 
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;
 
    @Column(name = "nombre_usuario", nullable = false, length = 200)
    private String nombreUsuario;
 
    @Column(name = "email_usuario", nullable = false, length = 255)
    private String emailUsuario;
 
    @Column(nullable = false, length = 50)
    private String tipo;
 
    @Column(nullable = false, length = 200)
    private String titulo;
 
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;
 
    @Column(nullable = false, length = 20)
    private String canal;
 
    @Builder.Default
    @Column(length = 20)
    private String prioridad = "normal";
 
    @Builder.Default
    @Column(length = 20)
    private String estado = "pendiente";
 
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
 
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;
 
    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;
 
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
 
    @Column(name = "created_at")
    private LocalDateTime createdAt;
 
    @Version
    private Integer version;
 
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (fechaCreacion == null) {
            fechaCreacion = now;
        }
        if (createdAt == null) {
            createdAt = now;
        }
    }
}