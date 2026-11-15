package cl.sanrucho.catalogo.model.entity.productos;

import java.math.BigDecimal;

import cl.sanrucho.catalogo.model.enums.EstadoProducto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Producto de la tienda.
 *
 * Vive en la base de datos "productos". La columna id_categoria es una
 * REFERENCIA LOGICA a la base de datos "categorias": no existe una FK fisica
 * porque PostgreSQL no permite FKs entre bases de datos distintas.
 * La existencia de la categoria se valida en la capa de servicio.
 */
@Entity
@Table(
        name = "productos",
        uniqueConstraints = @UniqueConstraint(name = "uk_productos_codigo", columnNames = "codigo")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer id;

    @Column(name = "codigo", nullable = false, length = 50, unique = true)
    private String codigo;

    /** Referencia logica a categorias.id_categoria (otra base de datos). */
    @Column(name = "id_categoria", nullable = false)
    private Integer idCategoria;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 0)
    private BigDecimal precio;

    @Column(name = "oferta")
    private Boolean oferta;

    @Column(name = "precio_oferta", precision = 10, scale = 0)
    private BigDecimal precioOferta;

    @Column(name = "descuento")
    private Integer descuento;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "stock_critico", nullable = false)
    private Integer stockCritico;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 30)
    private EstadoProducto estado;

    /** Precio que realmente paga el cliente (oferta si esta vigente). */
    public BigDecimal getPrecioFinal() {
        return Boolean.TRUE.equals(oferta) && precioOferta != null ? precioOferta : precio;
    }

    /** Indica si el stock esta en el umbral critico o por debajo. */
    public boolean isStockBajo() {
        return stock != null && stockCritico != null && stock <= stockCritico;
    }
}
