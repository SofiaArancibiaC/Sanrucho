package cl.sanrucho.pedidos.controller;

import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.sanrucho.common.dto.PageResponse;
import cl.sanrucho.pedidos.dto.EstadoUpdateRequest;
import cl.sanrucho.pedidos.dto.PedidoRequest;
import cl.sanrucho.pedidos.dto.PedidoResumenResponse;
import cl.sanrucho.pedidos.dto.PedidoResponse;
import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import cl.sanrucho.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public PageResponse<PedidoResumenResponse> listar(
            @RequestParam(required = false) Integer idUsuario,
            @RequestParam(required = false) EstadoPedido estado,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return pedidoService.listar(idUsuario, estado, pageable);
    }

    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable Integer id) {
        return pedidoService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse creado = pedidoService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/v1/pedidos/" + creado.getId()))
                .body(creado);
    }

    @PatchMapping("/{id}/estado")
    public PedidoResponse actualizarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody EstadoUpdateRequest request) {

        return pedidoService.actualizarEstado(id, request.getEstado());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        pedidoService.eliminar(id);
    }
}
