package cl.sanrucho.catalogo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
 
import java.time.LocalDateTime;
 
@Entity
@Table(name = "especificaciones_producto", indexes = {
        @Index(name = "idx_especificaciones_producto", columnList = "producto_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "producto")
@EqualsAndHashCode(of = "id")

public class EspecificacionProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
 
    @Column(name = "atributo", length = 100, nullable = false)
    private String atributo;
 
    @Column(name = "valor", length = 200, nullable = false)
    private String valor;
 
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}
