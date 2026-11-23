package cl.sanrucho.pedidos.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cl.sanrucho.pedidos.dto.DetallePedidoResponse;
import cl.sanrucho.pedidos.model.entity.detallepedidos.DetallePedido;

@Mapper(componentModel = "spring")
public interface DetallePedidoMapper {

    @Mapping(target = "idDetalle", source = "id")
    @Mapping(target = "nombreProducto", ignore = true)
    DetallePedidoResponse toResponse(DetallePedido detalle);

    List<DetallePedidoResponse> toResponseList(List<DetallePedido> detalles);
}
