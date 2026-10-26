package cl.sanrucho.pedidos.mapper;

import cl.sanrucho.pedidos.dto.*;
import cl.sanrucho.pedidos.model.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "subtotal", ignore = true)      // calculado en el service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    ItemPedido toEntity(ItemPedidoRequest request);

    List<ItemPedido> toEntityList(List<ItemPedidoRequest> requests);

    ItemPedidoResponse toResponse(ItemPedido item);

    List<ItemPedidoResponse> toResponseList(List<ItemPedido> items);
}