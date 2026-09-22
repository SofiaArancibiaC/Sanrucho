package cl.sanrucho.inventario.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventario",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_inventario_producto_id", columnNames = "producto_id"),
           @UniqueConstraint(name = "uk_inventario_sku", columnNames = "sku")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "producto_id", nullable = false, unique = true)
    private Integer productoId;

    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "nombre_producto", nullable = false, length = 200)
    private String nombreProducto;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Column(name = "stock_reservado", nullable = false)
    private Integer stockReservado;

    @Column(name = "stock_disponible", insertable = false, updatable = false)
    private Integer stockDisponible;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column(name = "stock_maximo", nullable = false)
    private Integer stockMaximo;

    @Column(name = "ubicacion_bodega", length = 50)
    private String ubicacionBodega;

    @Column(name = "alerta_stock_bajo")
    private Boolean alertaStockBajo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "version")
    private Integer version;

    @OneToMany(mappedBy = "inventario", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MovimientoInventario> movimientos = new ArrayList<>();
}
