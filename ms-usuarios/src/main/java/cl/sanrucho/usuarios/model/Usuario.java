package cl.sanrucho.usuarios.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "nombre_completo", insertable = false, updatable = false, length = 201)
    private String nombreCompleto;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    @Builder.Default
    private EstadoUsuario estado = EstadoUsuario.activo;

    @Column(name = "personaje_favorito", length = 50)
    private String personajeFavorito;
//builder.default:de lombok, "Cuando utilice el Builder y 
// no especifique este campo, utiliza el valor que puse por defecto."
    @Column(name = "notificaciones_email")
    @Builder.Default
    private Boolean notificacionesEmail = true;

    @Column(name = "notificaciones_push")
    @Builder.Default
    private Boolean notificacionesPush = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "version")
    @Builder.Default
    private Integer version = 1;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Direccion> direcciones = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EventoUsuario> eventos = new ArrayList<>();

    public enum EstadoUsuario {
        activo, 
        inactivo, 
        suspendido
    }
}
