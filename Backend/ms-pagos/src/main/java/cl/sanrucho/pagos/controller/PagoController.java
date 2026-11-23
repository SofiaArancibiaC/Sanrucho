package cl.sanrucho.pagos.controller;

import java.net.URI;
import java.util.List;

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
import cl.sanrucho.pagos.dto.EstadoPagoRequest;
import cl.sanrucho.pagos.dto.PagoRequest;
import cl.sanrucho.pagos.dto.PagoResponse;
import cl.sanrucho.pagos.model.enums.EstadoTransaccion;
import cl.sanrucho.pagos.service.PagoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pagos")
@Tag(name = "Pagos", description = "Transacciones y sincronizacion del estado del pedido")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @GetMapping
    public PageResponse<PagoResponse> listar(
            @RequestParam(required = false) Integer idPedido,
            @RequestParam(required = false) EstadoTransaccion estado,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return pagoService.listar(idPedido, estado, pageable);
    }

    @GetMapping("/{id}")
    public PagoResponse obtener(@PathVariable Integer id) {
        return pagoService.obtener(id);
    }

    @GetMapping("/pedido/{idPedido}")
    public List<PagoResponse> listarPorPedido(@PathVariable Integer idPedido) {
        return pagoService.listarPorPedido(idPedido);
    }

    @PostMapping
    public ResponseEntity<PagoResponse> crear(@Valid @RequestBody PagoRequest request) {
        PagoResponse creado = pagoService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/v1/pagos/" + creado.getId()))
                .body(creado);
    }

    @PatchMapping("/{id}/estado")
    public PagoResponse actualizarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody EstadoPagoRequest request) {

        return pagoService.actualizarEstado(id, request.getEstado());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        pagoService.eliminar(id);
    }
}
