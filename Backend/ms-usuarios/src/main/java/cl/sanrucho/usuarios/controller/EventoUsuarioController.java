package cl.sanrucho.usuarios.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.sanrucho.usuarios.dto.EventoUsuarioRequest;
import cl.sanrucho.usuarios.dto.EventoUsuarioResponse;
import cl.sanrucho.usuarios.service.EventoUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST de eventos de usuario.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/eventos-usuario")
public class EventoUsuarioController {

    private final EventoUsuarioService eventoUsuarioService;

    @GetMapping
    public ResponseEntity<List<EventoUsuarioResponse>> findAll() {
        return ResponseEntity.ok(eventoUsuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoUsuarioResponse> findById(@PathVariable int id) {
        return ResponseEntity.ok(eventoUsuarioService.findById(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EventoUsuarioResponse>> findByUsuarioId(@PathVariable int usuarioId) {
        return ResponseEntity.ok(eventoUsuarioService.findByUsuarioId(usuarioId));
    }

    @PostMapping
    public ResponseEntity<EventoUsuarioResponse> create(@Valid @RequestBody EventoUsuarioRequest request) {
        EventoUsuarioResponse creado = eventoUsuarioService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoUsuarioResponse> update(
            @PathVariable int id,
            @Valid @RequestBody EventoUsuarioRequest request) {
        return ResponseEntity.ok(eventoUsuarioService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        eventoUsuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
