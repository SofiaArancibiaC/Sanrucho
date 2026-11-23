# 🧩 Backend Sanrucho — README de la rama

Documentación técnica del **backend** de Sanrucho: arquitectura de microservicios con Spring Boot 4.1.0, Spring Cloud 2025.1.4, Java 21, PostgreSQL, Eureka, OpenFeign, JWT y Swagger/OpenAPI.

> ℹ️ El README general del repositorio (frontend React, tecnologías y autoras) está en [`../README.md`](../README.md). Este archivo se enfoca 100 % en el backend y en cómo probarlo.

---

## Índice

1. [Qué contiene esta rama](#1-qué-contiene-esta-rama)
2. [Arquitectura: cómo se conectan los archivos](#2-arquitectura-cómo-se-conectan-los-archivos)
3. [Requisitos y arranque](#3-requisitos-y-arranque)
4. [Endpoints REST](#4-endpoints-rest)
5. [Reglas de negocio y estados](#5-reglas-de-negocio-y-estados)
6. [Datos de prueba (seeds)](#6-datos-de-prueba-seeds)
7. [JWT: cómo obtener y usar el token](#7-jwt-cómo-obtener-y-usar-el-token)
8. [Cómo probar en Swagger](#8-cómo-probar-en-swagger)
9. [Cómo probar en Postman](#9-cómo-probar-en-postman)
10. [Build y pruebas automáticas](#10-build-y-pruebas-automáticas)
11. [Solución de problemas](#11-solución-de-problemas)
12. [Notas adicionales](#12-notas-adicionales)

---

## 1. Qué contiene esta rama

| Módulo | Rol | Puerto | Base de datos |
| --- | --- | ---: | --- |
| `common` | Librería compartida: excepciones, `PageResponse`, JWT, interceptor de Feign | — | — |
| `eureka` | Servidor de descubrimiento | 8761 | — |
| `api-gateway` | Enrutamiento + Swagger unificado | 9000 | — |
| `ms-catalogo` | Productos, categorías y stock | 9002 | `catalogo` |
| `ms-pedidos` | Pedidos, detalle y descuento de stock | 9003 | `pedidos` |
| `ms-pagos` | Transacciones y sincronización del pedido | 9004 | `pagos` |

**Importante:** `ms-usuarios` **no forma parte del trabajo de esta rama**. En el repositorio queda solamente el esqueleto original (clase principal + `application.yml`). La implementación de usuarios/JWT se guardó en otro lugar y es responsabilidad de otro integrante. Los microservicios de aquí lo siguen invocando por Feign con el nombre lógico `ms-usuarios`, así que para el flujo completo de checkout necesitas ese servicio corriendo en la red (puerto 9001) o un stub que responda `GET /api/v1/usuarios/{id}`.

### Tecnologías por módulo

- **Spring Boot 4.1.0** / **Spring Cloud 2025.1.4** / **Java 21**.
- Spring Data JPA + PostgreSQL (driver `42.7.3`), H2 para pruebas.
- Netflix Eureka (descubrimiento) + Spring Cloud Gateway (WebFlux).
- OpenFeign con `feign-hc5` en `ms-pedidos` y `ms-pagos` (necesario para soportar `PATCH`).
- MapStruct + Lombok, Bean Validation.
- **springdoc-openapi 3.1.1** (Swagger UI + OpenAPI 3.1).
- Sin Kafka: no hay dependencias, variables ni contenedores de Kafka en esta rama.

---

## 2. Arquitectura: cómo se conectan los archivos

### 2.1 Mapa de módulos

```text
Backend/
├─ pom.xml                     ← POM padre: versiones, BOMs, springdoc 3.1.1
├─ docker-compose.yml          ← Postgres + Eureka + Gateway + 3 MS (sin Kafka)
├─ init-multi-db/              ← SQL de creación y seed (se monta en el contenedor)
├─ common/                     ← librería que heredan TODOS los módulos
├─ eureka/                     ← registro de servicios
├─ api-gateway/                ← rutas /api/v1/** + rutas /ms-*/v3/api-docs/**
├─ ms-catalogo/                ← 9002
├─ ms-pedidos/                 ← 9003  (Feign → catalogo + usuarios)
├─ ms-pagos/                   ← 9004  (Feign → pedidos)
├─ compile.bat / install.bat   ← compilar / reinstalar dependencias
├─ run-all.bat / run-test.bat  ← levantar todo con mvn / con jars
└─ postman/                    ← carpeta de Postman (aún vacía: ver sección 9)
```

### 2.2 Estructura interna de cada microservicio

Todos siguen el mismo patrón (lo aprendimos de `ms-catalogo`):

```text
ms-pedidos/src/main/java/cl/sanrucho/pedidos/
├─ TiendaPedidosApplication.java   ← @SpringBootApplication + @EnableFeignClients + @EnableDiscoveryClient
├─ config/
│  ├─ SecurityConfig.java          ← resource server: valida JWT y define roles por endpoint
│  └─ OpenApiConfig.java           ← título, versión y esquema bearerAuth para Swagger
├─ controller/                     ← endpoints REST (@RestController + @RequestMapping)
├─ dto/                            ← Request / Response (validación con Bean Validation)
├─ mapper/                         ← MapStruct: DTO ↔ entidad
├─ model/
│  ├─ entity/                      ← entidades JPA
│  └─ enums/                       ← estados del dominio
├─ repository/                     ← Spring Data JPA
├─ service/                        ← reglas de negocio (@Transactional)
├─ client/                         ← interfaces Feign + sus DTOs (solo en pedidos y pagos)
└─ resources/application.yml       ← puerto, BD, Eureka, JWT, springdoc
```

**Regla de oro:** el controlador **no** contiene lógica; delega en el service. El service usa repositorios para su BD y clients Feign para datos de otros servicios. Los DTOs nunca exponen la entidad JPA directamente.

### 2.3 Flujo de una petición (ejemplo: crear pedido)

```text
Postman / Swagger / Frontend
        │  Authorization: Bearer <JWT>
        ▼
API Gateway (9000)  ──►  ruta lb://ms-pedidos  ──►  ms-pedidos (9003)
        │
        ├─ JwtAuthenticationFilter (common) valida firma, expiración y rol
        ├─ SecurityConfig autoriza el método HTTP
        ├─ PedidoController recibe PedidoRequest (@Valid)
        ├─ PedidoService:
        │    1. UsuarioClient (Feign) ──► ms-usuarios: GET /api/v1/usuarios/{id}
        │    2. CatalogoClient (Feign) ─► ms-catalogo: GET /api/v1/productos/{id}
        │    3. calcula total con precioFinal/precioOferta/precio
        │    4. guarda pedido en estado Pendiente
        │    5. CatalogoClient (Feign) ─► ms-catalogo: PATCH /api/v1/productos/{id}/stock
        │       {"cantidad": -n}   (si falla, repone stock y borra el pedido)
        │    6. guarda detalle_pedidos
        └─ PedidoResponse + 201 Created con header Location
```

### 2.4 Comunicación entre microservicios (Feign)

| Origen | Cliente (archivo) | Destino | Métodos usados |
| --- | --- | --- | --- |
| `ms-pedidos` | `client/UsuarioClient.java` | `ms-usuarios` | `GET /api/v1/usuarios/{id}` |
| `ms-pedidos` | `client/CatalogoClient.java` | `ms-catalogo` | `GET /api/v1/productos/{id}`, `PATCH /api/v1/productos/{id}/stock` |
| `ms-pagos` | `client/PedidoClient.java` | `ms-pedidos` | `GET /api/v1/pedidos/{id}`, `PATCH /api/v1/pedidos/{id}/estado` |

- No se escribe la URL: `@FeignClient(name = "ms-catalogo")` resuelve la instancia vía **Eureka** (load balancer).
- `common/security/FeignClientInterceptor.java` copia el `Authorization: Bearer <token>` de la petición entrante a cada llamada Feign. Por eso el servicio destino no devuelve 401.
- `common/exception/GlobalExceptionHandler.java` traduce `FeignException` a un `ApiError` con el mismo formato que los errores locales.

### 2.5 Seguridad compartida (`common`)

| Archivo | Función |
| --- | --- |
| `JwtProperties` | lee `jwt.secret` y `jwt.expiration-ms` del `application.yml` |
| `JwtTokenProvider` | genera/valida tokens HS256 con claims `sub` (email), `rol`, `nombre` |
| `JwtAuthenticationFilter` | lee el header, valida el token y arma `ROLE_<rol>` en el `SecurityContext` |
| `FeignClientInterceptor` | propaga el token entre microservicios |
| `GlobalExceptionHandler` | 400/401/403/404/409/500 con formato uniforme |
| `PageResponse<T>` | envoltorio estable de paginación |
| `RestAuthenticationEntryPoint` / `RestAccessDeniedHandler` | respuestas JSON para 401 y 403 |

### 2.6 API Gateway

`api-gateway/src/main/resources/application.yml` define:

- `/api/v1/productos/**` → `lb://ms-catalogo`
- `/api/v1/pedidos/**` → `lb://ms-pedidos`
- `/api/v1/pagos/**` → `lb://ms-pagos`
- `/api/v1/usuarios/**` y `/api/v1/auth/**` → `lb://ms-usuarios`
- `/ms-catalogo/v3/api-docs/**`, `/ms-pedidos/...`, `/ms-pagos/...` → cada servicio con `RewritePath`, usados por el Swagger unificado del gateway.

El gateway **no quita el prefijo**: la ruta `/api/v1/pedidos/1` llega igual al microservicio.

---

## 3. Requisitos y arranque

### 3.1 Requisitos

- JDK 21
- Maven 3.9+ (o el `mvnw` incluido)
- Docker Desktop (para PostgreSQL)
- Postman (opcional) o navegador para Swagger

### 3.2 Compilar

```bash
cd Backend
mvn -B -DskipTests install
```

En Windows también puedes ejecutar `compile.bat`. El reactor compila los 8 módulos en este orden: padre → common → eureka → usuarios → pagos → catalogo → pedidos → gateway.

### 3.3 Levantar infraestructura

**Opción A — Docker Compose (recomendada):**

```bash
cd Backend
mvn -B -DskipTests install      # compila los .jar con el código actual
docker compose up -d --build    #copia los .jar nuevos a las imágenes y levanta todo
```

> ⚠️ Los `Dockerfile` solo hacen `COPY target/*.jar`, **no compilan el código**. Si cambias un `.java`, un `application.yml` o es la primera vez que levantas el proyecto, usa `--build`. Si haces solo `docker compose up -d`, Docker reutiliza imágenes antiguas y los cambios (por ejemplo la configuración CORS del gateway) no se aplican.

Esto levanta PostgreSQL (puerto **5433**), Eureka (8761), Gateway (9000) y los 3 microservicios. Los scripts de `init-multi-db/` crean las 4 bases y cargan los datos de prueba la primera vez.

**Opción B — local sin Docker para los MS:**

```bash
# 1) PostgreSQL en 5433 + bases usuarios, catalogo, pedidos, pagos
# 2) Eureka
mvn -f eureka spring-boot:run

# 3) Microservicios, cada uno en su terminal
mvn -f ms-catalogo spring-boot:run
mvn -f ms-pedidos spring-boot:run
mvn -f ms-pagos spring-boot:run

# 4) Gateway (opcional, para Swagger unificado)
mvn -f api-gateway spring-boot:run
```

En Windows: `run-all.bat` (usa `mvn spring-boot:run`) o `run-test.bat` (usa los `.jar` de `target/` con `--spring.profiles.active=test`).

> Ojo: no existen archivos `application-test.yml`, así que el perfil `test` de `run-test.bat` no cambia la configuración; solo sirve como etiqueta.

### 3.4 Verificación rápida

```bash
curl http://localhost:8761        # panel de Eureka
curl http://localhost:9002/actuator/health
curl http://localhost:9003/actuator/health
curl http://localhost:9004/actuator/health
curl http://localhost:9002/swagger-ui.html
```

---

## 4. Endpoints REST

Todas las rutas llevan prefijo `/api/v1`. Los campos marcados como obligatorios son validados con `@Valid` (si faltan, la API responde **400** con la lista de errores).

### 4.1 ms-catalogo (9002)

| Método | Ruta | Quién puede llamarlo | Descripción |
| --- | --- | --- | --- |
| GET | `/api/v1/productos?nombre=&idCategoria=&page=&size=` | Dueño, Admin, Cliente | Lista paginada con filtros opcionales |
| GET | `/api/v1/productos/{id}` | Dueño, Admin, Cliente | Detalle de producto |
| GET | `/api/v1/productos/codigo/{codigo}` | Dueño, Admin, Cliente | Buscar por código |
| POST | `/api/v1/productos` | Dueño, Admin | Crear producto |
| PUT | `/api/v1/productos/{id}` | Dueño, Admin | Actualizar producto |
| PATCH | `/api/v1/productos/{id}/stock` | cualquier rol autenticado | Ajustar stock (lo usa ms-pedidos) |
| DELETE | `/api/v1/productos/{id}` | Dueño, Admin | Eliminar producto |
| GET | `/api/v1/categorias?nombre=&page=&size=` | Dueño, Admin, Cliente | Lista paginada |
| GET | `/api/v1/categorias/{id}` | Dueño, Admin, Cliente | Detalle |
| POST | `/api/v1/categorias` | Dueño, Admin | Crear categoría |
| PUT | `/api/v1/categorias/{id}` | Dueño, Admin | Actualizar categoría |
| DELETE | `/api/v1/categorias/{id}` | Dueño, Admin | Eliminar categoría |

`POST /api/v1/productos`:

```json
{
  "codigo": "HK-NOTE-102",
  "idCategoria": 5,
  "nombre": "Cuaderno Hello Kitty Edición Limitada",
  "descripcion": "Cuaderno A5 de 80 hojas",
  "precio": 6990,
  "oferta": true,
  "precioOferta": 5490,
  "descuento": 21,
  "stock": 20,
  "stockCritico": 5,
  "imagenUrl": "/static/img/productos/cuaderno-hk2.jpg",
  "estado": "Activo"
}
```

`POST /api/v1/categorias`:

```json
{ "nombre": "Juguetes", "descripcion": "Juguetes Sanrio" }
```

`PATCH /api/v1/productos/1/stock`:

```json
{ "cantidad": -5 }
```

> Cantidad **positiva suma** stock; **negativa descuenta**. No se permite stock negativo.

### 4.2 ms-pedidos (9003)

| Método | Ruta | Quién puede llamarlo | Descripción |
| --- | --- | --- | --- |
| GET | `/api/v1/pedidos?idUsuario=&estado=&page=&size=` | autenticado | Lista paginada con filtros |
| GET | `/api/v1/pedidos/{id}` | autenticado | Detalle con `detalles[]` |
| POST | `/api/v1/pedidos` | Cliente, Admin, Dueño | Crear pedido (descuenta stock) |
| PATCH | `/api/v1/pedidos/{id}/estado` | Admin, Dueño | Cambiar estado |
| DELETE | `/api/v1/pedidos/{id}` | Admin, Dueño | Eliminar y reponer stock |

`POST /api/v1/pedidos`:

```json
{
  "idUsuario": 3,
  "items": [
    { "idProducto": 1, "cantidad": 2 },
    { "idProducto": 5, "cantidad": 1 }
  ]
}
```

- Respuesta **201 Created** con header `Location: /api/v1/pedidos/{id}`.
- Si el mismo producto aparece repetido, se suman las cantidades.
- El total se calcula con `precioFinal` (o `precioOferta` si está en oferta, o `precio`).

`PATCH /api/v1/pedidos/1/estado`:

```json
{ "estado": "Pagado" }
```

Valores: `Pendiente`, `Pagado`, `Enviado`, `Entregado`, `Cancelado`.

### 4.3 ms-pagos (9004)

| Método | Ruta | Quién puede llamarlo | Descripción |
| --- | --- | --- | --- |
| GET | `/api/v1/pagos?idPedido=&estado=&page=&size=` | autenticado | Lista paginada con filtros |
| GET | `/api/v1/pagos/{id}` | autenticado | Detalle de transacción |
| GET | `/api/v1/pagos/pedido/{idPedido}` | autenticado | Todas las transacciones de un pedido |
| POST | `/api/v1/pagos` | Cliente, Admin, Dueño | Iniciar pago (solo pedido `Pendiente`) |
| PATCH | `/api/v1/pagos/{id}/estado` | Admin, Dueño | Aprobar / rechazar / anular |
| DELETE | `/api/v1/pagos/{id}` | Admin, Dueño | Eliminar (solo Pendiente/Rechazado) |

`POST /api/v1/pagos`:

```json
{ "idPedido": 3, "pasarela": "Webpay Plus" }
```

El `monto` **no se envía**: se consulta al pedido vía Feign.

`PATCH /api/v1/pagos/1/estado`:

```json
{ "estado": "Aprobado" }
```

Valores: `Pendiente`, `Aprobado`, `Rechazado`, `Anulado`.

### 4.4 Formatos de respuesta

Paginación (`PageResponse<T>`):

```json
{
  "content": [ /* ... */ ],
  "page": 0,
  "size": 10,
  "totalElements": 8,
  "totalPages": 1,
  "last": true
}
```

Error (`ApiError`):

```json
{
  "timestamp": "2026-11-22T13:40:00",
  "status": 400,
  "error": "Bad Request",
  "message": "La solicitud contiene datos inválidos. Revise el campo 'errors'.",
  "errors": ["'cantidad': La cantidad debe ser mayor que cero"],
  "path": "/api/v1/pedidos"
}
```

---

## 5. Reglas de negocio y estados

### Pedidos

```text
Pendiente ──► Pagado ──► Enviado ──► Entregado (final)
    │            │
    └────────────┴──────► Cancelado (final)
```

- No se permiten saltos (por ejemplo `Pendiente → Enviado` da 400).
- Al **crear** un pedido se valida usuario, producto activo y stock suficiente; luego se descuenta el stock.
- Si falla cualquier línea, se **reponen** las unidades ya descontadas y se elimina el pedido (compensación).
- Al **cancelar** o **eliminar** un pedido se repone el stock (salvo que ya estuviera cancelado).
- No se puede eliminar un pedido `Enviado` ni `Entregado`.

### Pagos

```text
Pendiente ──► Aprobado ──► Anulado (final)
    │
    └────────► Rechazado (final)
```

- Solo se puede iniciar pago si el pedido está `Pendiente` y no tiene ya una transacción `Aprobada`.
- Al **aprobar**: se genera `codigoAutorizacion` tipo `AUTH-XXXXXXXXXX` y se llama a `PATCH /api/v1/pedidos/{id}/estado` con `Pagado`.
- Al **anular**: se llama al mismo endpoint con `Cancelado`.
- Si ms-pedidos rechaza el cambio, la transacción local hace rollback.
- No se puede eliminar una transacción `Aprobada` ni `Anulada`.

---

## 6. Datos de prueba (seeds)

Los datos los cargan los scripts `init-multi-db/0*.sql` al iniciar PostgreSQL por primera vez con Docker Compose.

**Usuarios** (`usuarios`, contraseñas en texto para referencia; en BD están con BCrypt):

| id | Email | Contraseña | Rol |
| ---: | --- | --- | --- |
| 1 | `admin@gmail.com` | `admin123` | Admin |
| 2 | `dueno@gmail.com` | `dueno123` | Dueño |
| 3 | `camila@gmail.com` | `123456` | Cliente |
| 4 | `vale.soto@gmail.com` | `123456` | Cliente |

**Categorías** (ids 1 a 7): Maquillaje, Peluches, Llaveros, Blind-box, Papelería, Tecnología, Hogar.

**Productos** (ids 1 a 8): por ejemplo `1 = Peluche Hello Kitty Clásico 30cm` (precio 24990, oferta 19990, stock 15), `2 = Peluche Kuromi` (stock 20), `5 = Cuaderno Hello Kitty` (stock 10).

**Pedidos:**

| id | idUsuario | Estado | Total | Detalle |
| ---: | ---: | --- | ---: | --- |
| 1 | 3 | Pagado | 25980 | producto 1 (19990) + producto 5 (5990) |
| 2 | 4 | Enviado | 13990 | producto 7 |
| 3 | 3 | Pendiente | 17990 | producto 2 |

**Transacciones:**

| id | idPedido | Pasarela | Estado |
| ---: | ---: | --- | --- |
| 1 | 1 | Webpay Plus | Aprobado |
| 2 | 2 | MercadoPago | Aprobado |
| 3 | 3 | Webpay Plus | Rechazado |
| 4 | 3 | Webpay Plus | Pendiente |

👉 El **pedido 3** es ideal para probar el flujo de pago: está `Pendiente` y no tiene transacción aprobada.

> Las relaciones entre bases (`id_usuario`, `id_producto`, `id_pedido`) son **referencias lógicas**, sin Foreign Key física, porque cada microservicio es dueño de su BD.

---

## 7. JWT: cómo obtener y usar el token

Los 3 microservicios son **resource servers**: validan tokens, pero no los emiten. El emisor es `ms-usuarios` (`POST /api/v1/auth/login`), que en esta rama no está implementado (ver sección 1).

### Forma recomendada

Levanta tu copia de `ms-usuarios` en el puerto 9001 y pide un token:

```bash
curl -X POST http://localhost:9001/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"camila@gmail.com","password":"123456"}'
```

Respuesta esperada: `{ "token": "eyJhbGciOiJIUzI1NiJ9..." }` (o el campo que use tu implementación).

### Alternativa rápida para testing (jwt.io)

1. Entra a <https://jwt.io> → selecciona algoritmo **HS256**.
2. En *VERIFY SIGNATURE* pega el secreto Base64 que está en `application.yml` de cada MS (`jwt.secret`).
3. En *PAYLOAD* usa:

```json
{
  "sub": "camila@gmail.com",
  "rol": "Cliente",
  "nombre": "Camila Soto Perez",
  "iat": 1763800000,
  "exp": 1999999999
}
```

4. Copia el token generado y úsalo como `Authorization: Bearer <token>`.

> Roles válidos: `Dueño`, `Admin`, `Cliente`. El filtro convierte el claim en authority `ROLE_<rol>`. El token dura **1 hora** (`expiration-ms: 3600000`). El secreto es solo de desarrollo: cámbialo en producción.

---

## 8. Cómo probar en Swagger

### Swagger por microservicio (no necesita Eureka)

| Servicio | URL |
| --- | --- |
| Catálogo | <http://localhost:9002/swagger-ui.html> |
| Pedidos | <http://localhost:9003/swagger-ui.html> |
| Pagos | <http://localhost:9004/swagger-ui.html> |

El JSON crudo está en `http://localhost:<puerto>/v3/api-docs` (y `.yaml`).

**Pasos:**

1. Abre la URL del servicio.
2. Haz clic en el botón **Authorize** 🔒.
3. Pega **solo el token**, sin la palabra `Bearer` (Swagger la agrega automáticamente).
4. Prueba primero un `GET` (por ejemplo `GET /api/v1/productos`) para confirmar que responde 200.
5. Luego usa `POST` / `PATCH` con los JSON de la sección 4. Si el botón **Try it out** está deshabilitado, revisa el token.

Los endpoints aparecen agrupados por `@Tag` (`Productos`, `Categorías`, `Pedidos`, `Pagos`) y ordenados por método HTTP.

### Swagger unificado en el gateway (necesita Eureka)

<http://localhost:9000/swagger-ui.html>

Arriba a la derecha aparece un desplegable con **ms-catalogo**, **ms-pedidos** y **ms-pagos**. Al elegir uno se carga el `/v3/api-docs` de ese servicio a través de las rutas `RewritePath` del gateway.

> Para que el desplegable cargue, los microservicios deben estar registrados en Eureka (panel en <http://localhost:8761>).

---

## 9. Cómo probar en Postman

La carpeta `Backend/postman/` existe pero **todavía no contiene una colección** (solo la estructura vacía). Puedes crear una en 5 minutos:

### 9.1 Configurar el entorno

1. Postman → **Environments** → **New** → nombre `Sanrucho-Local`.
2. Agrega variables:

| Variable | Initial value |
| --- | --- |
| `baseCatalogo` | `http://localhost:9002` |
| `basePedidos` | `http://localhost:9003` |
| `basePagos` | `http://localhost:9004` |
| `baseGateway` | `http://localhost:9000` |
| `token` | `<pega aquí el JWT>` |
| `pedidoId` | `3` |
| `pagoId` | `4` |

3. Selecciona ese entorno arriba a la derecha.

### 9.2 Authorization

En la pestaña **Authorization** de la colección (o de cada request):

- Type: **Bearer Token**
- Token: `{{token}}`

Así todas las peticiones envían el header `Authorization: Bearer ...`. No hace falta escribirlo a mano en Headers.

### 9.3 Flujo recomendado (checkout completo)

Ejecuta estos requests en orden:

| # | Método | URL | Body |
| ---: | --- | --- | --- |
| 1 | GET | `{{baseCatalogo}}/api/v1/productos/1` | — (ver stock disponible) |
| 2 | POST | `{{basePedidos}}/api/v1/pedidos` | `{"idUsuario":3,"items":[{"idProducto":1,"cantidad":2}]}` |
| 3 | GET | `{{basePedidos}}/api/v1/pedidos/1` | — (o el id devuelto en el paso 2) |
| 4 | POST | `{{basePagos}}/api/v1/pagos` | `{"idPedido":3,"pasarela":"Webpay Plus"}` |
| 5 | PATCH | `{{basePagos}}/api/v1/pagos/4/estado` | `{"estado":"Aprobado"}` |
| 6 | GET | `{{basePedidos}}/api/v1/pedidos/3` | — (debe estar `Pagado`) |
| 7 | PATCH | `{{basePedidos}}/api/v1/pedidos/3/estado` | `{"estado":"Enviado"}` |
| 8 | PATCH | `{{basePedidos}}/api/v1/pedidos/3/estado` | `{"estado":"Entregado"}` |

Todos los bodies van con header `Content-Type: application/json`.

### 9.4 Guardar IDs automáticamente (tip)

En la pestaña **Tests** del request `POST /api/v1/pedidos`, agrega:

```javascript
const data = pm.response.json();
pm.environment.set("pedidoId", data.id);
```

Y en `POST /api/v1/pagos`:

```javascript
const data = pm.response.json();
pm.environment.set("pagoId", data.id);
```

Así los siguientes requests usan `{{pedidoId}}` y `{{pagoId}}` sin editar manualmente.

### 9.5 Probar vía gateway

Cambia `{{baseCatalogo}}` por `{{baseGateway}}` y llama a `{{baseGateway}}/api/v1/productos/1`. La ruta es idéntica; la diferencia es que pasa por el gateway.

---

## 10. Build y pruebas automáticas

```bash
cd Backend
mvn -B -DskipTests install                 # compila e instala los 8 módulos
mvn test                                   # todas las pruebas
mvn -pl ms-catalogo test -Dtest=OpenApiDocumentationTest
```

Estado verificado en esta rama:

| Prueba | Resultado |
| --- | --- |
| `mvn -B -DskipTests install` (reactor completo) | ✅ BUILD SUCCESS (8/8) |
| `Tienda*ApplicationTests` de catalogo, pedidos y pagos (con H2, sin Eureka) | ✅ context load |
| `OpenApiDocumentationTest` de los 3 MS (`/v3/api-docs` → 200 sin token) | ✅ 3/3 |
| Swagger del gateway por HTTP (`/swagger-ui.html` y `/v3/api-docs`) | ✅ 200 |
| Rutas `/ms-*/v3/api-docs/**` del gateway | ✅ hacen match |
| `docker compose config --quiet` | ✅ configuración válida |

Comando usado para las pruebas de contexto con H2:

```bash
mvn -B -pl ms-pedidos test \
  -Dspring.datasource.url=jdbc:h2:mem:pedidos \
  -Dspring.datasource.driver-class-name=org.h2.Driver \
  -Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect \
  -Dspring.jpa.hibernate.ddl-auto=create-drop \
  -Deureka.client.enabled=false
```

> Nota: Spring Boot 4 movió `@AutoConfigureMockMvc` al módulo `spring-boot-webmvc-test`, por eso los POMs incluyen esa dependencia en scope `test`.

---

## 11. Solución de problemas

| Síntoma | Causa probable | Solución |
| --- | --- | --- |
| **401 No autenticado** | Falta token, expiró o no lleva `Bearer ` | Genera uno nuevo y usa Authorization → Bearer Token |
| **403 Acceso denegado** | El rol no tiene permiso (ej. Cliente creando producto) | Usa token de Admin/Dueño |
| **400 "Transición de estado no permitida"** | Salto inválido en la máquina de estados | Sigue las flechas de la sección 5 |
| **400 "Stock insuficiente"** | El producto no tiene unidades | Aumenta stock con `PATCH /productos/{id}/stock` |
| **503 al crear pedido / pagar** | Eureka no tiene registrado el servicio destino | Revisa <http://localhost:8761> y los logs del MS llamado |
| **404 en el gateway** | Ruta mal escrita o MS no registrado | Verifica el prefijo `/api/v1/...` y el registro en Eureka |
| **Swagger en blanco o 403** | URL sin el puerto correcto o token inválido | Usa el puerto 9002/9003/9004 y haz Authorize de nuevo |
| **No conecta a PostgreSQL** | Docker apagado o puerto distinto | `docker compose up -d`; la BD está en **5433** (no 5432) |
| **Error al hacer PATCH desde Feign** | Falta el cliente HC5 | Confirmar `io.github.openfeign:feign-hc5` en el POM (ya está) |
| **Los datos seed no aparecen** | Los scripts SQL solo corren cuando se crea el contenedor | `docker compose down && docker compose up -d` (¡borra los datos!) |

---

## 12. Notas adicionales

- **No hay Kafka** en esta rama: se eliminaron los contenedores, `depends_on` y variables `SPRING_KAFKA_*`. La comunicación es sincrónica vía Feign.
- **`ddl-auto: update`**: Hibernate crea/actualiza tablas automáticamente; los SQL de `init-multi-db` además cargan datos de prueba.
- **Swagger en producción**: si no quieres exponerlo, agrega `springdoc.api-docs.enabled=false` y `springdoc.swagger-ui.enabled=false`.
- **Scripts Windows**: `compile.bat` e `install.bat` usan `%~dp0` (ruta del script), así que funcionan desde cualquier carpeta. `install.bat` borra `~/.m2` (descarga todo de nuevo).
- **H2 en tests**: los tests de contexto sobrescriben la URL JDBC con H2 en memoria para no depender de PostgreSQL.
- **`target/` está versionado** en este repo: después de compilar verás cambios en esas carpetas; son artefactos generados, no código fuente.
- **Frontend**: todavía usa `localStorage`; la integración con estos endpoints es un paso futuro (ver README raíz).

---

¿Dudas con algún flujo? Lo más rápido para validar el sistema completo es: **login → POST pedido → POST pago → PATCH pago Aprobado → GET pedido**. Si ese camino funciona, Feign, Eureka, JWT y las tres bases están conectados correctamente. 🩵
