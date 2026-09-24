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

import cl.sanrucho.usuarios.dto.DireccionRequest;
import cl.sanrucho.usuarios.dto.DireccionResponse;
import cl.sanrucho.usuarios.service.DireccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST de direcciones de usuario.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/direcciones")
public class DireccionController {

    private final DireccionService direccionService;

    @GetMapping
    public ResponseEntity<List<DireccionResponse>> findAll() {
        return ResponseEntity.ok(direccionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DireccionResponse> findById(@PathVariable int id) {
        return ResponseEntity.ok(direccionService.findById(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DireccionResponse>> findByUsuarioId(@PathVariable int usuarioId) {
        return ResponseEntity.ok(direccionService.findByUsuarioId(usuarioId));
    }

    @PostMapping
    public ResponseEntity<DireccionResponse> create(@Valid @RequestBody DireccionRequest request) {
        DireccionResponse creada = direccionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DireccionResponse> update(
            @PathVariable int id,
            @Valid @RequestBody DireccionRequest request) {
        return ResponseEntity.ok(direccionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        direccionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
