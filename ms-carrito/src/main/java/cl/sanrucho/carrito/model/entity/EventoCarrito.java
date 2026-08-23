package cl.sanrucho.carrito.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
 
import java.time.LocalDateTime;
import java.util.Map;
 
@Entity
@Table(name = "eventos_carrito")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoCarrito {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
 
    @Column(name = "event_id", length = 100, nullable = false, unique = true)
    private String eventId;
 
    @Column(name = "carrito_id", nullable = false)
    private Integer carritoId;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", length = 50, nullable = false)
    private TipoEvento tipoEvento;
 
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payload;
 
    @Column(name = "publicado", nullable = false)
    @Builder.Default
    private Boolean publicado = false;
 
    @Column(name = "fecha_evento", nullable = false, updatable = false)
    private LocalDateTime fechaEvento;
 
    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;
 
    @PrePersist
    protected void onCreate() {
        if (this.fechaEvento == null) {
            this.fechaEvento = LocalDateTime.now();
        }
        if (this.publicado == null) {
            this.publicado = false;
        }
    }
 
    public void marcarComoPublicado() {
        this.publicado = true;
        this.fechaPublicacion = LocalDateTime.now();
    }
 
    public enum TipoEvento {
        ITEM_AGREGADO("item_agregado"),
        ITEM_REMOVIDO("item_removido"),
        ITEM_ACTUALIZADO("item_actualizado"),
        CARRITO_ABANDONADO("carrito_abandonado"),
        CARRITO_CONVERTIDO("carrito_convertido"),
        CARRITO_VACIADO("carrito_vaciado");
 
        private final String valor;
 
        TipoEvento(String valor) {
            this.valor = valor;
        }
 
        public String getValor() {
            return valor;
        }
 
        public static TipoEvento fromString(String valor) {
            for (TipoEvento tipo : TipoEvento.values()) {
                if (tipo.valor.equalsIgnoreCase(valor)) {
                    return tipo;
                }
            }
            throw new IllegalArgumentException("Tipo de evento no válido: " + valor);
        }
    }
}