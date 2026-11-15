# Arquitectura y decisiones de diseno

## 1. Vista general

```
                         +-----------------------+
                         |   Frontend (futuro)   |
                         +-----------+-----------+
                                     | HTTP + JWT
                                     v
                         +-----------------------+
                         |  api-gateway :9000    |
                         +-----+-----------+-----+
                               |           |
                /api/v1/catalogo/**   /api/v1/pedidos/**
                               |           |
                               v           v
                    +-------------+   +-------------+
                    | ms-catalogo |   | ms-pedidos  |
                    |    :9002    |<--|    :9003    |
                    +------+------+   +------+------+
                           |                 |
             +-------------+-------+   +-----+----------------+
             |                     |   |                      |
        BD productos         BD categorias   BD pedidos   BD detalle_pedidos
             |                     |   |                      |
             +---------------------+   +----------------------+
                           (una base por tabla, sin FK fisicas)

                    +---------------------------+
                    |   Eureka Server :8761     |
                    +---------------------------+
        Todos los servicios se registran y se resuelven por nombre.
```

## 2. Por que dos datasources por microservicio

Se eligio el diseno **una base de datos por tabla**. Cada microservicio
necesita, por lo tanto, dos conexiones:

| Microservicio | Datasource primario | Datasource secundario |
|---------------|---------------------|-----------------------|
| ms-catalogo   | `productos`         | `categorias`          |
| ms-pedidos    | `pedidos`           | `detalle_pedidos`     |

Spring Boot solo auto-configura **un** `DataSource` y **un**
`EntityManagerFactory`. Para soportar dos se excluyen las auto-configuraciones
y se declaran manualmente:

```yaml
spring.autoconfigure.exclude:
  - DataSourceAutoConfiguration
  - DataSourceTransactionManagerAutoConfiguration
  - HibernateJpaAutoConfiguration
  - JpaRepositoriesAutoConfiguration
```

Cada configuracion (`ProductoDataSourceConfig`, `CategoriaDataSourceConfig`,
`PedidoDataSourceConfig`, `DetallePedidoDataSourceConfig`) define:

- `DataSourceProperties` + `HikariDataSource`
- `LocalContainerEntityManagerFactoryBean` con escaneo de un paquete concreto
  de entidades
- `JpaTransactionManager`
- `@EnableJpaRepositories` con `basePackages`, `entityManagerFactoryRef` y
  `transactionManagerRef`

Los datasources primarios (`productos`, `pedidos`) son `@Primary`. Las clases de
servicio que escriben en la base secundaria declaran su transaction manager de
forma explicita, por ejemplo:

```java
@Transactional(transactionManager = "categoriasTransactionManager")
@Transactional(transactionManager = "detallePedidosTransactionManager")
```

### Entidades y paquetes

```
ms-catalogo
  model/entity/productos/Producto.java      -> BD productos
  model/entity/categorias/Categoria.java    -> BD categorias
  repository/productos/ProductoRepository   -> EntityManager de productos
  repository/categorias/CategoriaRepository -> EntityManager de categorias

ms-pedidos
  model/entity/pedidos/Pedido.java          -> BD pedidos
  model/entity/detallepedidos/DetallePedido.java -> BD detalle_pedidos
  repository/pedidos/PedidoRepository
  repository/detallepedidos/DetallePedidoRepository
```

## 3. Reglas de negocio implementadas

### ms-catalogo

| Regla | Implementacion |
|-------|----------------|
| Codigo de producto unico | `ProductoService.crear/actualizar` + `existsByCodigo` |
| Categoria debe existir | `validarCategoria()` consulta la BD `categorias` |
| Precio > 0 | Bean Validation (`@DecimalMin`) + validacion de servicio |
| Stock >= 0 | `@Min(0)` y validacion al ajustar stock |
| Oferta coherente | Si `oferta=true`, `precioOferta` obligatorio y menor al precio |
| Descuento automatico | `calcularDescuento()` si no se envia explicitamente |
| Stock nunca negativo | Bloqueo `PESSIMISTIC_WRITE` en `findByIdForUpdate` |
| Nombre de categoria unico | `existsByNombreIgnoreCase` |
| No borrar categoria con productos | `countByIdCategoria` (integridad logica) |

### ms-pedidos

| Regla | Implementacion |
|-------|----------------|
| Pedido con al menos un item | `validarItems()` |
| Sin productos repetidos | `Set<Integer>` de IDs vistos |
| Producto existente y activo | Feign a ms-catalogo + `validarProducto()` |
| Stock suficiente | Comparacion contra el stock actual del catalogo |
| Precio congelado | `precioUnitario` se guarda en `detalle_pedidos` |
| Total calculado en servidor | Suma de `precioFinal * cantidad` |
| Transiciones de estado | `Pendiente -> Pagado/Cancelado`, `Pagado -> Enviado/Cancelado`, `Enviado -> Entregado` |
| Cancelar repone stock | `reponerStock()` via Feign |
| Solo borrar Pendiente/Cancelado | `eliminar()` |
| Borrar Pendiente repone stock | `reponerStock()` antes de borrar |
| Pedido huerfano imposible | Limpieza + compensaciones si falla un paso |

## 4. Consistencia sin transacciones ACID globales

`pedidos`, `detalle_pedidos` y `productos` viven en bases distintas (y en
microservicios distintos). No existe un commit atomico entre ellas. El flujo de
creacion aplica **compensaciones**:

1. Valida productos y stock (sin escribir).
2. Guarda la cabecera en `pedidos`.
3. Guarda el detalle en `detalle_pedidos`.
   - Si falla: elimina la cabecera (`limpiarPedido`).
4. Descuenta stock en `ms-catalogo`.
   - Si falla en la linea N: repone el stock de las lineas 1..N-1
     (`compensarStock`) y elimina cabecera + detalle.

Al cancelar o borrar un pedido `Pendiente` se repone el stock. Si la reposicion
falla a mitad de camino, `reponerStock()` revierte las unidades ya repuestas
para no inflar el inventario.

## 5. Comunicacion entre microservicios (Feign)

`ms-pedidos` usa `CatalogoClient` (`@FeignClient(name = "ms-catalogo")`) para:

- `GET /api/v1/catalogo/productos/{id}`: validar producto, precio y stock.
- `PATCH /api/v1/catalogo/productos/{id}/stock`: descontar o reponer stock.

Detalles relevantes:

- `FeignClientInterceptor` propaga el header `Authorization` con el token del
  usuario autenticado.
- Se agrego `io.github.openfeign:feign-hc5` porque el cliente HTTP por defecto
  (`HttpURLConnection`) lanza `ProtocolException: Invalid HTTP method: PATCH`.
  Con Apache HttpClient 5 el `PATCH` funciona y el checkout completa sin error.

## 6. Seguridad
- `common` incluye `JwtAuthenticationFilter`, `JwtTokenProvider`,
  `RestAuthenticationEntryPoint` y `RestAccessDeniedHandler`.
- El filtro valida firma y expiracion, extrae `rol` y crea un
  `UsernamePasswordAuthenticationToken` con autoridad `ROLE_<rol>`.
- El token crudo se guarda como `credentials` para que
  `FeignClientInterceptor` lo propague en llamadas entre microservicios.
- `SecurityConfig` de cada MS define la matriz de roles y desactiva sesiones
  (`STATELESS`) y CSRF.
- Swagger y actuator quedan publicos; el resto exige token.

## 7. Manejo de errores

`GlobalExceptionHandler` (en `common`) traduce excepciones a un unico envelope:

```json
{
  "timestamp": "2026-11-14T21:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Producto con codigo igual a 'HK-PLUSH-001' ya existe...",
  "errors": null,
  "path": "/api/v1/catalogo/productos"
}
```

| Excepcion | HTTP |
|-----------|------|
| `EntityNotFoundException` | 404 |
| `DuplicateResourceException` | 409 |
| `ReferentialIntegrityException` | 409 |
| `BusinessRuleException` | 400 |
| `MethodArgumentNotValidException` | 400 (con `errors`) |
| `AuthenticationException` | 401 |
| `AccessDeniedException` | 403 |
| `FeignException` | mismo status del MS remoto |
| `Exception` | 500 |

## 8. Pruebas unitarias

| Clase | Casos | Cobertura funcional |
|-------|-------|---------------------|
| `ProductoServiceTest` | 7 | codigo duplicado, categoria inexistente, oferta invalida, descuento automatico, stock negativo |
| `CategoriaServiceTest` | 3 | nombre duplicado, borrado con productos, borrado exitoso |
| `PedidoServiceTest` | 16 | items vacios/repetidos, stock insuficiente, producto inactivo, calculo de total, compensacion de stock, transiciones y borrado |

Todas usan Mockito (sin base de datos) y se ejecutan con `mvn test`.

## 9. Limitaciones conocidas

- La blacklist de tokens es en memoria (suficiente para la evaluacion; en
  produccion se usaria Redis).
- La validacion "el pedido pertenece al usuario autenticado" requiere integrar
  la identidad de `ms-usuarios` y queda fuera del alcance actual.
- La UI de Swagger se consume directamente en cada microservicio; el gateway
  expone solo los JSON OpenAPI (`/catalogo/v3/api-docs`, `/pedidos/v3/api-docs`).
