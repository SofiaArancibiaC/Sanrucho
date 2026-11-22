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
 * Linea de un pedido. idProducto es una referencia logica al microservicio
 * de catalogo (sin foreign key fisica).
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

    @Column(name = "id_pedido", nullable = false)
    private Integer idPedido;

    @Column(name = "id_producto", nullable = false)
    private Integer idProducto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 0)
    private BigDecimal precioUnitario;

    public BigDecimal getSubtotal() {
        if (cantidad == null || precioUnitario == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
