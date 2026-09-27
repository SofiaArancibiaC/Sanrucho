package cl.sanrucho.notificaciones.controller;

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

import cl.sanrucho.notificaciones.dto.EventoNotificacionRequest;
import cl.sanrucho.notificaciones.dto.EventoNotificacionResponse;
import cl.sanrucho.notificaciones.service.EventoNotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/eventos-notificacion")
@RequiredArgsConstructor 
public class EventoNotificacionController {
 
    private final EventoNotificacionService eventoService;
 
    @PostMapping 
    public ResponseEntity<EventoNotificacionResponse> crear(@Valid @RequestBody EventoNotificacionRequest dto) {
        EventoNotificacionResponse creado = eventoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
 
    @GetMapping ("/pendientes")
    public ResponseEntity<List<EventoNotificacionResponse>> listarPendientes() {
        return ResponseEntity.ok(eventoService.listarPendientes());
    }
 
    @GetMapping("/notificacion/{notificacionId}")
    public ResponseEntity<List<EventoNotificacionResponse>> listarPorNotificacion(
            @PathVariable Integer notificacionId) {
        return ResponseEntity.ok(eventoService.listarPorNotificacion(notificacionId));
    }
 
    @PatchMapping("/{id}/publicado")
    public ResponseEntity<EventoNotificacionResponse> marcarComoPublicado(@PathVariable Integer id) {
        return ResponseEntity.ok(eventoService.marcarComoPublicado(id));
    }
}