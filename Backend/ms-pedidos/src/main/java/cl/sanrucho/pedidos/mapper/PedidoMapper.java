package cl.sanrucho.pedidos.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cl.sanrucho.pedidos.dto.PedidoRequest;
import cl.sanrucho.pedidos.dto.PedidoResponse;
import cl.sanrucho.pedidos.dto.ResumenPedidoResponse;
import cl.sanrucho.pedidos.model.entity.Pedido;

@Mapper(componentModel = "spring", uses = ItemPedidoMapper.class)
public interface PedidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numeroPedido", ignore = true)     
    @Mapping(target = "fechaPedido", ignore = true)       
    @Mapping(target = "estado", ignore = true)            
    @Mapping(target = "subtotal", ignore = true)          
    @Mapping(target = "total", ignore = true)             
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "items", ignore = true)      
    @Mapping(target = "createdBy", ignore = true)       
    Pedido toEntity(PedidoRequest request);


    PedidoResponse toResponse(Pedido pedido);

    List<PedidoResponse> toResponseList(List<Pedido> pedidos);

    @Mapping(target = "cantidadItems", expression = "java(pedido.getItems() != null ? pedido.getItems().size() : 0)")
    ResumenPedidoResponse toSummaryResponse(Pedido pedido);

    List<ResumenPedidoResponse> toSummaryResponseList(List<Pedido> pedidos);
}