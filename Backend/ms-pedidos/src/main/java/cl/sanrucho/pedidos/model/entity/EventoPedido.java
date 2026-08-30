package cl.sanrucho.pedidos.model.entity;

import jakarta.persistence.*;
import lombok.*;
import cl.sanrucho.pedidos.model.enums.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_pedido")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class EventoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "event_id", unique = true, nullable = false, length = 100)
    private String eventId;

    // pedido_id como Integer plano: este evento puede publicarse/leerse
    // independientemente del ciclo de vida de Pedido (Kafka)
    @Column(name = "pedido_id", nullable = false)
    private Integer pedidoId;

    @Column(name = "numero_pedido", nullable = false, length = 50)
    private String numeroPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 50)
    private TipoEvento tipoEvento;

    @Column(name = "publicado")
    @Builder.Default
    private Boolean publicado = false;

    @Column(name = "fecha_evento")
    private LocalDateTime fechaEvento;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    @PrePersist
    protected void onCreate() {
        if (this.fechaEvento == null) {
            this.fechaEvento = LocalDateTime.now();
        }
    }
}