package cl.sanrucho.carrito.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/carritos")
public class CarritoController {

    private final Map<Long, Map<String, Object>> carritos = new HashMap<>();

    @GetMapping("/usuario/{usuarioId}")
    public Map<String, Object> obtenerPorUsuario(@PathVariable Long usuarioId) {
        Map<String, Object> carrito = carritos.get(usuarioId);
        if (carrito == null) {
            carrito = new HashMap<>();
            carrito.put("id", usuarioId);
            carrito.put("usuarioId", usuarioId);
            carrito.put("items", new ArrayList<>());
            carrito.put("total", 0.0);
            carritos.put(usuarioId, carrito);
        }
        return carrito;
    }

    @GetMapping("/{id}")
    public Map<String, Object> obtenerPorId(@PathVariable Long id) {
        Map<String, Object> carrito = carritos.get(id);
        if (carrito == null) {
            carrito = new HashMap<>();
            carrito.put("id", id);
            carrito.put("usuarioId", id);
            carrito.put("items", new ArrayList<>());
            carrito.put("total", 0.0);
        }
        return carrito;
    }

    @PostMapping
    public Map<String, Object> crear(@RequestBody Map<String, Object> carrito) {
        Long id = System.currentTimeMillis();
        carrito.put("id", id);
        carrito.put("items", carrito.getOrDefault("items", new ArrayList<>()));
        carritos.put(id, carrito);
        return carrito;
    }

    @PostMapping("/{id}/items")
    public Map<String, Object> agregarItem(@PathVariable Long id, @RequestBody Map<String, Object> item) {
        Map<String, Object> carrito = carritos.get(id);
        if (carrito == null) {
            carrito = new HashMap<>();
            carrito.put("id", id);
            carrito.put("items", new ArrayList<>());
            carritos.put(id, carrito);
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) carrito.get("items");
        items.add(item);
        return carrito;
    }

    @PutMapping("/{id}/items/{productoId}")
    public Map<String, Object> actualizarItem(@PathVariable Long id, @PathVariable Long productoId, @RequestBody Map<String, Object> item) {
        Map<String, Object> carrito = carritos.get(id);
        if (carrito != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) carrito.get("items");
            for (Map<String, Object> i : items) {
                if (productoId.equals(i.get("productoId"))) {
                    i.putAll(item);
                    break;
                }
            }
        }
        return carrito;
    }

    @DeleteMapping("/{id}/items/{productoId}")
    public Map<String, Object> eliminarItem(@PathVariable Long id, @PathVariable Long productoId) {
        Map<String, Object> carrito = carritos.get(id);
        if (carrito != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) carrito.get("items");
            items.removeIf(i -> productoId.equals(i.get("productoId")));
        }
        return carrito;
    }

    @DeleteMapping("/{id}/vaciar")
    public Map<String, Object> vaciar(@PathVariable Long id) {
        Map<String, Object> carrito = carritos.get(id);
        if (carrito != null) {
            carrito.put("items", new ArrayList<>());
        }
        return carrito;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> eliminar(@PathVariable Long id) {
        carritos.remove(id);
        return Map.of("mensaje", "Carrito eliminado");
    }
}
