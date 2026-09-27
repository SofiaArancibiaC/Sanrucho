package cl.sanrucho.notificaciones.model.entity;

import java.time.LocalDateTime;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Entity
@Table(name = "configuracion_notificaciones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ConfiguracionNotificaciones {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
 
    @Column(name = "usuario_id", nullable = false, unique = true)
    private Integer usuarioId;
 
    @Builder.Default
    @Column(name = "email_pedidos")
    private Boolean emailPedidos = true;
 
    @Builder.Default
    @Column(name = "email_promociones")
    private Boolean emailPromociones = true;
 
    @Builder.Default
    @Column(name = "email_newsletter")
    private Boolean emailNewsletter = false;
 
    @Builder.Default
    @Column(name = "push_pedidos")
    private Boolean pushPedidos = true;
 
    @Builder.Default
    @Column(name = "push_promociones")
    private Boolean pushPromociones = false;
 
    @Builder.Default
    @Column(name = "sms_pedidos")
    private Boolean smsPedidos = false;
 
    @Column(name = "created_at")
    private LocalDateTime createdAt;
 
    @Version
    private Integer version;
 
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}