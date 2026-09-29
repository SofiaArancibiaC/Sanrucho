package cl.sanrucho.carrito.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.sanrucho.carrito.dto.ProductoResponse;

@FeignClient(name = "ms-catalogo")
public interface CatalogoClient {

    @GetMapping("/api/v1/productos/{id}")
    ProductoResponse getProductoById(@PathVariable Integer id);

}