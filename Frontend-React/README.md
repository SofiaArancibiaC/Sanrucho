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
cd sanrucho-react

# 2. Instalar las dependencias (solo la primera vez)
npm install

# 3. Levantar el servidor de desarrollo
npm run dev
```

Se abrirá el navegador en <http://localhost:5173>.

Otros comandos:

| Comando           | Qué hace                                              |
| ----------------- | ----------------------------------------------------- |
| `npm run dev`     | Servidor de desarrollo con recarga automática         |
| `npm run build`   | Verifica tipos (TypeScript) y genera la carpeta `dist`|
| `npm run preview` | Sirve la versión compilada de `dist`                  |

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
sanrucho-react/
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
3. Agregar **tests** de las funciones de `utils/` y de los contextos.
4. Reemplazar `localStorage` por una **API real** (backend) manteniendo los tipos.
