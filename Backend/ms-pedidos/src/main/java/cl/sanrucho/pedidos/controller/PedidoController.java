package cl.sanrucho.pedidos.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final Map<Long, Map<String, Object>> pedidos = new HashMap<>();

    @GetMapping
    public List<Map<String, Object>> listar() {
        return new ArrayList<>(pedidos.values());
    }

    @GetMapping("/{id}")
    public Map<String, Object> obtenerPorId(@PathVariable Long id) {
        return pedidos.get(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Map<String, Object>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Map<String, Object> pedido : pedidos.values()) {
            if (usuarioId.equals(pedido.get("usuarioId"))) {
                resultado.add(pedido);
            }
        }
        return resultado;
    }

    @PostMapping
    public Map<String, Object> crear(@RequestBody Map<String, Object> pedido) {
        Long id = System.currentTimeMillis();
        pedido.put("id", id);
        pedido.put("fechaCreacion", new Date().toString());
        pedidos.put(id, pedido);
        return pedido;
    }

    @PatchMapping("/{id}/estado")
    public Map<String, Object> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Map<String, Object> pedido = pedidos.get(id);
        if (pedido != null) {
            pedido.put("estado", body.get("estado"));
        }
        return pedido;
    }

    @PostMapping("/{id}/cancelar")
    public Map<String, Object> cancelar(@PathVariable Long id) {
        Map<String, Object> pedido = pedidos.get(id);
        if (pedido != null) {
            pedido.put("estado", "CANCELADO");
        }
        return pedido;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> eliminar(@PathVariable Long id) {
        pedidos.remove(id);
        return Map.of("mensaje", "Pedido eliminado");
    }
}
