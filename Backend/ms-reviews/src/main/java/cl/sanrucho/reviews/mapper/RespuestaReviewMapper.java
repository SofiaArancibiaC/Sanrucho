package cl.sanrucho.reviews.mapper;

import cl.sanrucho.reviews.dto.RespuestaReviewRequest;
import cl.sanrucho.reviews.dto.RespuestaReviewResponse;
import cl.sanrucho.reviews.model.RespuestaReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RespuestaReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRespuesta", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "esVendedor", ignore = true)
    RespuestaReview toEntity(RespuestaReviewRequest request);

    RespuestaReviewResponse toResponse(RespuestaReview respuesta);

    List<RespuestaReviewResponse> toResponseList(List<RespuestaReview> respuestas);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRespuesta", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "esVendedor", ignore = true)
    void updateEntity(RespuestaReviewRequest request, @MappingTarget RespuestaReview respuesta);
}
