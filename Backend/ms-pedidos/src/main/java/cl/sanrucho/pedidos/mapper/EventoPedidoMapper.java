package cl.sanrucho.pedidos.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import cl.sanrucho.pedidos.dto.EventoPedidoResponse;
import cl.sanrucho.pedidos.model.entity.EventoPedido;

@Mapper(componentModel = "spring")
public interface EventoPedidoMapper {

    EventoPedidoResponse toResponse(EventoPedido evento);

    List<EventoPedidoResponse> toResponseList(List<EventoPedido> eventos);
}