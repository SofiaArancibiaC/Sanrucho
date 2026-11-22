package cl.sanrucho.catalogo.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import cl.sanrucho.catalogo.dto.CategoriaRequest;
import cl.sanrucho.catalogo.dto.CategoriaResponse;
import cl.sanrucho.catalogo.model.entity.categorias.Categoria;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    @Mapping(target = "id", ignore = true)
    Categoria toEntity(CategoriaRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(CategoriaRequest request, @MappingTarget Categoria categoria);

    @Mapping(target = "cantidadProductos", ignore = true)
    CategoriaResponse toResponse(Categoria categoria);

    List<CategoriaResponse> toResponseList(List<Categoria> categorias);
}
