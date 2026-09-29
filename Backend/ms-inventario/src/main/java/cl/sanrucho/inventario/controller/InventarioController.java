package cl.sanrucho.inventario.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.sanrucho.inventario.dto.AjusteStockRequest;
import cl.sanrucho.inventario.dto.InventarioRequest;
import cl.sanrucho.inventario.dto.InventarioResponse;
import cl.sanrucho.inventario.dto.OperacionStockRequest;
import cl.sanrucho.inventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST del inventario de productos.
 *
 * Ademas del CRUD habitual, expone las operaciones de stock. Cada una devuelve
 * el inventario ya actualizado, para que el cliente reciba el stockDisponible
 * y la alerta recalculados sin tener que consultar de nuevo.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventarios")
public class InventarioController {

    private final InventarioService inventarioService;

    // ─── Consultas ───────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<InventarioResponse>> findAll() {
        return ResponseEntity.ok(inventarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponse> findById(@PathVariable int id) {
        return ResponseEntity.ok(inventarioService.findById(id));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<InventarioResponse> findByProductoId(@PathVariable int productoId) {
        return ResponseEntity.ok(inventarioService.findByProductoId(productoId));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<InventarioResponse> findBySku(@PathVariable String sku) {
        return ResponseEntity.ok(inventarioService.findBySku(sku));
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<InventarioResponse>> findConAlertaStockBajo() {
        return ResponseEntity.ok(inventarioService.findConAlertaStockBajo());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<InventarioResponse>> findByNombreProducto(
            @RequestParam String nombre) {
        return ResponseEntity.ok(inventarioService.findByNombreProducto(nombre));
    }

    // ─── Alta, actualizacion y baja ───────────────────────────────────────────

    @PostMapping
    public ResponseEntity<InventarioResponse> create(@Valid @RequestBody InventarioRequest request) {
        InventarioResponse creado = inventarioService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioResponse> update(
            @PathVariable int id,
            @Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(inventarioService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        inventarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Operaciones de stock ─────────────────────────────────────────────────

    @PostMapping("/{id}/entrada")
    public ResponseEntity<InventarioResponse> registrarEntrada(
            @PathVariable int id,
            @Valid @RequestBody OperacionStockRequest request) {
        return ResponseEntity.ok(inventarioService.registrarEntrada(id, request));
    }

    @PostMapping("/{id}/salida")
    public ResponseEntity<InventarioResponse> registrarSalida(
            @PathVariable int id,
            @Valid @RequestBody OperacionStockRequest request) {
        return ResponseEntity.ok(inventarioService.registrarSalida(id, request));
    }

    @PostMapping("/{id}/reserva")
    public ResponseEntity<InventarioResponse> registrarReserva(
            @PathVariable int id,
            @Valid @RequestBody OperacionStockRequest request) {
        return ResponseEntity.ok(inventarioService.registrarReserva(id, request));
    }

    @PostMapping("/{id}/liberacion")
    public ResponseEntity<InventarioResponse> registrarLiberacion(
            @PathVariable int id,
            @Valid @RequestBody OperacionStockRequest request) {
        return ResponseEntity.ok(inventarioService.registrarLiberacion(id, request));
    }

    @PostMapping("/{id}/ajuste")
    public ResponseEntity<InventarioResponse> registrarAjuste(
            @PathVariable int id,
            @Valid @RequestBody AjusteStockRequest request) {
        return ResponseEntity.ok(inventarioService.registrarAjuste(id, request));
    }
}
