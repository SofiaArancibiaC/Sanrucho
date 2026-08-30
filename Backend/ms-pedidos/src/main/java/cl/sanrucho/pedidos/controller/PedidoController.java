package cl.sanrucho.pedidos.controller;

import cl.sanrucho.pedidos.dto.PedidoRequest;
import cl.sanrucho.pedidos.dto.EstadoUpdateRequest;
import cl.sanrucho.pedidos.dto.PedidoResponse;
import cl.sanrucho.pedidos.dto.ResumenPedidoResponse;
import cl.sanrucho.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestión de órdenes de compra")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido (checkout)")
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse creado = pedidoService.crear(request);
        URI location = URI.create("/api/pedidos/" + creado.getId());
        return ResponseEntity.created(location).body(creado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pedido por id")
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    @GetMapping("/numero/{numeroPedido}")
    @Operation(summary = "Obtener un pedido por número de orden")
    public ResponseEntity<PedidoResponse> obtenerPorNumero(@PathVariable String numeroPedido) {
        return ResponseEntity.ok(pedidoService.obtenerPorNumero(numeroPedido));
    }

    @GetMapping
    @Operation(summary = "Listar pedidos de un usuario (paginado)")
    public ResponseEntity<Page<ResumenPedidoResponse>> listarPorUsuario(
            @RequestParam Integer usuarioId,
            @PageableDefault(size = 20, sort = "fechaPedido") Pageable pageable) {
        return ResponseEntity.ok(pedidoService.listarPorUsuario(usuarioId, pageable));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar el estado de un pedido")
    public ResponseEntity<PedidoResponse> actualizarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody EstadoUpdateRequest request) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, request));
    }
}