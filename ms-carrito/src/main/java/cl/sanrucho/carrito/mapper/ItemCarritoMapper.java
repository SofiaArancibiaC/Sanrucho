package cl.sanrucho.carrito.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cl.sanrucho.carrito.dto.AgregarItemRequest;
import cl.sanrucho.carrito.dto.ItemCarritoResponse;
import cl.sanrucho.carrito.model.entity.ItemCarrito;

@Mapper(componentModel = "spring")
public interface ItemCarritoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "carrito", ignore = true)
    @Mapping(target = "productoId", ignore = true)
    @Mapping(target = "nombreProducto", ignore = true)
    @Mapping(target = "personaje", ignore = true)
    @Mapping(target = "precioUnitario", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "fechaAgregado", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    ItemCarrito toEntity(AgregarItemRequest request);

    @Mapping(target = "precioUnit", ignore = true)
    ItemCarritoResponse toResponse(ItemCarrito itemCarrito);
}
