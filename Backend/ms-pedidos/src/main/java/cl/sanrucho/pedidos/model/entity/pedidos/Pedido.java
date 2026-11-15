package cl.sanrucho.pedidos.model.entity.pedidos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Pedido (cabecera) de la tienda.
 *
 * Vive en la base de datos "pedidos". id_usuario es una REFERENCIA LOGICA a
 * la base de datos "usuarios" y no tiene FK fisica (PostgreSQL no permite
 * FK entre bases de datos distintas). El detalle vive en la base de datos
 * "detalle_pedidos", por lo que la relacion se resuelve en la capa de servicio.
 */
@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer id;

    /** Referencia logica a usuarios.id_usuario (otra base de datos). */
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;

    @Column(name = "total", nullable = false, precision = 10, scale = 0)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 50)
    private EstadoPedido estado;
}
