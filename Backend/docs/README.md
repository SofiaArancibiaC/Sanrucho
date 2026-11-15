# Tienda Sanrucho - Backend (U3)

Backend de la tienda Sanrucho implementado con **microservicios Spring Boot**.
Incluye base de datos PostgreSQL real, reglas de negocio, API REST documentada
con Swagger/OpenAPI y autenticacion JWT con roles.

> Estado: compila, pasa las pruebas unitarias y corre contra PostgreSQL.
> El frontend, la emision de tokens y la gestion de sesiones quedan fuera de
> este alcance (los dos microservicios solo **validan** el JWT).

---

## 1. Modulos

| Modulo        | Tipo                | Puerto | Descripcion                                                        |
|---------------|---------------------|--------|--------------------------------------------------------------------|
| `common`      | Libreria compartida | -      | Seguridad JWT, manejo global de errores, OpenAPI y `PageResponse`. |
| `eureka`      | Servidor Eureka     | 8761   | Registro y descubrimiento de servicios.                            |
| `ms-catalogo` | Microservicio       | 9002   | Categorias y productos (2 bases de datos).                         |
| `ms-pedidos`  | Microservicio       | 9003   | Pedidos y detalle (2 bases de datos) + cliente Feign a catalogo.   |
| `api-gateway` | API Gateway         | 9000   | Punto de entrada unico, rutas y CORS.                              |

Flujo de una compra:

```
Frontend
   |  (HTTP + JWT)
   v
api-gateway :9000
   |-- /api/v1/catalogo/**  --> ms-catalogo :9002  --> BD productos / BD categorias
   |-- /api/v1/pedidos/**   --> ms-pedidos  :9003  --> BD pedidos / BD detalle_pedidos
                                   |
                                   | Feign (JWT propagado)
                                   v
                              ms-catalogo (valida y descuenta stock)
```

Eureka en `:8761` permite que el gateway y Feign resuelvan los servicios por
nombre (`lb://ms-catalogo`, `lb://ms-pedidos`) sin IPs fijas.

---

## 2. Requisitos

- Java 21
- Maven 3.9+
- Docker Desktop (para PostgreSQL en el puerto **5433**)
- PowerShell (para generar tokens de prueba)

---

## 3. Puesta en marcha

### 3.1 Base de datos

Desde `Backend/init-multi-db`:

```bat
docker_install_biblio.bat
docker_compile_dbs.bat
```

El primer script crea el contenedor `postgres-db` (usuario `postgres`,
password `123`, puerto local `5433`). El segundo crea una base por tabla y
carga los datos de prueba:

| Base de datos    | Tabla             | Usada por                  |
|------------------|-------------------|----------------------------|
| `categorias`     | `categorias`      | ms-catalogo (secundaria)   |
| `productos`      | `productos`       | ms-catalogo (primaria)     |
| `pedidos`        | `pedidos`         | ms-pedidos (primaria)      |
| `detalle_pedidos`| `detalle_pedidos` | ms-pedidos (secundaria)    |
| `usuarios`       | `usuarios`        | futura autenticacion       |
| `transacciones`  | `transacciones`   | futuro microservicio pagos |

> Ver `ROMPIMIENTO_FK.md` para entender por que **no existen claves foraneas
> fisicas** entre estas bases de datos.

### 3.2 Compilar y probar

```bat
mvn -B clean test
mvn -B -DskipTests package
```

Resultado esperado de las pruebas: **27 tests, 0 fallos** (Eureka 1,
ms-catalogo 10, ms-pedidos 16).

### 3.3 Levantar los servicios

En terminals separadas, en este orden:

```bat
java -jar eureka\target\cl-sanrucho-eureka-1.0-SNAPSHOT.jar
java -jar ms-catalogo\target\cl-sanrucho-catalogo-0.0.1-SNAPSHOT.jar
java -jar ms-pedidos\target\cl-sanrucho-pedidos-0.0.1-SNAPSHOT.jar
java -jar api-gateway\target\cl-sanrucho-gateway-0.0.1-SNAPSHOT.jar
```

Tambien existen los scripts `run-eureka.bat`, `run-catalogo.bat`,
`run-pedidos.bat`, `run-gateway.bat`, `run-all.bat` (stack completo) y
`run-test.bat`. Los scripts suponen que ya se ejecuto
`mvn -B -DskipTests package`.

Verificacion rapida:

- Eureka: http://localhost:8761
- Salud catalogo: http://localhost:9002/actuator/health
- Salud pedidos: http://localhost:9003/actuator/health
- Salud gateway: http://localhost:9000/actuator/health

### 3.4 Prueba end-to-end automatizada

Con el stack arriba y la base cargada:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\smoke-test.ps1
```

El script genera tokens (`Admin` y `Cliente`), recorre seguridad, CRUD de
catalogo, creacion de pedido con descuento de stock via Feign, transiciones de
estado, reposicion de stock y borrado, y valida los OpenAPI expuestos por el
gateway. Resultado esperado: **20/20 pruebas OK**.

---

## 4. Swagger / OpenAPI

| Servicio    | Swagger UI                                  | OpenAPI JSON                                  |
|-------------|---------------------------------------------|-----------------------------------------------|
| ms-catalogo | http://localhost:9002/swagger-ui.html       | http://localhost:9002/v3/api-docs             |
| ms-pedidos  | http://localhost:9003/swagger-ui.html       | http://localhost:9003/v3/api-docs             |
| Via gateway | (usar las URLs directas para la UI)         | http://localhost:9000/catalogo/v3/api-docs    |
| Via gateway |                                             | http://localhost:9000/pedidos/v3/api-docs     |

En Swagger UI: boton **Authorize** -> pegar el token **sin** la palabra
`Bearer`.

---

## 5. Autenticacion JWT

Los microservicios son **resource servers**: no emiten tokens, solo validan
firma HS256, expiracion y el claim `rol`.

Generar un token de prueba:

```powershell
cd scripts
powershell -ExecutionPolicy Bypass -File .\generar-token.ps1 -Email admin@gmail.com -Rol Admin
```

Roles soportados: `Dueño`, `Admin`, `Cliente`.

Matriz de permisos:

| Endpoint                                             | Metodo | Dueño | Admin | Cliente | Sin token |
|------------------------------------------------------|--------|:-----:|:-----:|:-------:|:---------:|
| `/api/v1/catalogo/**` (lectura)                      | GET    |  Si   |  Si   |   Si    | 401       |
| `/api/v1/catalogo/productos/{id}/stock`              | PATCH  |  Si   |  Si   |   Si    | 401       |
| `/api/v1/catalogo/**` (crear/actualizar/eliminar)    | POST/PUT/DELETE | Si | Si | No (403) | 401 |
| `/api/v1/pedidos/**` (leer/crear/cambiar estado)     | GET/POST/PATCH | Si | Si | Si  | 401       |
| `/api/v1/pedidos/**` (eliminar)                      | DELETE |  Si   |  Si   | No (403)| 401       |
| `/swagger-ui/**`, `/v3/api-docs/**`, `/actuator/**`  | ALL    |  Publico | Publico | Publico | Publico |

Respuestas de seguridad en formato `ApiError`:

- **401** token ausente, invalido o expirado.
- **403** token valido pero el rol no permite la operacion.

> El token se propaga automaticamente de `ms-pedidos` a `ms-catalogo` mediante
> `FeignClientInterceptor`, por lo que el descuento de stock usa la identidad
> del usuario que hizo la compra.

---

## 6. Documentacion adicional

- `ARQUITECTURA.md`: decisiones de diseno, doble datasource, reglas de negocio.
- `PRUEBAS_API.md`: ejemplos `curl`/PowerShell y matriz de pruebas funcionales.
- `ROMPIMIENTO_FK.md`: explicacion detallada de por que se rompen las FK al
  usar una base de datos por tabla.
