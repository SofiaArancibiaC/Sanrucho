package cl.sanrucho.carrito.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
@Entity
@Table(name = "carritos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrito {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
 
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;
 
    @Column(name = "sesion_id", length = 100)
    private String sesionId;
 
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
 
    @Column(name = "fecha_ultima_modificacion")
    private LocalDateTime fechaUltimaModificacion;
 
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    @Builder.Default
    private EstadoCarrito estado = EstadoCarrito.ACTIVO;
 
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
 
    @Version
    @Column(name = "version")
    @Builder.Default
    private Integer version = 1;
 
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ItemCarrito> items = new ArrayList<>();
 
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.fechaCreacion = now;
        this.fechaUltimaModificacion = now;
        this.createdAt = now;
        if (this.estado == null) {
            this.estado = EstadoCarrito.ACTIVO;
        }
    }
 
    @PreUpdate
    protected void onUpdate() {
        this.fechaUltimaModificacion = LocalDateTime.now();
    }
 
    // Métodos de conveniencia
    public void addItem(ItemCarrito item) {
        items.add(item);
        item.setCarrito(this);
    }
 
    public void removeItem(ItemCarrito item) {
        items.remove(item);
        item.setCarrito(null);
    }
 
    public Integer calcularTotal() {
        return items.stream()
                .mapToInt(ItemCarrito::getSubtotal)
                .sum();
    }
 
    public int contarItems() {
        return items.stream()
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
    }
 
    public enum EstadoCarrito {
        ACTIVO,
        ABANDONADO,
        CONVERTIDO,
        EXPIRADO
    }
}