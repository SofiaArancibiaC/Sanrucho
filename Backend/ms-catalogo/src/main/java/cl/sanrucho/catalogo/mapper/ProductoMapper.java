package cl.sanrucho.catalogo.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import cl.sanrucho.catalogo.dto.ProductoRequest;
import cl.sanrucho.catalogo.dto.ProductoResponse;
import cl.sanrucho.catalogo.model.entity.productos.Producto;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mapping(target = "id", ignore = true)
    Producto toEntity(ProductoRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(ProductoRequest request, @MappingTarget Producto producto);

    @Mapping(target = "nombreCategoria", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    ProductoResponse toResponse(Producto producto);

    List<ProductoResponse> toResponseList(List<Producto> productos);
}
