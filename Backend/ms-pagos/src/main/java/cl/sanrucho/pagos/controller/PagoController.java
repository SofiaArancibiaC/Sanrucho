package cl.sanrucho.pagos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.sanrucho.pagos.dto.ActualizarEstadoRequest;
import cl.sanrucho.pagos.dto.ReembolsoRequest;
import cl.sanrucho.pagos.dto.ReembolsoResponse;
import cl.sanrucho.pagos.dto.TransaccionRequest;
import cl.sanrucho.pagos.dto.TransaccionResponse;
import cl.sanrucho.pagos.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {
    
    private final PagoService pagoService;

    // --- Transacciones 

    @PostMapping("/transacciones")
    public ResponseEntity<TransaccionResponse> crearTransaccion(@Valid @RequestBody TransaccionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.crearTransaccion(request));
    }

    @GetMapping("/transacciones/{id}")
    public ResponseEntity<TransaccionResponse> obtenerTransaccionPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(pagoService.obtenerTransaccionPorId(id));
    }

    @GetMapping("/transacciones/pedido/{pedidoId}")
    public ResponseEntity<List<TransaccionResponse>> listarTransaccionesPorPedido(@PathVariable Integer pedidoId) {
        return ResponseEntity.ok(pagoService.listarTransaccionesPorPedido(pedidoId));
    }

    @PutMapping("/transacciones/{id}/estado")
    public ResponseEntity<TransaccionResponse> actualizarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarEstadoRequest nuevoEstado) {
        return ResponseEntity.ok(pagoService.actualizarEstado(id, nuevoEstado.getNuevoEstado()));
    }

    // --- Reembolsos 

    @PostMapping("/reembolsos")
    public ResponseEntity<ReembolsoResponse> crearReembolso(@Valid @RequestBody ReembolsoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.crearReembolso(request));
    }

    @GetMapping("/reembolsos/{id}")
    public ResponseEntity<ReembolsoResponse> obtenerReembolsoPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(pagoService.obtenerReembolsoPorId(id));
    }

    @GetMapping("/reembolsos/transaccion/{transaccionId}")
    public ResponseEntity<List<ReembolsoResponse>> listarReembolsosPorTransaccion(@PathVariable Integer transaccionId) {
        return ResponseEntity.ok(pagoService.listarReembolsosPorTransaccion(transaccionId));
    }
}
