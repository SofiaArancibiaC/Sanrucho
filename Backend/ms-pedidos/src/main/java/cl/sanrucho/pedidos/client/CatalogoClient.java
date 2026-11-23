package cl.sanrucho.pedidos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import cl.sanrucho.pedidos.client.dto.ProductoCatalogoResponse;
import cl.sanrucho.pedidos.client.dto.StockUpdateRequest;

/**
 * Cliente Feign hacia ms-catalogo.
 *
 * Con Eureka no es necesario indicar la URL: se resuelve por el nombre
 * del servicio registrado.
 */
@FeignClient(name = "ms-catalogo")
public interface CatalogoClient {

    @GetMapping("/api/v1/productos/{id}")
    ProductoCatalogoResponse obtenerProducto(@PathVariable Integer id);

    @PatchMapping("/api/v1/productos/{id}/stock")
    ProductoCatalogoResponse ajustarStock(
            @PathVariable Integer id,
            @RequestBody StockUpdateRequest request);
}
