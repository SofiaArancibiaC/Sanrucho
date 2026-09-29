package cl.sanrucho.inventario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.sanrucho.inventario.dto.MovimientoInventarioResponse;
import cl.sanrucho.inventario.service.MovimientoInventarioService;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST de consulta del historial de movimientos de inventario.
 *
 * Es de solo lectura: los movimientos se generan al aplicar las operaciones de
 * stock de InventarioController, que son las que los dejan consistentes con el
 * stock actual. Para ver el historial de un producto:
 * /api/v1/movimientos-inventario/inventario/{inventarioId}.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movimientos-inventario")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoInventarioService;

    @GetMapping
    public ResponseEntity<List<MovimientoInventarioResponse>> findAll() {
        return ResponseEntity.ok(movimientoInventarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventarioResponse> findById(@PathVariable int id) {
        return ResponseEntity.ok(movimientoInventarioService.findById(id));
    }

    @GetMapping("/inventario/{inventarioId}")
    public ResponseEntity<List<MovimientoInventarioResponse>> findByInventarioId(
            @PathVariable int inventarioId) {
        return ResponseEntity.ok(movimientoInventarioService.findByInventarioId(inventarioId));
    }
}
