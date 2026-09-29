package cl.sanrucho.pagos.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "metodos_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre", nullable = false, length = 60)
    private NombreMetodoPago nombre;

    public enum NombreMetodoPago {
        TRANSFERENCIA, WEBPAY, MERCADOPAGO, PAYPAL
    }
}