package cl.sanrucho.catalogo.model.entity;



import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

import cl.sanrucho.catalogo.model.enums.TipoEvento;




@Entity
@Table(name = "eventos_catalogo")




@EqualsAndHashCode(of = "id")

public class EventoCatalogo {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "event_id", length = 100, nullable = false, unique = true)
        private String eventId;

        @Column(name = "producto_id", nullable = false)
        private Long productoId;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo_evento", length = 50, nullable = false)
        private TipoEvento tipoEvento;

        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
        private String payload;

        @Column(name = "publicado", nullable = false)
        @Builder.Default
        private Boolean publicado = false;

        @CreationTimestamp
        @Column(name = "fecha_evento", updatable = false)
        private LocalDateTime fechaEvento;

        @Column(name = "fecha_publicacion")
        private LocalDateTime fechaPublicacion;

}
