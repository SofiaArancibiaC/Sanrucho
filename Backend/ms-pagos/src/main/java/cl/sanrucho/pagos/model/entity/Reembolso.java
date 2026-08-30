package cl.sanrucho.pagos.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reembolsos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Reembolso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaccion_id", nullable = false)
    private Transaccion transaccion;

    @Column(name = "monto_reembolso", nullable = false)
    private Long montoReembolso;

    @Column(name = "motivo", length = 200)
    private String motivo;

    @Column(name = "procesado_en", nullable = false, updatable = false)
    private LocalDateTime procesadoEn;

    @PrePersist
    protected void onCreate() {
        if (procesadoEn == null) {
            procesadoEn = LocalDateTime.now();
        }
    }
}