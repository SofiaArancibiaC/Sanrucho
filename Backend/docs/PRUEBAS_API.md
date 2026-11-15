# Guia de pruebas de la API

Todos los ejemplos usan el **API Gateway** (`http://localhost:9000`). Tambien
se puede llamar directamente a cada microservicio (`9002` / `9003`).

## 1. Obtener un token

```powershell
cd scripts
$TOKEN = powershell -ExecutionPolicy Bypass -File .\generar-token.ps1 `
    -Email admin@gmail.com -Rol Admin | Select-Object -Last 1
```

Roles disponibles para probar: `Admin`, `Dueño`, `Cliente`.

En `curl` (CMD o Git Bash):

```bat
curl -H "Authorization: Bearer %TOKEN%" http://localhost:9000/api/v1/catalogo/productos
```

En PowerShell:

```powershell
$headers = @{ Authorization = "Bearer $TOKEN" }
Invoke-RestMethod -Uri http://localhost:9000/api/v1/catalogo/productos -Headers $headers
```

> El endpoint de generacion de tokens es solo una **herramienta de prueba**.
> En la version final el login lo emitira el servicio de autenticacion.

---

## 2. Catalogo (ms-catalogo, :9002)

### 2.1 Categorias

| Metodo | Ruta | Rol |
|--------|------|-----|
| GET | `/api/v1/catalogo/categorias?nombre=&page=0&size=10&sort=id,asc` | Cualquier autenticado |
| GET | `/api/v1/catalogo/categorias/{id}` | Cualquier autenticado |
| POST | `/api/v1/catalogo/categorias` | Dueno / Admin |
| PUT | `/api/v1/catalogo/categorias/{id}` | Dueno / Admin |
| DELETE | `/api/v1/catalogo/categorias/{id}` | Dueno / Admin |

Crear:

```json
{
  "nombre": "Accesorios",
  "descripcion": "Accesorios varios de Sanrio."
}
```

Respuesta `201 Created` + header `Location`.

Eliminar una categoria con productos devuelve `409 Conflict`:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "No se puede eliminar el registro de Categoria con ID '2', porque tiene registros asociados en: 5 producto(s) de la base de datos productos..."
}
```

### 2.2 Productos

| Metodo | Ruta | Rol |
|--------|------|-----|
| GET | `/api/v1/catalogo/productos?nombre=&idCategoria=&page=0&size=10` | Cualquier autenticado |
| GET | `/api/v1/catalogo/productos/{id}` | Cualquier autenticado |
| GET | `/api/v1/catalogo/productos/codigo/{codigo}` | Cualquier autenticado |
| POST | `/api/v1/catalogo/productos` | Dueno / Admin |
| PUT | `/api/v1/catalogo/productos/{id}` | Dueno / Admin |
| DELETE | `/api/v1/catalogo/productos/{id}` | Dueno / Admin |
| PATCH | `/api/v1/catalogo/productos/{id}/stock` | Cualquier autenticado (lo usa ms-pedidos) |

Crear:

```json
{
  "codigo": "HK-ACC-900",
  "idCategoria": 2,
  "nombre": "Peluche Hello Kitty Edicion Especial",
  "descripcion": "Peluche de edicion limitada.",
  "precio": 29990,
  "oferta": true,
  "precioOferta": 24990,
  "stock": 10,
  "stockCritico": 2,
  "imagenUrl": "/static/img/productos/hello-kitty1.webp",
  "estado": "Activo"
}
```

El servidor calcula `precioFinal` y `descuento` automaticamente.

Ajustar stock (positivo repone, negativo descuenta):

```json
{ "cantidad": -2 }
```

Reglas que devuelven `400 Bad Request`:

- codigo duplicado (`409`),
- categoria inexistente (`404`),
- `oferta=true` sin `precioOferta` o con `precioOferta >= precio`,
- stock resultante negativo.

---

## 3. Pedidos (ms-pedidos, :9003)

| Metodo | Ruta | Rol |
|--------|------|-----|
| GET | `/api/v1/pedidos?idUsuario=&estado=&page=0&size=10` | Cualquier autenticado |
| GET | `/api/v1/pedidos/{id}` | Cualquier autenticado |
| POST | `/api/v1/pedidos` | Cualquier autenticado |
| PATCH | `/api/v1/pedidos/{id}/estado` | Cualquier autenticado |
| DELETE | `/api/v1/pedidos/{id}` | Dueno / Admin |

Crear pedido:

```json
{
  "idUsuario": 3,
  "items": [
    { "idProducto": 1, "cantidad": 2 },
    { "idProducto": 5, "cantidad": 1 }
  ]
}
```

Respuesta `201 Created`:

```json
{
  "id": 4,
  "idUsuario": 3,
  "fechaCompra": "2026-11-14T21:50:00",
  "total": 45970,
  "estado": "Pendiente",
  "detalles": [
    {
      "idDetalle": 5,
      "idProducto": 1,
      "nombreProducto": "Peluche Hello Kitty Clasico 30cm",
      "cantidad": 2,
      "precioUnitario": 19990,
      "subtotal": 39980
    },
    {
      "idDetalle": 6,
      "idProducto": 5,
      "nombreProducto": "Cuaderno Hello Kitty A5 Rayas",
      "cantidad": 1,
      "precioUnitario": 5990,
      "subtotal": 5990
    }
  ]
}
```

Efectos verificables:

- el producto 1 baja de 15 a 13 unidades y el 5 de 10 a 9 en la BD `productos`;
- el detalle queda en la BD `detalle_pedidos`;
- la cabecera queda en la BD `pedidos` con estado `Pendiente`.

Cambiar estado:

```json
{ "estado": "Pagado" }
```

Transiciones permitidas:

```
Pendiente -> Pagado | Cancelado
Pagado    -> Enviado | Cancelado
Enviado   -> Entregado
Entregado -> (final)
Cancelado -> (final)
```

Al pasar a `Cancelado` se repone el stock. Una transicion invalida devuelve
`400 Bad Request`.

---

## 4. Matriz de pruebas funcionales sugerida

| # | Escenario | Herramienta | Resultado esperado |
|---|-----------|-------------|--------------------|
| 1 | GET catalogo sin token | curl/Swagger | 401 ApiError |
| 2 | GET catalogo con token Cliente | Swagger Authorize | 200 con `content` paginado |
| 3 | POST producto con token Cliente | Swagger | 403 |
| 4 | POST producto con token Admin | Swagger | 201 + Location |
| 5 | POST producto con codigo repetido | Swagger | 409 |
| 6 | POST producto con categoria inexistente | Swagger | 404 |
| 7 | POST producto con oferta invalida | Swagger | 400 |
| 8 | PATCH stock con cantidad que deja negativo | Swagger | 400 |
| 9 | GET producto por ID | Swagger | 200 con categoria y `precioFinal` |
| 10 | DELETE categoria con productos | Swagger | 409 |
| 11 | POST pedido con token Cliente | Swagger | 201, total correcto |
| 12 | Verificar stock descontado | Swagger/psql | stock anterior - cantidad |
| 13 | POST pedido con stock insuficiente | Swagger | 400 |
| 14 | POST pedido con producto repetido | Swagger | 400 |
| 15 | PATCH estado Pendiente -> Pagado | Swagger | 200 |
| 16 | PATCH estado Pendiente -> Entregado | Swagger | 400 |
| 17 | PATCH estado -> Cancelado | Swagger | 200 y stock repuesto |
| 18 | DELETE pedido Pagado | Swagger | 400 |
| 19 | DELETE pedido Pendiente como Admin | Swagger | 204 y stock repuesto |
| 20 | DELETE pedido como Cliente | Swagger | 403 |
| 21 | GET `/v3/api-docs` via gateway | navegador | JSON OpenAPI |
| 22 | Reiniciar ms-catalogo con ms-pedidos arriba | consola | Eureka/Feign reintentan sin cambios de codigo |

Verificar stock directamente:

```bat
docker exec postgres-db psql -U postgres -d productos ^
  -c "select id_producto, nombre, stock from productos order by id_producto;"
```

---

## 5. CORS

El gateway permite peticiones desde `http://localhost:*` con credenciales y los
metodos `GET, POST, PUT, PATCH, DELETE, OPTIONS`. Los microservicios tambien
tienen su propio `CorsConfigurationSource` por si se consumen directamente.
