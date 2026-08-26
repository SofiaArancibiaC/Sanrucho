package cl.sanrucho.carrito.dto;

import java.time.LocalDateTime;
import org.springframework.hateoas.RepresentationModel;

import cl.sanrucho.carrito.model.entity.Carrito.EstadoCarrito;
import lombok.Data;

@Data
public class CarritoResponse extends RepresentationModel<CarritoResponse>{

    private Integer id;
    private Integer usuarioId;
    private String sesionId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaModificacion;
    private LocalDateTime fechaCierre;
    private EstadoCarrito estado = EstadoCarrito.ACTIVO;
 

}
