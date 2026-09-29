package cl.sanrucho.reviews.controller;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.sanrucho.reviews.dto.ReviewRequest;
import cl.sanrucho.reviews.dto.ReviewResponse;
import cl.sanrucho.reviews.model.Review.EstadoReview;
import cl.sanrucho.reviews.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    private ReviewResponse addLinks(ReviewResponse review) {
        Long id = review.getId();

        review.add(linkTo(methodOn(ReviewController.class).findById(id)).withSelfRel());
        review.add(linkTo(methodOn(ReviewController.class).update(id, null))
                .withRel("update").withTitle("PUT - Actualizar review"));
        review.add(linkTo(methodOn(ReviewController.class).deleteById(id))
                .withRel("delete").withTitle("DELETE - Eliminar review"));
        review.add(linkTo(methodOn(ReviewController.class).findAll())
                .withRel("all").withTitle("GET - Listado de reviews"));

        return review;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<ReviewResponse>> findAll() {
        List<ReviewResponse> reviews = reviewService.findAll();
        reviews.forEach(this::addLinks);

        CollectionModel<ReviewResponse> collection = CollectionModel.of(
                reviews,
                linkTo(methodOn(ReviewController.class).findAll()).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> findById(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(addLinks(reviewService.findById(id)));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<CollectionModel<ReviewResponse>> findByProductoId(@PathVariable Integer productoId) {
        List<ReviewResponse> reviews = reviewService.findByProductoId(productoId);
        reviews.forEach(this::addLinks);

        CollectionModel<ReviewResponse> collection = CollectionModel.of(
                reviews,
                linkTo(methodOn(ReviewController.class).findByProductoId(productoId)).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<ReviewResponse>> findByUsuarioId(@PathVariable Integer usuarioId) {
        List<ReviewResponse> reviews = reviewService.findByUsuarioId(usuarioId);
        reviews.forEach(this::addLinks);

        CollectionModel<ReviewResponse> collection = CollectionModel.of(
                reviews,
                linkTo(methodOn(ReviewController.class).findByUsuarioId(usuarioId)).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<CollectionModel<ReviewResponse>> findByPedidoId(@PathVariable Integer pedidoId) {
        List<ReviewResponse> reviews = reviewService.findByPedidoId(pedidoId);
        reviews.forEach(this::addLinks);

        CollectionModel<ReviewResponse> collection = CollectionModel.of(
                reviews,
                linkTo(methodOn(ReviewController.class).findByPedidoId(pedidoId)).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<CollectionModel<ReviewResponse>> findByEstado(@PathVariable EstadoReview estado) {
        List<ReviewResponse> reviews = reviewService.findByEstado(estado);
        reviews.forEach(this::addLinks);

        CollectionModel<ReviewResponse> collection = CollectionModel.of(
                reviews,
                linkTo(methodOn(ReviewController.class).findByEstado(estado)).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse creado = addLinks(reviewService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> update(
            @PathVariable @NonNull Long id,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(addLinks(reviewService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable @NonNull Long id) {
        reviewService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
