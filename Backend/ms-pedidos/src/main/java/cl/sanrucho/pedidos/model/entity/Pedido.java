package cl.sanrucho.pedidos.model.entity;

import jakarta.persistence.*;
import lombok.*;
import cl.sanrucho.pedidos.model.enums.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero_pedido", unique = true, nullable = false, length = 50)
    private String numeroPedido;

    // Referencia cruzada a ms-usuarios: se mantiene como Integer plano
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "nombre_usuario", nullable = false, length = 200)
    private String nombreUsuario;

    @Column(name = "email_usuario", nullable = false)
    private String emailUsuario;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 30)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @Column(name = "subtotal", nullable = false)
    private Integer subtotal;

    @Column(name = "descuento")
    @Builder.Default
    private Integer descuento = 0;

    @Column(name = "costo_envio", nullable = false)
    private Integer costoEnvio;

    @Column(name = "total", nullable = false)
    private Integer total;

    // Referencia cruzada a ms-direcciones (o similar): Integer plano
    @Column(name = "direccion_envio_id", nullable = false)
    private Integer direccionEnvioId;

    @Column(name = "direccion_envio_snapshot", nullable = false, columnDefinition = "TEXT")
    private String direccionEnvioSnapshot;

    @Column(name = "ciudad_envio", nullable = false, length = 100)
    private String ciudadEnvio;

    @Column(name = "region_envio", nullable = false, length = 100)
    private String regionEnvio;

    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "version")
    @Builder.Default
    private Integer version = 1;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<ItemPedido> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.fechaPedido == null) {
            this.fechaPedido = LocalDateTime.now();
        }
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(ItemPedido item) {
        items.add(item);
        item.setPedido(this);
    }
}