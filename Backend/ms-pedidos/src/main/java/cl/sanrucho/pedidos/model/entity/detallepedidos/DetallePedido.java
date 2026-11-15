package cl.sanrucho.pedidos.model.entity.detallepedidos;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Linea de detalle de un pedido.
 *
 * Vive en la base de datos "detalle_pedidos". id_pedido e id_producto son
 * referencias logicas: pedidos esta en otra base de datos y productos en el
 * microservicio de catalogo. No hay FKs fisicas.
 *
 * precio_unitario guarda el precio al momento de la compra: si el producto
 * cambia de precio o se elimina, el historial del pedido sigue siendo correcto.
 */
@Entity
@Table(name = "detalle_pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer id;

    /** Referencia logica a pedidos.id_pedido (otra base de datos). */
    @Column(name = "id_pedido", nullable = false)
    private Integer idPedido;

    /** Referencia logica a productos.id_producto (ms-catalogo). */
    @Column(name = "id_producto", nullable = false)
    private Integer idProducto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 0)
    private BigDecimal precioUnitario;

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
