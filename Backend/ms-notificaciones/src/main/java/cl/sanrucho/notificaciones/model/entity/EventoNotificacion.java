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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Entity
@Table(name = "eventos_notificacion")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class EventoNotificacion {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
 
    @Column(name = "event_id", nullable = false, unique = true, length = 100)
    private String eventId;
 
    @Column(name = "notificacion_id", nullable = false)
    private Integer notificacionId;
 
    @Column(name = "tipo_evento", nullable = false, length = 50)
    private String tipoEvento;
 
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payload;
 
    @Builder.Default
    @Column
    private Boolean publicado = false;
 
    @Column(name = "fecha_evento")
    private LocalDateTime fechaEvento;
 
    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;
 
    @PrePersist
    protected void onCreate() {
        if (fechaEvento == null) {
            fechaEvento = LocalDateTime.now();
        }
    }
}