package cl.sanrucho.inventario.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import cl.sanrucho.inventario.dto.InventarioRequest;
import cl.sanrucho.inventario.dto.InventarioResponse;
import cl.sanrucho.inventario.model.Inventario;

/**
 * Mapper MapStruct entre InventarioRequest/Response y la entidad Inventario.
 *
 * El mapper solo copia datos descriptivos. Todo lo que es estado derivado queda
 * fuera a proposito y lo calcula el service: el stock actual, el reservado y el
 * disponible solo cambian por movimiento, la alerta se recalcula en cada
 * operacion, y createdAt y version los administra la base de datos y Hibernate.
 */
@Mapper(componentModel = "spring")
public interface InventarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stockActual", ignore = true)
    @Mapping(target = "stockReservado", ignore = true)
    @Mapping(target = "stockDisponible", ignore = true)
    @Mapping(target = "alertaStockBajo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "movimientos", ignore = true)
    Inventario toModel(InventarioRequest request);

    InventarioResponse toResponse(Inventario inventario);

    List<InventarioResponse> toResponseList(List<Inventario> inventarios);

    // Actualizacion sobre la entidad persistida (Dirty Checking).
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stockActual", ignore = true)
    @Mapping(target = "stockReservado", ignore = true)
    @Mapping(target = "stockDisponible", ignore = true)
    @Mapping(target = "alertaStockBajo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "movimientos", ignore = true)
    void updateEntity(InventarioRequest request, @MappingTarget Inventario inventario);
}
