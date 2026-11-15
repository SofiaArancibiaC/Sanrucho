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
 * Usa el nombre logico "ms-catalogo": Eureka resuelve la IP/puerto y
 * Spring Cloud LoadBalancer reparte entre las instancias disponibles.
 * El token JWT se propaga automaticamente con FeignClientInterceptor.
 */
@FeignClient(name = "ms-catalogo")
public interface CatalogoClient {

    @GetMapping("/api/v1/catalogo/productos/{id}")
    ProductoCatalogoResponse obtenerProducto(@PathVariable("id") Integer id);

    @PatchMapping("/api/v1/catalogo/productos/{id}/stock")
    ProductoCatalogoResponse ajustarStock(
            @PathVariable("id") Integer id,
            @RequestBody StockUpdateRequest request);
}
