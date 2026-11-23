package cl.sanrucho.pedidos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.sanrucho.pedidos.client.dto.UsuarioResponse;

/**
 * Cliente Feign hacia ms-usuarios. Se usa para validar que el usuario
 * existe antes de crear el pedido.
 */
@FeignClient(name = "ms-usuarios")
public interface UsuarioClient {

    @GetMapping("/api/v1/usuarios/{id}")
    UsuarioResponse obtenerUsuario(@PathVariable Integer id);
}
