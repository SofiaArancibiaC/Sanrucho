# Documentación de APIs — Sanrucho

**Fecha:** 24 de noviembre de 2026
**Versión del API:** `/api/v1`
**Arquitectura:** microservicios Java (Spring Boot 4.1.0) + Spring Cloud Gateway + Eureka

---

## 1. URLs base

| Componente | Puerto | URL |
|---|---:|---|
| API Gateway (punto de entrada del frontend) | 9000 | `http://localhost:9000` |
| Eureka (descubrimiento) | 8761 | `http://localhost:8761` |
| Swagger unificado | 9000 | `http://localhost:9000/swagger-ui.html` |
| ms-usuarios | 9001 | `http://localhost:9001` (interno) |
| ms-catalogo | 9002 | `http://localhost:9002` (interno) |
| ms-pedidos | 9003 | `http://localhost:9003` (interno) |
| ms-pagos | 9004 | `http://localhost:9004` (interno) |
| PostgreSQL | 5433 | contenedor `postgres-rdbms` |

El frontend usa `VITE_API_URL=http://localhost:9000` (archivo `.env`), inyectado en tiempo de compilación como `__API_URL__` por `vite.config.ts`.

---

## 2. Autenticación

Todas las peticiones (excepto login, registro, categorías y productos en lectura) requieren:

```http
Authorization: Bearer <token JWT>
```

**Login**

```http
POST /api/v1/auth/login
Content-Type: application/json

{ "email": "camila@gmail.com", "password": "camila123" }
```

**Respuesta**

```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "usuario": {
    "idUsuario": 3,
    "run": "33333333-3",
    "nombre": "Camila",
    "apellidos": "Soto Perez",
    "email": "camila@gmail.com",
    "rol": "CLIENTE",
    "estado": "Activo"
  }
}
```

El JWT contiene los claims `sub` (email), `rol` (`DUENO` / `ADMIN` / `CLIENTE`) e `idUsuario`. Vigencia: 24 h.

**Usuarios de prueba**

| Rol | Correo | Contraseña |
|---|---|---|
| Dueño | `dueno@gmail.com` | `dueno123` |
| Admin | `admin@gmail.com` | `admin123` |
| Cliente | `camila@gmail.com` | `camila123` |

---

## 3. Convenciones

### Respuesta paginada

```json
{
  "content": [ ... ],
  "page": 0,
  "size": 10,
  "totalElements": 3,
  "totalPages": 1,
  "last": true
}
```

Parámetros comunes: `page`, `size`.

### Formato de error

```json
{
  "timestamp": "2026-11-24T01:00:00.000Z",
  "status": 403,
  "error": "Forbidden",
  "message": "No tienes permisos para realizar esta acción.",
  "errors": null,
  "path": "/api/v1/usuarios/1"
}
```

### Roles

| Rol | Permisos |
|---|---|
| `DUENO` | Lectura y escritura completa (productos, categorías, usuarios, pedidos, pagos) |
| `ADMIN` | Lectura de usuarios y boletas; administración de productos, categorías, pedidos y pagos |
| `CLIENTE` | Catálogo, creación de pedidos/pagos y consulta de sus propios pedidos |

---

## 4. Endpoints por microservicio

### 4.1 Autenticación y usuarios — `ms-usuarios`

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| POST | `/api/v1/auth/login` | Iniciar sesión | Público |
| POST | `/api/v1/auth/register` | Registrar usuario | Público |
| GET | `/api/v1/usuarios` | Listar usuarios | Autenticado |
| GET | `/api/v1/usuarios/{id}` | Obtener por id | Autenticado |
| GET | `/api/v1/usuarios/run/{run}` | Obtener por RUN | Autenticado |
| POST | `/api/v1/usuarios` | Crear usuario | Dueño |
| PUT | `/api/v1/usuarios/{id}` | Editar usuario (contraseña vacía = no cambiarla) | Dueño |
| DELETE | `/api/v1/usuarios/{id}` | Eliminar usuario | Dueño |
| DELETE | `/api/v1/usuarios/run/{run}` | Eliminar por RUN | Dueño |

**`UsuarioResponse`**

```json
{
  "idUsuario": 3,
  "run": "33333333-3",
  "nombre": "Camila",
  "apellidos": "Soto Perez",
  "email": "camila@gmail.com",
  "fechaNacimiento": "2000-03-15",
  "regionId": 1,
  "comunaId": 1,
  "direccion": "Direccion camila",
  "telefono": "934567890",
  "estado": "Activo",
  "rol": "CLIENTE"
}
```

### 4.2 Productos y categorías — `ms-catalogo`

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| GET | `/api/v1/productos` | Listar (filtros `nombre`, `idCategoria`, paginación) | Público |
| GET | `/api/v1/productos/{id}` | Obtener por id | Público |
| GET | `/api/v1/productos/codigo/{codigo}` | Obtener por código | Público |
| POST | `/api/v1/productos` | Crear producto | Dueño / Admin |
| PUT | `/api/v1/productos/{id}` | Editar producto | Dueño / Admin |
| DELETE | `/api/v1/productos/{id}` | Eliminar producto | Dueño / Admin |
| PATCH | `/api/v1/productos/{id}/stock` | Ajustar stock (uso interno de ms-pedidos) | Autenticado |
| GET | `/api/v1/categorias` | Listar categorías | Público |
| GET | `/api/v1/categorias/{id}` | Obtener categoría | Público |
| POST | `/api/v1/categorias` | Crear categoría | Dueño / Admin |
| PUT | `/api/v1/categorias/{id}` | Editar categoría | Dueño / Admin |
| DELETE | `/api/v1/categorias/{id}` | Eliminar categoría | Dueño / Admin |

**`ProductoResponse` (campos principales)**

```json
{
  "id": 2,
  "codigo": "KR-PLUSH-002",
  "idCategoria": 2,
  "nombreCategoria": "Peluches",
  "nombre": "Peluche Kuromi con Capucha 25cm",
  "precio": 22990,
  "oferta": true,
  "precioOferta": 17990,
  "precioFinal": 17990,
  "stock": 24,
  "stockCritico": 5,
  "imagenUrl": "productos/kuromi.webp",
  "estado": "Activo"
}
```

### 4.3 Pedidos — `ms-pedidos`

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| GET | `/api/v1/pedidos` | Listar (filtros `idUsuario`, `estado`, paginación) | Autenticado |
| GET | `/api/v1/pedidos/{id}` | Detalle con `detalles[]` | Autenticado |
| POST | `/api/v1/pedidos` | Crear pedido (descuenta stock) | Cliente / Admin / Dueño |
| PATCH | `/api/v1/pedidos/{id}/estado` | Cambiar estado | Admin / Dueño |
| DELETE | `/api/v1/pedidos/{id}` | Eliminar (reponer stock) | Admin / Dueño |

**Crear pedido**

```json
{ "idUsuario": 3, "items": [{ "idProducto": 2, "cantidad": 1 }] }
```

**`PedidoResponse`**

```json
{
  "id": 1,
  "idUsuario": 3,
  "fechaCompra": "2026-11-10T14:30:00",
  "total": 25980,
  "estado": "Pagado",
  "detalles": [
    { "idDetalle": 1, "idProducto": 1, "nombreProducto": "Peluche Hello Kitty", "cantidad": 1, "precioUnitario": 19990, "subtotal": 19990 }
  ]
}
```

Estados posibles: `Pendiente`, `Pagado`, `Enviado`, `Entregado`, `Cancelado`.

### 4.4 Pagos — `ms-pagos`

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| GET | `/api/v1/pagos` | Listar pagos | Autenticado |
| GET | `/api/v1/pagos/{id}` | Obtener pago | Autenticado |
| GET | `/api/v1/pagos/pedido/{idPedido}` | Pagos de un pedido | Autenticado |
| POST | `/api/v1/pagos` | Registrar pago | Cliente / Admin / Dueño |
| PATCH | `/api/v1/pagos/{id}/estado` | Cambiar estado | Admin / Dueño |
| DELETE | `/api/v1/pagos/{id}` | Eliminar pago | Admin / Dueño |

---

## 5. Ejemplos con curl

```bash
# Login
curl -s -X POST http://localhost:9000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"camila@gmail.com\",\"password\":\"camila123\"}"

# Listar pedidos propios (usar el token devuelto)
curl -s http://localhost:9000/api/v1/pedidos?idUsuario=3 \
  -H "Authorization: Bearer <TOKEN>"

# Crear pedido
curl -s -X POST http://localhost:9000/api/v1/pedidos \
  -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" \
  -d "{\"idUsuario\":3,\"items\":[{\"idProducto\":2,\"cantidad\":1}]}"
```

---

## 6. Capa de consumo en el frontend

| Archivo | Responsabilidad |
|---|---|
| `src/api/client.ts` | Instancia axios, `baseURL`, JWT interceptor y manejo de 401 |
| `src/api/usuarios.ts` | Login, registro y CRUD de usuarios |
| `src/api/productos.ts` | Catálogo de productos |
| `src/api/categorias.ts` | Categorías |
| `src/api/pedidos.ts` | Pedidos + adaptadores al tipo `Pedido` de la UI |
| `src/api/pagos.ts` | Pagos |

La correspondencia detallada entre DTOs del backend y tipos del frontend se documenta en `DOCUMENTO_APIS_E_INTEGRACION.md`.
