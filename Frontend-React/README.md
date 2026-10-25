# Sanrucho React

Migración del sitio **Sanrucho** (tienda de productos Sanrio, proyecto académico) desde
una maqueta en **Flask + HTML/CSS/JS con localStorage** hacia una aplicación
**React + TypeScript + Vite**.

El objetivo de la migración es conservar la apariencia, las rutas, los datos de prueba
y las reglas de negocio del sitio original, pero reemplazando la manipulación manual del
DOM (`document.getElementById`, `innerHTML`, `addEventListener`) por **componentes,
estado, hooks y contextos de React**.

---

## 1. Requisitos

- **Node.js 18 o superior** (recomendado 20+). Verifica con:

  ```bash
  node -v
  npm -v
  ```

  Si no lo tienes, descárgalo desde <https://nodejs.org> (versión LTS).

---

## 2. Cómo ejecutar el proyecto

```bash
# 1. Entrar a la carpeta del proyecto
cd Frontend-React

# 2. Instalar las dependencias (solo la primera vez)
npm install

# 3. Levantar el servidor de desarrollo
npm run dev
```

Se abrirá el navegador en <http://localhost:5173>.

Otros comandos:

| Comando             | Qué hace                                              |
| ------------------- | ----------------------------------------------------- |
| `npm run dev`       | Servidor de desarrollo con recarga automática         |
| `npm run build`     | Verifica tipos (TypeScript) y genera la carpeta `dist`|
| `npm run preview`   | Sirve la versión compilada de `dist`                  |
| `npm test`          | Ejecuta las pruebas unitarias (Karma + Jasmine) una vez |
| `npm run test:watch`| Igual que `npm test`, pero re-ejecuta al guardar cambios |

---

## 3. Usuarios de prueba

Los datos se cargan automáticamente en `localStorage` la primera vez (ver `src/data/seed.ts`).

| Rol     | Correo             | Contraseña | Puede entrar a `/admin` |
| ------- | ------------------ | ---------- | ----------------------- |
| Dueño   | `dueno@gmail.com`  | `dueno123` | Sí (ve todo)            |
| Admin   | `admin@gmail.com`  | `admin123` | Sí (no ve "Usuarios")   |
| Cliente | `camila@gmail.com` | `camila123`| No                      |

> Regla de roles: **1 = Dueño**, **2 = Admin**, **3 = Cliente**.
> El enlace "Usuarios" del panel solo se muestra al Dueño.

---

## 4. Estructura del proyecto

```
Frontend-React/
├─ index.html                 # Punto de entrada HTML (fuentes + <div id="root">)
├─ package.json
├─ tsconfig.json              # Configuración estricta de TypeScript
├─ vite.config.ts
├─ public/
│  └─ static/                 # Assets copiados del proyecto original
│     ├─ css/estilos.css
│     └─ img/                 # logo, index/, nosotros/, productos/
└─ src/
   ├─ main.tsx                # Monta React, carga Bootstrap y estilos
   ├─ App.tsx                 # Enrutador (reemplazo de las rutas de app.py)
   ├─ estilos.css             # CSS original adaptado
   ├─ types/index.ts          # Tipos del dominio (Producto, Usuario, etc.)
   ├─ storage/
   │  ├─ claves.ts            # SANRUCHO_KEYS (nombres en localStorage)
   │  └─ useColeccion.ts      # Lectura/escritura reactiva de localStorage
   ├─ data/seed.ts            # Datos de prueba + generarSiguienteId
   ├─ context/
   │  ├─ AuthContext.tsx      # Sesión, login, registro, logout
   │  └─ CartContext.tsx      # Carrito, totales y envío
   ├─ hooks/                  # useProductos, useUsuarios, useNoticias, ...
   ├─ utils/                  # validaciones, formato, alertas (SweetAlert2)
   ├─ components/
   │  ├─ layout/              # Navbar, Footer, AdminLayout, AdminSidebar...
   │  └─ common/              # ProductCard, DataTable, RutaProtegida
   └─ pages/                  # Páginas públicas y de administración
```

---

## 5. Cómo se migró cada parte

### 5.1 Datos (localStorage)

El sitio original guardaba todo en `localStorage` con claves `sanrucho_*`. Se mantuvo
exactamente el mismo esquema para no romper datos existentes:

- `src/storage/claves.ts` → objeto `SANRUCHO_KEYS` (antes en `storage.js`).
- `src/storage/useColeccion.ts` → reemplaza `obtenerColeccion()` / `guardarColeccion()`.
  Es un hook que además **avisa a todos los componentes suscritos** cuando cambia una
  colección, así el carrito y el catálogo se actualizan solos.

### 5.2 Equivalencias JS → React

| Archivo original        | Nuevo archivo en React                                      |
| ----------------------- | ----------------------------------------------------------- |
| `storage.js`            | `src/storage/claves.ts`, `src/storage/useColeccion.ts`, `src/data/seed.ts` |
| `validaciones.js`       | `src/utils/validaciones.ts`                                 |
| (helpers varios)        | `src/utils/formato.ts`, `src/utils/alertas.ts`              |
| `carrito.js`            | `src/context/CartContext.tsx` + `src/pages/Carrito.tsx`     |
| `productos.js` (catálogo) | `src/pages/Productos.tsx`, `src/components/common/ProductCard.tsx` |
| `detalle-producto.js`   | `src/pages/ProductoDetalle.tsx`                             |
| `resenas.js`            | `src/hooks/useResenas.ts` + sección en `ProductoDetalle.tsx`|
| `blog.js`               | `src/pages/Blog.tsx` + `src/hooks/useNoticias.ts`           |
| `contacto.js`           | `src/pages/Contacto.tsx`                                    |
| `usuarios.js` (login/registro) | `src/context/AuthContext.tsx` + `src/pages/Login.tsx`, `Registro.tsx` |
| `usuarios.js` (admin)   | `src/pages/admin/AdminUsuarios.tsx`, `AdminUsuarioForm.tsx` |
| `productos.js` (admin)  | `src/pages/admin/AdminProductos.tsx`, `AdminProductoForm.tsx` |
| `admin.js`              | `src/pages/admin/AdminDashboard.tsx`                        |

### 5.3 ¿Qué cambió conceptualmente?

- **Ya no se toca el DOM a mano.** Antes: `document.getElementById(...).innerHTML = ...`.
  Ahora: el estado vive en React y la vista se vuelve a dibujar sola.
- **Filtros del catálogo.** Antes se recalculaban con listeners. Ahora son `useState`
  y la lista se calcula con `useMemo`.
- **Carrito y sesión.** Antes se leían de `localStorage` en cada página. Ahora viven en
  `CartContext` y `AuthContext`, accesibles desde cualquier componente con `useCart()`
  y `useAuth()`. El badge del navbar se actualiza automáticamente.
- **Rutas.** Las de `app.py` con Flask se convirtieron en `<Route>` de
  `react-router-dom`. Las URLs se conservan igual (`/productos`, `/carrito`, etc.).
- **Protección del panel.** Antes `protegerPaginaAdmin([1,2])`. Ahora el componente
  `RutaProtegida rolesPermitidos={[1,2]}` envuelve las rutas `/admin/*`.
- **Bootstrap.** El JS de Bootstrap se importa una vez en `main.tsx` y los componentes
  interactivos (carrusel, offcanvas, dropdown) siguen funcionando por atributos
  `data-bs-*`, para no reescribir el HTML ni el CSS.

---

## 6. Rutas disponibles

Públicas: `/`, `/productos`, `/producto-detalle?codigo=...`, `/carrito`, `/login`,
`/registro`, `/nosotros`, `/blog`, `/blog/detalle-1`, `/blog/detalle-2`, `/contacto`.

Administración (requiere rol Dueño o Admin): `/admin`, `/admin/productos`,
`/admin/productos/form`, `/admin/usuarios`, `/admin/usuarios/form`.

---

## 7. Problemas comunes

- **`Port 5173 is in use`** → cierra el otro proceso o cambia el puerto en
  `vite.config.ts`.
- **Los datos parten "vacíos" o quiero reiniciar la demo** → borra las claves
  `sanrucho_*` de `localStorage` (DevTools → Application → Local Storage) y recarga;
  `seed.ts` volverá a sembrar los datos.
- **Error al compilar por tipos** → ejecuta `npm run build` para ver el detalle exacto.

---

## 8. Siguientes pasos sugeridos

1. Subir las imágenes que faltan a `public/static/img/productos/`.
2. Implementar el módulo de **Pedidos** (hoy muestra 0 en el panel).
3. Conectar el frontend con el **backend** (los microservicios) en vez de `localStorage`
   (manteniendo los tipos de `src/types`).
4. Agregar más pruebas unitarias (contextos, componentes).

---

## 9. Levantar el proyecto completo

El repositorio tiene dos partes independientes:

```
Sanrucho/
├─ Backend/          # Microservicios Java (Spring Cloud) + Postgres + Kafka
└─ Frontend-React/   # La aplicación React (esto es lo que se "ve")
```

> **Importante:** el frontend guarda los datos en `localStorage`, así que **no necesita
> el backend** para verse ni para funcionar. Para ver la página web basta con levantar
> el frontend.

### 9.1 Levantar SOLO el frontend (recomendado para ver la página)

```bash
cd Frontend-React
npm install        # solo la primera vez
npm run dev
```

Se abre <http://localhost:5173>. Esto es lo que necesitas para "ver la web en internet"
(es decir, en el navegador).

Si quieres **publicarla en internet de verdad** (una URL pública), compila y sube la
carpeta `dist/` a un hosting estático gratuito (Vercel, Netlify o GitHub Pages):

```bash
npm run build      # genera la carpeta dist/
npm run preview    # la sirve localmente para probarla antes de subirla
```

### 9.2 Levantar el backend (opcional, solo si necesitas los microservicios)

El backend es una arquitectura de microservicios con **Eureka**, **API Gateway**,
**Postgres**, **Kafka** y ~14 servicios. Requiere **Java + Maven** y **Docker Desktop**
abiertos.

**Opción A — todo con Docker (la más simple):**

```bash
cd Backend
docker compose up --build
```

Quedan disponibles:
- Eureka: <http://localhost:8761>
- API Gateway: <http://localhost:9012>

**Opción B — servicio por servicio con Maven (para desarrollo):**

```bash
cd Backend
run-all.bat          # abre una ventana por cada microservicio
```

Antes hay que tener Postgres y Kafka arriba (`docker compose up -d postgres kafka`).

> Nota: verifica que tu versión de Java sea compatible con la del `pom.xml`
> (este equipo tiene Java 27 y Maven 3.9). Si un servicio falla al arrancar, revisa
> la versión de Java configurada en el proyecto.

---

## 10. Pruebas unitarias (Karma + Jasmine)

### 10.1 ¿Qué es cada cosa?

- **Jasmine**: el *framework* de pruebas. Permite escribir casos con
  `describe()` (grupo), `it()` (un caso) y `expect()` (una afirmación).
- **Karma**: el *runner*. Abre un navegador real (Chrome Headless), carga los archivos
  `.spec.ts`, ejecuta Jasmine y muestra el resultado.

La configuración ya está en `karma.conf.cjs` y en `package.json`.

### 10.2 Cómo ejecutar las pruebas

```bash
cd Frontend-React
npm test            # ejecuta una vez y termina (ideal para entregar)
npm run test:watch  # queda escuchando y re-ejecuta al guardar
```

Salida esperada:

```
Executed 10 of 10 SUCCESS
TOTAL: 10 SUCCESS
```

### 10.3 ¿Dónde están las pruebas?

```
src/utils/validaciones.spec.ts   # 7 pruebas
src/utils/formato.spec.ts        # 3 pruebas
```

Resultado: **10 pruebas unitarias en total**.

> El archivo `src/ejemplo.spec.tsx` (la prueba de ejemplo `expect(true).toBe(true)`)
> fue reemplazado por estas 10 pruebas reales.

### 10.4 Cómo se escribe una prueba (paso a paso)

1. **Elige qué probar.** Lo más fácil de probar son **funciones puras** (reciben algo y
   devuelven algo, sin depender de la pantalla). Por eso probamos `src/utils/`.
2. **Crea un archivo `.spec.ts`** al lado del archivo a probar (o en una carpeta
   `tests/`). El nombre debe terminar en `.spec.ts`.
3. **Importa la función** que vas a probar:
   ```ts
   import { validarCorreo } from "./validaciones";
   ```
4. **Agrupa con `describe()`** y **escribe el caso con `it()`**:
   ```ts
   describe("validarCorreo", () => {
     it("rechaza dominios no permitidos", () => {
       expect(validarCorreo("x@hotmail.com")).toBe(false);
     });
   });
   ```
5. **Define la expectativa** con `expect(valor).toBe(resultadoEsperado)`.
   Matchers útiles: `toBe`, `toEqual`, `toBeTrue`/`toBeFalse`, `toBeGreaterThan`,
   `toContain`, `toThrow`.
6. **Ejecuta `npm test`** y corrige hasta que todo quede en verde.

### 10.5 Detalle de las 10 pruebas

| # | Archivo              | Qué verifica                                                    |
| - | -------------------- | --------------------------------------------------------------- |
| 1 | validaciones.spec.ts | `validarCorreo` acepta dominios permitidos                      |
| 2 | validaciones.spec.ts | `validarCorreo` rechaza dominios no permitidos                  |
| 3 | validaciones.spec.ts | `validarCorreo` rechaza correos vacíos o de más de 100 caracteres |
| 4 | validaciones.spec.ts | `validarPassword` acepta entre 4 y 10 caracteres                |
| 5 | validaciones.spec.ts | `validarPassword` rechaza largos fuera de rango                 |
| 6 | validaciones.spec.ts | `validarRun` acepta un RUN con DV correcto                      |
| 7 | validaciones.spec.ts | `validarRun` rechaza DV incorrecto o formato inválido           |
| 8 | formato.spec.ts      | `formatearPrecio` agrega `$` y separador de miles               |
| 9 | formato.spec.ts      | `rutaImagenProducto` devuelve el logo si no hay imagen          |
| 10| formato.spec.ts      | `rutaImagenProducto` respeta rutas absolutas y arma relativas   |

### 10.6 Problemas comunes en las pruebas

- **No abre el navegador / "Chrome not found"** → instala Google Chrome. Si usas otro
  navegador, cambia `browsers` en `karma.conf.cjs`.
- **`ChromeHeadless` falla por sandbox** → usa launcher `ChromeHeadlessNoSandbox`
  definido con `customLaunchers`.
- **Los tests no se refrescan** → recuerda que `npm test` corre una vez; para modo
  continuo usa `npm run test:watch`.
- **Debes detener el modo watch** → presiona `Ctrl + C` en la terminal.

---

## 11. Problemas con las imágenes (y cómo solucionarlos)

### ¿Dónde están las imágenes?

Todas las imágenes viven en `public/static/img/`:

```
public/static/img/
├─ logo-sanrucho.png
├─ productos/          # imágenes de los productos
├─ index/              # imágenes de la página de inicio
└─ nosotros/           # imágenes de "Nosotros"
```

En Vite, todo lo que está en `public/` se sirve desde la raíz `/`. Por eso el
código usa rutas como `/static/img/productos/hello-kitty1.webp`.

**Prueba rápida:** con `npm run dev` abierto, entra a
`http://localhost:5173/static/img/productos/hello-kitty1.webp`. Si la ves, el
servidor está entregando bien las imágenes.

### El carrito y los productos se guardan en el navegador

Los datos (productos, usuarios, carrito) se guardan en `localStorage`, **no** en
el proyecto. Esto es importante porque `localStorage` es **por origen**
(`localhost:5173`), no por carpeta: si antes ejecutaste otra versión del proyecto
en el mismo puerto, el navegador **reutiliza esos datos**.

Consecuencia típica: creaste productos con tus propias fotos en una versión
anterior; ahora esos productos siguen guardados en tu navegador, pero **los
archivos de esas fotos no están en este proyecto**, así que sus imágenes no
cargan.

### Cómo volver a los datos de ejemplo (imágenes correctas)

Si quieres partir de cero con los 8 productos de ejemplo (que sí tienen sus
imágenes), borra los datos guardados:

1. Abre la página y presiona **F12** (DevTools).
2. Ve a la pestaña **Application** → **Storage** → **Clear site data**.
3. Recarga la página.

O en la pestaña **Console** escribe:

```js
localStorage.clear(); location.reload();
```

> Al hacer esto se borran también los productos que hayas agregado tú.

### Cómo usar tus propias imágenes en un producto

1. **Copia el archivo** de tu foto dentro de `public/static/img/productos/`
   (por ejemplo `public/static/img/productos/mi-peluche.webp`).
2. Al crear/editar el producto, en el campo **Imagen** escribe la ruta:
   `/static/img/productos/mi-peluche.webp`.
3. Guarda y recarga. Si la ruta es correcta, la imagen aparece.

### "Imagen rota"

Si la ruta de una imagen no existe, la app ahora **muestra el logo** de Sanrucho
en lugar de un icono de imagen rota (ver `imagenPorDefecto` en
`src/utils/formato.ts`).
