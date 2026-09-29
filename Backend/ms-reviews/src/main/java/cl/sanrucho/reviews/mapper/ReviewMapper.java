package cl.sanrucho.reviews.mapper;

import cl.sanrucho.reviews.dto.ReviewRequest;
import cl.sanrucho.reviews.dto.ReviewResponse;
import cl.sanrucho.reviews.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "verificado", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "votosUtiles", ignore = true)
    @Mapping(target = "votosNoUtiles", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    Review toEntity(ReviewRequest request);

    ReviewResponse toResponse(Review review);

    List<ReviewResponse> toResponseList(List<Review> reviews);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "verificado", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "votosUtiles", ignore = true)
    @Mapping(target = "votosNoUtiles", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(ReviewRequest request, @MappingTarget Review review);
}
