package cl.sanrucho.inventario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.sanrucho.inventario.dto.EventoInventarioRequest;
import cl.sanrucho.inventario.dto.EventoInventarioResponse;
import cl.sanrucho.inventario.service.EventoInventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST de los eventos de inventario.
 *
 * La mayoria de los eventos los crea InventarioService al mover stock. El alta
 * manual existe para eventos que nacen fuera de una operacion de inventario, y
 * los endpoints de publicacion son el ciclo de vida del outbox.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/eventos-inventario")
public class EventoInventarioController {

    private final EventoInventarioService eventoInventarioService;

    // ─── Consultas ───────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<EventoInventarioResponse>> findAll() {
        return ResponseEntity.ok(eventoInventarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoInventarioResponse> findById(@PathVariable int id) {
        return ResponseEntity.ok(eventoInventarioService.findById(id));
    }

    @GetMapping("/evento/{eventId}")
    public ResponseEntity<EventoInventarioResponse> findByEventId(@PathVariable String eventId) {
        return ResponseEntity.ok(eventoInventarioService.findByEventId(eventId));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<EventoInventarioResponse>> findByProductoId(
            @PathVariable int productoId) {
        return ResponseEntity.ok(eventoInventarioService.findByProductoId(productoId));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<EventoInventarioResponse>> findPendientes() {
        return ResponseEntity.ok(eventoInventarioService.findPendientes());
    }

    @GetMapping("/publicados")
    public ResponseEntity<List<EventoInventarioResponse>> findPublicados() {
        return ResponseEntity.ok(eventoInventarioService.findPublicados());
    }

    // ─── Alta manual y publicacion del outbox ────────────────────────────────

    @PostMapping
    public ResponseEntity<EventoInventarioResponse> create(
            @Valid @RequestBody EventoInventarioRequest request) {
        EventoInventarioResponse creado = eventoInventarioService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PatchMapping("/{id}/publicar")
    public ResponseEntity<EventoInventarioResponse> marcarPublicado(@PathVariable int id) {
        return ResponseEntity.ok(eventoInventarioService.marcarPublicado(id));
    }

    @PatchMapping("/publicar-pendientes")
    public ResponseEntity<List<EventoInventarioResponse>> marcarPendientesComoPublicados() {
        return ResponseEntity.ok(eventoInventarioService.marcarPendientesComoPublicados());
    }
}
