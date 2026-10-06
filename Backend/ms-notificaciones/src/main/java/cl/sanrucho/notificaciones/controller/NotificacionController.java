package cl.sanrucho.notificaciones.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.sanrucho.notificaciones.dto.NotificacionRequest;
import cl.sanrucho.notificaciones.dto.NotificacionResponse;
import cl.sanrucho.notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/notificaciones")
@RequiredArgsConstructor 
public class NotificacionController {
 
    private final NotificacionService notificacionService;
 
    @PostMapping 
    public ResponseEntity<NotificacionResponse> crear(@Valid @RequestBody NotificacionRequest dto) {
        NotificacionResponse creada = notificacionService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }
 
    @GetMapping ("/{id}")
    public ResponseEntity<NotificacionResponse> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }
 
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponse>> listarPorUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(notificacionService.listarPorUsuario(usuarioId));
    }
 
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NotificacionResponse>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(notificacionService.listarPorEstado(estado));
    }
 
    @PatchMapping ("/{id}/enviada")
    public ResponseEntity<NotificacionResponse> marcarComoEnviada(@PathVariable Integer id) {
        return ResponseEntity.ok(notificacionService.marcarComoEnviada(id));
    }
 
    @PatchMapping("/{id}/leida")
    public ResponseEntity<NotificacionResponse> marcarComoLeida(@PathVariable Integer id) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(id));
    }
 
    @PatchMapping("/{id}/fallida")
    public ResponseEntity<NotificacionResponse> marcarComoFallida(@PathVariable Integer id) {
        return ResponseEntity.ok(notificacionService.marcarComoFallida(id));
    }
 
    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
