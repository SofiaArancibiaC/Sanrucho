package cl.sanrucho.carrito.dto;

import java.time.LocalDateTime;

import cl.sanrucho.carrito.model.entity.Carrito.EstadoCarrito;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

public class CarritoResponse {

    private Integer id;
    private Integer usuarioId;
    private String sesionId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaModificacion;
    private LocalDateTime fechaCierre;
    private EstadoCarrito estado = EstadoCarrito.ACTIVO;
 

}
