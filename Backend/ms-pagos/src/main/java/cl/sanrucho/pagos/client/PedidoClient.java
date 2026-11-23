package cl.sanrucho.pagos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import cl.sanrucho.pagos.client.dto.EstadoUpdateRequest;
import cl.sanrucho.pagos.client.dto.PedidoResponse;

/**
 * Cliente Feign hacia ms-pedidos.
 *
 * Con Eureka no es necesario indicar la URL: se resuelve por el nombre
 * del servicio registrado.
 */
@FeignClient(name = "ms-pedidos")
public interface PedidoClient {

    @GetMapping("/api/v1/pedidos/{id}")
    PedidoResponse obtenerPedido(@PathVariable Integer id);

    @PatchMapping("/api/v1/pedidos/{id}/estado")
    void actualizarEstado(@PathVariable Integer id, @RequestBody EstadoUpdateRequest request);
}
