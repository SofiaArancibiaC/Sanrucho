package cl.sanrucho.usuarios.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoUsuario {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id", nullable = false)
private Integer id;

@Column(name = "event_id", nullable = false, unique = true, length = 100)
private String eventId;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;

@Enumerated(EnumType.STRING)
@Column(name = "tipo_evento", nullable = false, length = 50)
private TipoEvento tipoEvento;

@Column(name = "payload", nullable = false, columnDefinition = "jsonb")
private String payload;

@Column(name = "publicado")
@Builder.Default
private Boolean publicado = false;

@Column(name = "fecha_evento")
private LocalDateTime fechaEvento;

@Column(name = "fecha_publicacion")
private LocalDateTime fechaPublicacion;

public enum TipoEvento {
    usuario_registrado,
    usuario_actualizado,
    direccion_agregada,
    preferencias_actualizadas,
    usuario_suspendido
}

}
