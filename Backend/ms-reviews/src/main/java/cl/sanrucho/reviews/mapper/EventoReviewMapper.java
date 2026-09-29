package cl.sanrucho.reviews.mapper;

import cl.sanrucho.reviews.dto.EventoReviewRequest;
import cl.sanrucho.reviews.dto.EventoReviewResponse;
import cl.sanrucho.reviews.model.EventoReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventoReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaEvento", ignore = true)
    @Mapping(target = "fechaPublicacion", ignore = true)
    @Mapping(target = "publicado", ignore = true)
    EventoReview toEntity(EventoReviewRequest request);

    EventoReviewResponse toResponse(EventoReview evento);

    List<EventoReviewResponse> toResponseList(List<EventoReview> eventos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaEvento", ignore = true)
    @Mapping(target = "fechaPublicacion", ignore = true)
    @Mapping(target = "publicado", ignore = true)
    void updateEntity(EventoReviewRequest request, @MappingTarget EventoReview evento);
}
