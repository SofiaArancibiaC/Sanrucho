package cl.sanrucho.pagos.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cl.sanrucho.pagos.model.enums.EstadoTransaccion;
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
 * Transaccion de pago. idPedido es una referencia logica al microservicio
 * de pedidos (sin foreign key fisica).
 */
@Entity
@Table(name = "transacciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaccion")
    private Integer id;

    @Column(name = "id_pedido", nullable = false)
    private Integer idPedido;

    @Column(name = "monto", nullable = false, precision = 10, scale = 0)
    private BigDecimal monto;

    @Column(name = "pasarela", nullable = false, length = 50)
    private String pasarela;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 50)
    private EstadoTransaccion estado;

    @Column(name = "codigo_autorizacion", length = 100)
    private String codigoAutorizacion;

    @Column(name = "fecha_transaccion")
    private LocalDateTime fechaTransaccion;
}
