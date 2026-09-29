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

import cl.sanrucho.reviews.dto.EventoReviewRequest;
import cl.sanrucho.reviews.dto.EventoReviewResponse;
import cl.sanrucho.reviews.model.EventoReview.TipoEvento;
import cl.sanrucho.reviews.service.EventoReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/eventos-reviews")
public class EventoReviewController {

    private final EventoReviewService eventoService;

    private EventoReviewResponse addLinks(EventoReviewResponse evento) {
        Long id = evento.getId();

        evento.add(linkTo(methodOn(EventoReviewController.class).findById(id)).withSelfRel());
        evento.add(linkTo(methodOn(EventoReviewController.class).update(id, null))
                .withRel("update").withTitle("PUT - Actualizar evento"));
        evento.add(linkTo(methodOn(EventoReviewController.class).deleteById(id))
                .withRel("delete").withTitle("DELETE - Eliminar evento"));
        evento.add(linkTo(methodOn(EventoReviewController.class).findAll())
                .withRel("all").withTitle("GET - Listado de eventos"));

        return evento;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EventoReviewResponse>> findAll() {
        List<EventoReviewResponse> eventos = eventoService.findAll();
        eventos.forEach(this::addLinks);

        CollectionModel<EventoReviewResponse> collection = CollectionModel.of(
                eventos,
                linkTo(methodOn(EventoReviewController.class).findAll()).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoReviewResponse> findById(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(addLinks(eventoService.findById(id)));
    }

    @GetMapping("/event-id/{eventId}")
    public ResponseEntity<EventoReviewResponse> findByEventId(@PathVariable String eventId) {
        return ResponseEntity.ok(addLinks(eventoService.findByEventId(eventId)));
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<CollectionModel<EventoReviewResponse>> findByReviewId(@PathVariable Integer reviewId) {
        List<EventoReviewResponse> eventos = eventoService.findByReviewId(reviewId);
        eventos.forEach(this::addLinks);

        CollectionModel<EventoReviewResponse> collection = CollectionModel.of(
                eventos,
                linkTo(methodOn(EventoReviewController.class).findByReviewId(reviewId)).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<CollectionModel<EventoReviewResponse>> findByProductoId(@PathVariable Integer productoId) {
        List<EventoReviewResponse> eventos = eventoService.findByProductoId(productoId);
        eventos.forEach(this::addLinks);

        CollectionModel<EventoReviewResponse> collection = CollectionModel.of(
                eventos,
                linkTo(methodOn(EventoReviewController.class).findByProductoId(productoId)).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/publicado/{publicado}")
    public ResponseEntity<CollectionModel<EventoReviewResponse>> findByPublicado(@PathVariable Boolean publicado) {
        List<EventoReviewResponse> eventos = eventoService.findByPublicado(publicado);
        eventos.forEach(this::addLinks);

        CollectionModel<EventoReviewResponse> collection = CollectionModel.of(
                eventos,
                linkTo(methodOn(EventoReviewController.class).findByPublicado(publicado)).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/tipo-evento/{tipoEvento}")
    public ResponseEntity<CollectionModel<EventoReviewResponse>> findByTipoEvento(@PathVariable TipoEvento tipoEvento) {
        List<EventoReviewResponse> eventos = eventoService.findByTipoEvento(tipoEvento);
        eventos.forEach(this::addLinks);

        CollectionModel<EventoReviewResponse> collection = CollectionModel.of(
                eventos,
                linkTo(methodOn(EventoReviewController.class).findByTipoEvento(tipoEvento)).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @PostMapping
    public ResponseEntity<EventoReviewResponse> create(@Valid @RequestBody EventoReviewRequest request) {
        EventoReviewResponse creado = addLinks(eventoService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoReviewResponse> update(
            @PathVariable @NonNull Long id,
            @Valid @RequestBody EventoReviewRequest request) {
        return ResponseEntity.ok(addLinks(eventoService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable @NonNull Long id) {
        eventoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
