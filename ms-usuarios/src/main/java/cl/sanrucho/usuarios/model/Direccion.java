package cl.sanrucho.usuarios.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "direcciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id", nullable = false)
private Integer id;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;

@Enumerated(EnumType.STRING)
@Column(name = "tipo", length = 20)
private TipoDireccion tipo;

@Column(name = "direccion", nullable = false)
private String direccion;

@Column(name = "ciudad", nullable = false, length = 100)
private String ciudad;

@Column(name = "region", nullable = false, length = 100)
private String region;

@Column(name = "codigo_postal", length = 20)
private String codigoPostal;

@Column(name = "pais", length = 100)
@Builder.Default
private String pais = "Chile";

@Column(name = "es_principal")
@Builder.Default
private Boolean esPrincipal = false;

@Column(name = "created_at")
private LocalDateTime createdAt;

@Column(name = "version")
@Builder.Default
private Integer version = 1;

public enum TipoDireccion {
    envio,
    facturacion
}


}

