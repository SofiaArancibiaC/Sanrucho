package cl.sanrucho.pedidos.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cl.sanrucho.pedidos.dto.PedidoRequest;
import cl.sanrucho.pedidos.dto.PedidoResponse;
import cl.sanrucho.pedidos.dto.PedidoResumenResponse;
import cl.sanrucho.pedidos.model.entity.pedidos.Pedido;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCompra", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Pedido toEntity(PedidoRequest request);

    PedidoResumenResponse toResumen(Pedido pedido);

    @Mapping(target = "detalles", ignore = true)
    PedidoResponse toResponse(Pedido pedido);

    List<PedidoResumenResponse> toResumenList(List<Pedido> pedidos);
}
