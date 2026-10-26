package cl.sanrucho.pedidos.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "items_pedido")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Misma BD/servicio que Pedido -> relación real
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @ToString.Exclude
    private Pedido pedido;

    // Referencia cruzada a ms-catalogo: Integer plano (vía OpenFeign si se necesita)
    @Column(name = "producto_id", nullable = false)
    private Integer productoId;

    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    @Column(name = "nombre_producto", nullable = false, length = 200)
    private String nombreProducto;

    @Column(name = "personaje", length = 50)
    private String personaje;

    @Column(name = "precio_unitario", nullable = false)
    private Integer precioUnitario;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "subtotal", nullable = false)
    private Integer subtotal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "version")
    @Builder.Default
    private Integer version = 1;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}