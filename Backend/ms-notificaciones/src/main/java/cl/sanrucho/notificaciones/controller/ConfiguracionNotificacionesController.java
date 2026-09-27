package cl.sanrucho.notificaciones.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.sanrucho.notificaciones.dto.ConfiguracionNotificacionesRequest;
import cl.sanrucho.notificaciones.dto.ConfiguracionNotificacionesResponse;
import cl.sanrucho.notificaciones.service.ConfiguracionNotificacionesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/configuracion-notificaciones")
@RequiredArgsConstructor 
public class ConfiguracionNotificacionesController {
 
    private final ConfiguracionNotificacionesService configuracionService;
 
    @PostMapping 
    public ResponseEntity<ConfiguracionNotificacionesResponse> crear(
            @Valid @RequestBody ConfiguracionNotificacionesRequest dto) {
        ConfiguracionNotificacionesResponse creada = configuracionService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }
 
    @GetMapping ("/{usuarioId}")
    public ResponseEntity<ConfiguracionNotificacionesResponse> obtenerPorUsuarioId(
            @PathVariable Integer usuarioId) {
        return ResponseEntity.ok(configuracionService.obtenerPorUsuarioId(usuarioId));
    }
 
    @PutMapping("/{usuarioId}")
    public ResponseEntity<ConfiguracionNotificacionesResponse> actualizar(
            @PathVariable Integer usuarioId,
            @Valid @RequestBody ConfiguracionNotificacionesRequest dto) {
        return ResponseEntity.ok(configuracionService.actualizar(usuarioId, dto));
    }
}