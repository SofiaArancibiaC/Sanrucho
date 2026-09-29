package cl.sanrucho.notificaciones.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    private final Map<Long, Map<String, Object>> notificaciones = new HashMap<>();

    @GetMapping
    public List<Map<String, Object>> listar() {
        return new ArrayList<>(notificaciones.values());
    }

    @GetMapping("/{id}")
    public Map<String, Object> obtenerPorId(@PathVariable Long id) {
        return notificaciones.get(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Map<String, Object>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Map<String, Object> notif : notificaciones.values()) {
            if (usuarioId.equals(notif.get("usuarioId"))) {
                resultado.add(notif);
            }
        }
        return resultado;
    }

    @PostMapping("/enviar")
    public Map<String, Object> enviar(@RequestBody Map<String, Object> notificacion) {
        Long id = System.currentTimeMillis();
        notificacion.put("id", id);
        notificacion.put("fechaEnvio", new Date().toString());
        notificacion.put("leida", false);
        notificaciones.put(id, notificacion);
        return notificacion;
    }

    @PatchMapping("/{id}/leida")
    public Map<String, Object> marcarLeida(@PathVariable Long id) {
        Map<String, Object> notificacion = notificaciones.get(id);
        if (notificacion != null) {
            notificacion.put("leida", true);
        }
        return notificacion;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> eliminar(@PathVariable Long id) {
        notificaciones.remove(id);
        return Map.of("mensaje", "Notificación eliminada");
    }
}
