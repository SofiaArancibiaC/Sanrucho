


// import com.tienda.mscatalogo.enums.EstadoProducto;
// import jakarta.persistence.*;
// import lombok.*;
// import org.hibernate.annotations.CreationTimestamp;
 
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;


// @Entity
// @Table(name = "productos", indexes = {
//         @Index(name = "idx_productos_sku", columnList = "sku"),
//         @Index(name = "idx_productos_personaje", columnList = "personaje"),
//         @Index(name = "idx_productos_categoria", columnList = "categoria"),
//         @Index(name = "idx_productos_estado", columnList = "estado")
// })

// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// @ToString(exclude = "especificaciones") //
// @EqualsAndHashCode(of = "id")
// public class Producto{

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
 
//     @Column(name = "sku", length = 50, nullable = false, unique = true)
//     private String sku;
 
//     @Column(name = "nombre", length = 200, nullable = false)
//     private String nombre;
 
//     @Column(name = "descripcion", columnDefinition = "TEXT")
//     private String descripcion;
 
//     @Column(name = "precio", nullable = false)
//     private Integer precio;
 
//     @Column(name = "personaje", length = 50, nullable = false)
//     private String personaje;
 
//     @Column(name = "categoria", length = 100, nullable = false)
//     private String categoria;
 
//     @Column(name = "imagen_url", length = 500)
//     private String imagenUrl;
 
//     @Enumerated(EnumType.STRING)
//     @Column(name = "estado", length = 20)
//     @Builder.Default
//     private EstadoProducto estado = EstadoProducto.ACTIVO;
 
//     @CreationTimestamp
//     @Column(name = "created_at", updatable = false)
//     private LocalDateTime createdAt;
 
//     @Column(name = "created_by", length = 100)
//     private String createdBy;
 
//     @Column(name = "updated_by", length = 100)
//     private String updatedBy;
 
//     @Version
//     @Column(name = "version")
//     @Builder.Default
//     private Integer version = 1;
 
//     @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//     @Builder.Default
//     private List<EspecificacionProducto> especificaciones = new ArrayList<>();
 
//     public void agregarEspecificacion(EspecificacionProducto especificacion) {
//         especificaciones.add(especificacion);
//         especificacion.setProducto(this);
//     }
 
//     public void removerEspecificacion(EspecificacionProducto especificacion) {
//         especificaciones.remove(especificacion);
//         especificacion.setProducto(null);
//     }


// }