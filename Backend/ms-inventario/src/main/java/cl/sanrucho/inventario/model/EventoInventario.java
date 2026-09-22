package cl.sanrucho.inventario.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_inventario",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_eventos_inventario_event_id", columnNames = "event_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "event_id", nullable = false, unique = true, length = 100)
    private String eventId;

    @Column(name = "producto_id", nullable = false)
    private Integer productoId;

    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 50)
    private TipoEvento tipoEvento;

    @Lob
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "publicado")
    private Boolean publicado;

    @Column(name = "fecha_evento")
    private LocalDateTime fechaEvento;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    public enum TipoEvento {
        stock_actualizado,
        stock_bajo,
        sin_stock,
        stock_reservado,
        stock_liberado
    }
}
