# Documento de APIs e Integración — Sanrucho

**Fecha:** 24 de noviembre de 2026
**Propósito:** documentar cómo el frontend React se conecta al backend de microservicios, qué datos se intercambian y qué decisiones se tomaron durante la integración.

---

## 1. Arquitectura general

```
                    Navegador
                        │
              http://localhost:5173 (Vite)
                        │  axios
                        ▼
            API Gateway :9000  (Spring Cloud Gateway)
              │        │        │        │
      ┌───────┘        │        │        └────────┐
      ▼                ▼        ▼                 ▼
 ms-usuarios     ms-catalogo  ms-pedidos      ms-pagos
    :9001           :9002        :9003           :9004
      │                │        │  │
      └────────┬───────┘        │  └── Feign → ms-catalogo (stock)
               ▼                └────── Feign → ms-usuarios (validar usuario)
         PostgreSQL :5433
       (usuarios, catalogo, pedidos, pagos)

            Eureka :8761  ← registro/descubrimiento de servicios
```

---

## 2. Rutas del Gateway

Definidas en `Backend/api-gateway/src/main/resources/application.yml`:

| Ruta | Microservicio |
|---|---|
| `/api/v1/productos/**` | `ms-catalogo` |
| `/api/v1/categorias/**` | `ms-catalogo` |
| `/api/v1/usuarios/**`, `/api/v1/auth/**` | `ms-usuarios` |
| `/api/v1/pedidos/**` | `ms-pedidos` |
| `/api/v1/pagos/**` | `ms-pagos` |
| `/ms-*/v3/api-docs/**` | Swagger unificado |

CORS global: orígenes `*`, métodos `*`, cabeceras `*`, `allow-credentials: true`.

---

## 3. Flujos de integración

### 3.1 Login y sesión

1. `Login.tsx` envía `POST /api/v1/auth/login` con `{email, password}`.
2. `ms-usuarios` valida el hash BCrypt y responde `{token, usuario}`.
3. `AuthContext`:
   - guarda el JWT en `localStorage` con clave `sanrucho.token`;
   - construye la sesión con `idUsuario`, `rolId` y `rol` ("Dueño" / "Admin" / "Cliente");
   - guarda la sesión en `sanrucho.sesion`.
4. `client.ts` inyecta `Authorization: Bearer <token>` en cada petición mediante un interceptor.
5. Al recargar la página, `leerSesionInicial()` prefiere la sesión guardada; si no existe, decodifica el JWT (que incluye `rol` e `idUsuario`).
6. Un 401 limpia el token y redirige a `/login`.

### 3.2 Catálogo y categorías

- `useProductos()` → `GET /productos?size=1000` → adapta `idCategoria` → `categoriaId` e `imagenUrl` → `imagen`.
- `useTablasMaestras()` → `GET /categorias?size=1000` → lista para filtros y selects. Si falla, usa `localStorage` como respaldo.
- Regiones, comunas y roles se mantienen en `localStorage` (tablas maestras del proyecto original).

### 3.3 Creación de un pedido (checkout)

1. `Checkout.tsx` valida el pago simulado (~80 % aprobado).
2. Resuelve el `idProducto` de cada item del carrito (si falta, lo busca por `codigo`).
3. `POST /api/v1/pedidos` con `{idUsuario, items}`.
4. `ms-pedidos`:
   - valida usuario existente vía **Feign** → `ms-usuarios`;
   - valida producto y stock vía **Feign** → `ms-catalogo`;
   - calcula `precioFinal × cantidad`, descuenta stock y guarda el pedido en estado `Pendiente`.
5. Si falla, el checkout muestra un aviso en vez de ocultar el error.

### 3.4 Boletas y perfil

- `usePedidos()` → `GET /pedidos?size=1000` (resúmenes) y, por cada pedido, `GET /pedidos/{id}` (detalles).
- `adaptarPedido()` convierte cada respuesta al tipo `Pedido` que esperan `Perfil`, `AdminBoletas`, `BoletaDetalle` y `AdminReportes`.
- `Perfil` filtra por `idUsuario`; si no está disponible, filtra por correo.
- `AdminBoletas` incluye un botón **Actualizar** para volver a consultar el backend.

---

## 4. Mapa de adaptación DTO → tipo frontend

| Backend (DTO) | Frontend (tipo) | Transformación |
|---|---|---|
| `UsuarioResponse.idUsuario` | `Usuario.idUsuario` | directo |
| `UsuarioResponse.email` | `Usuario.correo` | renombrado |
| `UsuarioResponse.rol` (`DUENO`/`ADMIN`/`CLIENTE`) | `Usuario.rolId` (1/2/3) y `rol` ("Dueño"/"Admin"/"Cliente") | mapa de roles |
| `ProductoResponse.idCategoria` | `Producto.categoriaId` | renombrado |
| `ProductoResponse.imagenUrl` | `Producto.imagen` | renombrado |
| `CategoriaResponse.id / nombre` | `Categoria.id / nombre` | directo |
| `PedidoResumenResponse.fechaCompra` | `Pedido.fechaCreacion` | renombrado |
| `PedidoResumenResponse.estado` | `Pedido.estadoPago` | `Pagado`/`Enviado`/`Entregado`/`Pendiente` → `aprobado`; `Cancelado` → `rechazado` |
| `PedidoResumenResponse.id` (number) | `Pedido.id` (string) | `String(id)` |
| — | `Pedido.codigoBoleta` | generado: `B-${id.padStart(6,"0")}` |
| `DetallePedidoResponse[]` | `Pedido.items[]` | `idProducto→id`, `nombreProducto→nombre`, `precioUnitario→precio`, `cantidad` |
| `PedidoResponse.total` | `Pedido.total` | `Number(total)` (BigDecimal → number) |
| `idUsuario` del pedido | `Pedido.cliente` | consulta a `ms-usuarios`; si no hay permisos, valores genéricos |

Archivos responsables: `src/api/pedidos.ts`, `src/api/productos.ts`, `src/api/usuarios.ts`, `src/api/categorias.ts`, `src/hooks/useProductos.ts`, `src/hooks/useUsuarios.ts`, `src/hooks/useTablasMaestras.ts`.

---

## 5. Seguridad entre servicios

- **JWT unificado:** todos los microservicios validan el token con la misma clave. `ms-usuarios` emite los claims `sub`, `rol` e `idUsuario`.
- **Roles en mayúsculas:** los `SecurityConfig` usan `DUENO`, `ADMIN`, `CLIENTE` para coincidir con el claim del token.
- **Feign:** el `FeignClientInterceptor` de `common` propaga el `Authorization: Bearer ...` en las llamadas inter-servicio.
- **Lectura de usuarios:** permitida a los tres roles porque `ms-pedidos` necesita validar la existencia del usuario al crear pedidos. La escritura sigue siendo exclusiva del dueño.
- **Almacenamiento:** `localStorage` se usa solo para token, sesión, carrito y tablas maestras auxiliares; el resto de los datos proviene del backend.

---

## 6. Incidencias detectadas y resueltas durante la integración

| Síntoma | Causa | Solución |
|---|---|---|
| El frontend no arrancaba | Dependencias `axios`, `jspdf`, `html2canvas` en `package.json` pero no instaladas | `npm install` |
| Login bloqueado por el navegador | Imágenes Docker desactualizadas: el gateway no tenía configuración CORS | Recompilar y `docker compose up -d --build` |
| `PUT /productos` devolvía 401 | Secreto JWT distinto en `ms-catalogo` y token sin claim `rol` | Unificar secreto y agregar `rol` + `idUsuario` al JWT |
| `PUT /usuarios` devolvía 400 | Backend exigía contraseña even en edición | Contraseña vacía = conservar hash actual |
| No se veían categorías | Gateway sin ruta `/api/v1/categorias` | Agregar ruta en el gateway |
| Perfil sin pedidos tras recargar | Sesión decodificada sin `idUsuario` | Preferir sesión guardada y agregar claim al JWT |
| Compras nuevas no se registraban | `POST /pedidos` → 403: Feign a `ms-usuarios` con rol cliente no autorizado | Permitir lectura de usuarios a `CLIENTE` |
| RUN rechazado al editar usuario | Base guarda RUN con guion; validador solo aceptaba dígitos | Normalizar puntos/guion en `validarRun` |
| Reportes en cero | `AdminReportes` leía `localStorage` | Usar hooks del backend |

> Lección operativa: los `Dockerfile` solo copian `target/*.jar`. Después de cambiar código Java o `application.yml`, ejecutar:
> ```bash
> cd Backend
> mvn -B -DskipTests install
> docker compose up -d --build
> ```

---

## 7. Verificación de la integración

| Comprobación | Resultado |
|---|---|
| Login con los 3 roles | 200 + token con claims `sub`, `rol`, `idUsuario` |
| `GET /api/v1/categorias` | 200 → 7 categorías |
| `GET /api/v1/productos` | 200 → 8 productos |
| `GET /api/v1/usuarios` (admin y dueño) | 200 → 4 usuarios |
| `GET /api/v1/pedidos?idUsuario=3` | 200 → 2 pedidos de Camila |
| `POST /api/v1/pedidos` | 201 → pedido creado y stock descontado |
| `PUT /api/v1/productos/{id}` (admin) | 200 |
| `PUT /api/v1/usuarios/{id}` (dueño) | 200 |
| Preflight CORS desde `localhost:5173` | 200 con `Access-Control-Allow-Origin` |
| Pruebas unitarias del frontend | 34 / 34 correctas |
| `npm run build` | correcto |

---

## 8. Archivos clave de la integración

```
Backend/
├─ api-gateway/src/main/resources/application.yml   ← rutas + CORS
├─ common/.../security/                             ← filtros JWT y propagación Feign
├─ ms-usuarios/.../security/                        ← reglas de acceso
├─ ms-usuarios/.../JwtService.java                  ← emisión del token
└─ init-multi-db/*.sql                              ← datos de prueba

Frontend-React/
├─ .env                                             ← URL del gateway
├─ vite.config.ts                                   ← inyección de __API_URL__
├─ src/api/                                         ← cliente axios y adaptadores
├─ src/hooks/                                       ← carga de datos del backend
└─ src/context/AuthContext.tsx                       ← sesión JWT
```
