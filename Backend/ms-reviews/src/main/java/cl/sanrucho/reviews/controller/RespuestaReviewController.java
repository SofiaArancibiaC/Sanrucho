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

import cl.sanrucho.reviews.dto.RespuestaReviewRequest;
import cl.sanrucho.reviews.dto.RespuestaReviewResponse;
import cl.sanrucho.reviews.service.RespuestaReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/respuestas-reviews")
public class RespuestaReviewController {

    private final RespuestaReviewService respuestaService;

    private RespuestaReviewResponse addLinks(RespuestaReviewResponse respuesta) {
        Long id = respuesta.getId();

        respuesta.add(linkTo(methodOn(RespuestaReviewController.class).findById(id)).withSelfRel());
        respuesta.add(linkTo(methodOn(RespuestaReviewController.class).update(id, null))
                .withRel("update").withTitle("PUT - Actualizar respuesta"));
        respuesta.add(linkTo(methodOn(RespuestaReviewController.class).deleteById(id))
                .withRel("delete").withTitle("DELETE - Eliminar respuesta"));
        respuesta.add(linkTo(methodOn(RespuestaReviewController.class).findAll())
                .withRel("all").withTitle("GET - Listado de respuestas"));

        return respuesta;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<RespuestaReviewResponse>> findAll() {
        List<RespuestaReviewResponse> respuestas = respuestaService.findAll();
        respuestas.forEach(this::addLinks);

        CollectionModel<RespuestaReviewResponse> collection = CollectionModel.of(
                respuestas,
                linkTo(methodOn(RespuestaReviewController.class).findAll()).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespuestaReviewResponse> findById(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(addLinks(respuestaService.findById(id)));
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<CollectionModel<RespuestaReviewResponse>> findByReviewId(@PathVariable Integer reviewId) {
        List<RespuestaReviewResponse> respuestas = respuestaService.findByReviewId(reviewId);
        respuestas.forEach(this::addLinks);

        CollectionModel<RespuestaReviewResponse> collection = CollectionModel.of(
                respuestas,
                linkTo(methodOn(RespuestaReviewController.class).findByReviewId(reviewId)).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @PostMapping
    public ResponseEntity<RespuestaReviewResponse> create(@Valid @RequestBody RespuestaReviewRequest request) {
        RespuestaReviewResponse creado = addLinks(respuestaService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespuestaReviewResponse> update(
            @PathVariable @NonNull Long id,
            @Valid @RequestBody RespuestaReviewRequest request) {
        return ResponseEntity.ok(addLinks(respuestaService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable @NonNull Long id) {
        respuestaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
