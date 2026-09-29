package cl.sanrucho.inventario.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cl.sanrucho.inventario.dto.MovimientoInventarioRequest;
import cl.sanrucho.inventario.dto.MovimientoInventarioResponse;
import cl.sanrucho.inventario.model.MovimientoInventario;

/**
 * Mapper MapStruct entre MovimientoInventarioRequest/Response y la entidad
 * MovimientoInventario.
 *
 * El service resuelve la relacion con Inventario a partir del inventarioId del
 * request, y calcula las cantidades y la fecha del movimiento, asi que todo eso
 * se ignora en el mapeo.
 */
@Mapper(componentModel = "spring")
public interface MovimientoInventarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inventario", ignore = true)
    @Mapping(target = "cantidadAnterior", ignore = true)
    @Mapping(target = "cantidadNueva", ignore = true)
    @Mapping(target = "fechaMovimiento", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    MovimientoInventario toModel(MovimientoInventarioRequest request);

    @Mapping(target = "inventarioId", source = "inventario.id")
    MovimientoInventarioResponse toResponse(MovimientoInventario movimiento);

    List<MovimientoInventarioResponse> toResponseList(List<MovimientoInventario> movimientos);
}
