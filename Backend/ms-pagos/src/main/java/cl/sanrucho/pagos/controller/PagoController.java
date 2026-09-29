package cl.sanrucho.pagos.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private final Map<Long, Map<String, Object>> pagos = new HashMap<>();

    @GetMapping
    public List<Map<String, Object>> listar() {
        return new ArrayList<>(pagos.values());
    }

    @GetMapping("/{id}")
    public Map<String, Object> obtenerPorId(@PathVariable Long id) {
        return pagos.get(id);
    }

    @GetMapping("/pedido/{pedidoId}")
    public List<Map<String, Object>> obtenerPorPedido(@PathVariable Long pedidoId) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Map<String, Object> pago : pagos.values()) {
            if (pedidoId.equals(pago.get("pedidoId"))) {
                resultado.add(pago);
            }
        }
        return resultado;
    }

    @PostMapping("/procesar")
    public Map<String, Object> procesar(@RequestBody Map<String, Object> pago) {
        Long id = System.currentTimeMillis();
        pago.put("id", id);
        pago.put("estado", "PROCESADO");
        pago.put("fechaProcesamiento", new Date().toString());
        pagos.put(id, pago);
        return pago;
    }

    @PostMapping("/{id}/reembolsar")
    public Map<String, Object> reembolsar(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Map<String, Object> pago = pagos.get(id);
        if (pago != null) {
            pago.put("estado", "REEMBOLSADO");
            pago.put("motivoReembolso", body.get("motivo"));
        }
        return pago;
    }

    @GetMapping("/{id}/estado")
    public Map<String, Object> verificarEstado(@PathVariable Long id) {
        Map<String, Object> pago = pagos.get(id);
        if (pago == null) {
            return Map.of("error", "Pago no encontrado");
        }
        return Map.of("id", id, "estado", pago.get("estado"));
    }
}
