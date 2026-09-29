/**
 * Paquete reservado para los clientes Feign de ms-inventario.
 *
 * PENDIENTE (aun no implementado, a proposito):
 *
 * Cuando ms-catalogo este listo, aca deberia vivir un CatalogoClient con
 *
 *     @FeignClient(name = "ms-catalogo")
 *     public interface CatalogoClient {
 *         @GetMapping("/api/v1/productos/{id}")
 *         ProductoResponse existeProducto(@PathVariable Integer id);
 *     }
 *
 * La idea es que InventarioService valide contra ms-catalogo que el productoId y
 * el sku realmente existen, en vez de confiar en los datos que manda el cliente.
 * Hoy el servicio usa el nombreProducto del request, y por eso todavia no se
 * puede dar de alta un inventario de un producto que no exista en el catalogo.
 *
 * Razon del aplazamiento: ms-catalogo todavia no esta desarrollado y sin el stub
 * el modulo no levanta. Se resuelve junto con el resto de la integracion.
 */
package cl.sanrucho.inventario.client;
