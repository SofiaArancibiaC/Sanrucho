package cl.sanrucho.carrito.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
import java.time.LocalDateTime;
 
@Entity
@Table(name = "items_carrito")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCarrito {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;
 
    @Column(name = "producto_id", nullable = false)
    private Integer productoId;
 
    @Column(name = "sku", length = 50, nullable = false)
    private String sku;
 
    @Column(name = "nombre_producto", length = 200, nullable = false)
    private String nombreProducto;
 
    @Column(name = "personaje", length = 50)
    private String personaje;
 
    @Column(name = "precio_unitario", nullable = false)
    private Integer precioUnitario;
 
    @Column(name = "cantidad", nullable = false)
    @Builder.Default
    private Integer cantidad = 1;
 
    @Column(name = "subtotal", nullable = false)
    private Integer subtotal;
 
    @Column(name = "fecha_agregado", nullable = false, updatable = false)
    private LocalDateTime fechaAgregado;
 
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
 
    @Version
    @Column(name = "version")
    @Builder.Default
    private Integer version = 1;
 
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.fechaAgregado = now;
        this.createdAt = now;
        calcularSubtotal();
    }
 
    @PreUpdate
    protected void onUpdate() {
        calcularSubtotal();
    }
 
    public void calcularSubtotal() {
        if (this.precioUnitario != null && this.cantidad != null) {
            this.subtotal = this.precioUnitario * this.cantidad;
        }
    }
 
    public void incrementarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.cantidad += cantidad;
        calcularSubtotal();
    }
 
    public void actualizarCantidad(int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.cantidad = nuevaCantidad;
        calcularSubtotal();
    }
}