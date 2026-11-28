# 🎬 Guión — Video de presentación de Sanrucho

**Duración objetivo:** 8–9 minutos (versión completa) · 4–5 minutos (versión corta al final)
**Enfoque:** la página web funcionando. **Muy poco código**: solo árbol de carpetas, terminal de pruebas y un diagrama de arquitectura.
**Público:** evaluación académica (Duoc UC) / presentación del proyecto.
**Tono:** cercano, claro y seguro. Hablar en primera persona plural (“construimos”, “hicimos”).

---

## ✅ Antes de grabar (checklist)

### 1. Levantar todo

- [ ] PostgreSQL arriba (Docker).
- [ ] Backend arriba: **Eureka (8761)**, **API Gateway (9000)** y los microservicios `ms-usuarios` (9001), `ms-catalogo` (9002), `ms-pedidos` (9003) y `ms-pagos` (9004).
  - Opción rápida: `cd Backend` → `docker compose up --build`
  - Opción desarrollo: `run-all.bat`
- [ ] Frontend arriba: `cd Frontend-React` → `npm run dev` → <http://localhost:5173>
- [ ] Verificar que el catálogo, el login y las boletas cargan sin el aviso de “backend apagado”.

> ⚠️ **Importante:** el catálogo, los usuarios, los pedidos y los pagos ya se consultan al backend a través del gateway (`http://localhost:9000`). El carrito y algunos datos de sesión se guardan en el navegador. Si el backend está apagado, la demo se ve incompleta.

### 2. Preparar la demo

- [ ] Borrar datos viejos del navegador: DevTools → Application → **Clear site data** (o `localStorage.clear()` en consola) y recargar.
- [ ] Tener a mano las cuentas de prueba:

| Rol | Correo | Contraseña |
| --- | --- | --- |
| Dueño | `dueno@gmail.com` | `dueno123` |
| Admin | `admin@gmail.com` | `admin123` |
| Cliente | `camila@gmail.com` | `camila123` |

- [ ] Hacer **una compra de ensayo** con el cliente para que “Mis pedidos” y “Boletas” tengan datos.
- [ ] Dejar un producto con **stock crítico** o agotado para mostrarlo en Reportes.
- [ ] Tener un producto en oferta (ej. `HK-PLUSH-001`) para el catálogo.
- [ ] Verificar que las imágenes cargan (si falta una, la app muestra el logo).
- [ ] Cerrar pestañas, notificaciones y la barra de marcadores.
- [ ] Navegador al **100 %** de zoom, ventana 1920×1080, cursor visible.
- [ ] Probar el micrófono y grabar en un lugar sin ruido.

### 3. Reglas de oro

- No mostrar la consola ni mensajes de error.
- No abrir archivos `.tsx` completos. **Código permitido:** árbol de carpetas, terminal de tests y diagrama.
- Moverse lento, pausar 2 segundos en cada pantalla importante.
- Grabar por bloques y unirlos después; si algo falla, cortar y repetir, no depurar en cámara.
- Usar datos ficticios en el checkout (nombre, dirección y tarjeta de prueba). Aclarar que el pago es **simulado**.

---

## 🎞️ Guión completo (escena por escena)

### Escena 1 — Introducción (0:00 – 0:30)

**En pantalla:** logo o página de inicio de Sanrucho. Texto inferior sugerido: “Sanrucho — Tienda online de productos Sanrio”.

**Narración (literal):**
> “¡Hola! Somos Sofía y Camila, y este es **Sanrucho**, nuestra tienda en línea de productos Sanrio. En este video les vamos a mostrar el sitio completo: desde que un cliente entra, explora el catálogo y compra, hasta el panel de administración donde se gestionan productos, usuarios y boletas. Nos vamos a enfocar en la experiencia de la página web y vamos a mostrar muy poco código, solo lo necesario para entender cómo está construido.”

---

### Escena 2 — Página de inicio (0:30 – 1:20)

**Acciones:**
1. Mostrar el inicio completo, sin apuro.
2. Pasar el mouse por una tarjeta que se voltea (flip card).
3. Hacer clic en una categoría destacada y volver al inicio.
4. Si ya hay sesión, mostrar el saludo con el nombre; si no, mostrar el bloque de “Inicia sesión para comprar”.

**Narración:**
> “Esta es la página de inicio. Arriba tenemos el menú de navegación y el carrito, que se actualiza solo cuando agregamos productos. El banner y el carrusel son parte de la identidad visual de la tienda. Más abajo aparecen las categorías destacadas, que llevan al catálogo ya filtrado, y una selección de productos con tarjetas que se voltean al pasar el mouse. Si no hay sesión iniciada, la página invita a entrar o crear una cuenta; si ya iniciamos sesión, nos saluda por nuestro nombre. Eso es React reaccionando al estado de la sesión.”

**Texto en pantalla:** “React + TypeScript + Vite · Bootstrap”.

---

### Escena 3 — Catálogo y detalle de producto (1:20 – 2:30)

**Acciones:**
1. Ir a **Productos**.
2. Escribir en el buscador (ej. “hello”).
3. Cambiar la categoría y mover el filtro de precio máximo.
4. Mostrar una tarjeta en oferta y una agotada.
5. Abrir el detalle de un producto (ej. Peluche Hello Kitty).
6. Bajar a las reseñas, mostrar estrellas y el formulario.
7. Elegir cantidad y pulsar **Agregar**; señalar el contador del carrito.

**Narración:**
> “Entramos a Productos. Aquí podemos buscar por nombre o código, filtrar por categoría y ajustar el precio máximo con este control. La lista se actualiza al instante. Cada tarjeta muestra la imagen, el precio, si está en oferta y si hay stock. Si un producto está agotado, el botón se deshabilita. Vamos a abrir este peluche de Hello Kitty… En el detalle vemos la descripción, el precio con descuento, el stock disponible y la cantidad que queremos. También hay una sección de opiniones: podemos ver las reseñas, el promedio de estrellas y dejar nuestra propia reseña. Agregamos el producto y fíjense que el contador del menú cambia automáticamente.”

**Texto en pantalla:** “Búsqueda + filtros + reseñas + carrito sincronizado”.

---

### Escena 4 — Carrito (2:30 – 3:05)

**Acciones:**
1. Ir al carrito.
2. Cambiar la cantidad de un producto.
3. Eliminar otro producto (opcional).
4. Mostrar cómo cambian subtotal, envío y total.
5. Pulsar **Continuar con el pago**.

**Narración:**
> “Este es el carrito. Podemos cambiar la cantidad o eliminar productos, y el subtotal, el envío y el total se recalculan solos. Si el carrito está vacío, se muestra un mensaje y un botón para volver a la tienda. Continuamos al pago.”

---

### Escena 5 — Checkout, pago y confirmación (3:05 – 4:20)

**Acciones:**
1. Completar el paso 1 con datos ficticios: nombre, correo, teléfono, dirección, región, comuna, fecha y hora.
2. Mostrar brevemente una validación (dejar un campo vacío y enseñar el aviso; luego completarlo).
3. Avanzar al paso 2.
4. Mostrar los tres métodos: débito, crédito y **Webpay**.
5. Seleccionar Webpay y confirmar.
6. En la pantalla de éxito, señalar código de boleta, número de pedido, seguimiento, total y despacho.
7. Entrar a **Ver resumen de compra** y mostrar el detalle completo.

**Narración:**
> “El checkout tiene dos pasos. Primero, los datos del cliente y del despacho: nombre, correo, teléfono, dirección, región, comuna, fecha y hora. El formulario valida el correo, el teléfono y los campos obligatorios antes de avanzar. Segundo, el método de pago: tarjeta de débito, crédito o Webpay. En esta demo el pago es simulado y no se cobra dinero real. Al confirmar, el sistema registra el pedido en el backend y nos lleva a la pantalla de éxito… Aquí vemos el código de boleta, el número de pedido, el código de seguimiento, el total y los datos del despacho. Desde acá podemos ver el resumen completo de la compra, con el detalle de los productos, los totales y la información de la boleta. Si el pago es rechazado, existe una pantalla de error que permite volver a intentarlo.”

> 💡 **Tip:** el pago tiene un 80 % de aprobación aleatoria. Si sale rechazado, puedes mostrar la pantalla de error como una funcionalidad y luego repetir la compra para terminar en la pantalla de éxito.

**Texto en pantalla:** “Pago simulado · Sin cobros reales”.

---

### Escena 6 — Cuenta, perfil y pedidos (4:20 – 5:05)

**Acciones:**
1. Cerrar sesión (si está abierta).
2. Ir a **Iniciar sesión** y entrar con `camila@gmail.com` / `camila123`.
3. Mostrar **Mi perfil**.
4. Bajar a **Mis pedidos** y mostrar el pedido de la compra anterior.
5. Ir a **Registrarse** y mostrar el formulario con sus validaciones (sin enviarlo).

**Narración:**
> “Para comprar con una cuenta, iniciamos sesión. El login valida las credenciales contra el backend y guarda un token JWT. En Mi perfil podemos actualizar nuestros datos y ver nuestro historial de pedidos, que se carga desde el backend. También existe el registro de nuevos usuarios, con validaciones de RUN, correo y contraseña.”

**Texto en pantalla:** “Sesión con JWT · Rutas protegidas por rol”.

---

### Escena 7 — Páginas de contenido (5:05 – 5:45)

**Acciones (montaje rápido, 8–10 s cada una):**
1. **Nosotros** — historia y equipo.
2. **Blog** — listado y un detalle.
3. **Contacto** — completar y enviar el formulario; mostrar la alerta de confirmación.
4. **Ofertas** y **Categorías** — mostrar que reutilizan el catálogo.

**Narración:**
> “Además de la tienda, el sitio tiene páginas de contenido: Nosotros, con la historia y el equipo; el Blog, con artículos y detalle; y Contacto, con un formulario que valida los datos y muestra una alerta de confirmación. También están las páginas de Ofertas y Categorías, que reutilizan el mismo catálogo.”

---

### Escena 8 — Panel de administración (5:45 – 7:30)

**Acciones:**
1. Cerrar sesión e iniciar como **Dueño** (`dueno@gmail.com` / `dueno123`).
2. Entrar a **Panel Admin**.
3. **Escritorio:** mostrar los cuatro indicadores, el buscador y la tabla de usuarios.
4. **Reportes:** bajar y mostrar métricas, productos por categoría, stock crítico y ganancias por mes.
5. Pulsar **Descargar CSV** y mostrar brevemente el archivo; luego **Imprimir / PDF** y mostrar el PDF generado.
6. **Productos:** buscar, editar un producto y mostrar el formulario (stock, precio, oferta, imagen). Guardar.
7. **Categorías:** mostrar el listado y el formulario.
8. **Usuarios:** crear/editar un usuario y abrir el **historial de compras** de un cliente.
9. **Boletas:** mostrar filtros y abrir el detalle de una boleta.
10. Mencionar que el menú cambia según el rol: el **Admin** no ve “Usuarios”.

**Narración:**
> “Ahora pasamos al panel de administración. Está protegido por rol: solo el Dueño y el Admin pueden entrar. El menú se adapta al rol; por ejemplo, Usuarios solo lo ve el Dueño. En el Escritorio tenemos los indicadores principales: usuarios, productos, pedidos y categorías, además de un buscador y una tabla de usuarios. En Reportes vemos métricas generales, productos por categoría, stock crítico y ganancias por mes. Podemos exportar todo a CSV para Excel o generar un PDF con el reporte y el gráfico. En Productos gestionamos el catálogo: crear, editar, buscar y controlar stock y ofertas. En Categorías administramos las categorías. En Usuarios, el Dueño puede crear, editar y revisar el historial de compras de cada cliente. Y en Boletas vemos las compras realizadas, con filtros por fecha, cliente o estado, y el detalle completo de cada boleta.”

**Texto en pantalla:** “Panel protegido por roles · Reportes exportables · CRUD completo”.

---

### Escena 9 — Arquitectura, pruebas y poco código (7:30 – 8:30)

> 🚫 **No mostrar archivos de código completos.** Solo estos tres momentos:

1. **Árbol de carpetas** del frontend en VS Code (10 s):
   `api`, `components`, `context`, `hooks`, `pages`, `storage`, `types`, `utils`.
2. **Terminal con pruebas** (15 s): `npm test` → 35 pruebas en verde.
3. **Diagrama simple** (20 s), hecho en una diapositiva:
   `Navegador → React (5173) → API Gateway (9000) → ms-usuarios / ms-catalogo / ms-pedidos / ms-pagos → PostgreSQL`, con `Eureka (8761)` al centro.

**Narración:**
> “Para cerrar, una mirada rápida a cómo está construido. El frontend está organizado por carpetas: páginas, componentes, contextos, hooks, API y utilidades. No vamos a entrar en el código, pero esta estructura nos permite separar la interfaz, el estado y la comunicación con el backend. El frontend está hecho con React, TypeScript y Vite, con Bootstrap para los estilos, Axios para las llamadas al backend y SweetAlert2 para las alertas. Del otro lado, el backend es una arquitectura de microservicios con Spring Boot y Java 21: un API Gateway en el puerto 9000, Eureka para el descubrimiento de servicios y microservicios de usuarios, catálogo, pedidos y pagos, cada uno con su base de datos PostgreSQL. La comunicación entre servicios usa OpenFeign y la seguridad se maneja con JWT. También incluimos pruebas unitarias con Karma y Jasmine: son 35 pruebas sobre validaciones, formatos, filtros de pedidos y componentes.”

> 💡 **Tip:** antes de grabar, ejecuta `npm test` y confirma que está en verde. Si alguna prueba falla, no la muestres: corrige o graba sin ese plano.

**Texto en pantalla:** “35 pruebas unitarias · API Gateway + Eureka + PostgreSQL”.

---

### Escena 10 — Cierre (8:30 – 9:00)

**En pantalla:** inicio o panel, con el logo. Música suave de fondo.

**Narración (literal):**
> “Con esto cerramos el recorrido por Sanrucho: una tienda completa, con catálogo, carrito, checkout, cuentas de usuario, contenido y un panel administrativo con reportes. Fue un proyecto que nos permitió aprender a integrar un frontend en React con un backend de microservicios, y a cuidar tanto la experiencia del cliente como la gestión del negocio. Gracias por verlo.”

---

## ✂️ Versión corta (4–5 minutos)

Si necesitas un video más breve, mantén este orden y recorta:

| Tiempo | Escena | Qué mostrar |
| --- | --- | --- |
| 0:00–0:20 | Intro | Logo + presentación. |
| 0:20–0:45 | Inicio | Carrusel, categorías, tarjetas. |
| 0:45–1:25 | Catálogo + detalle | Filtros, oferta, reseñas, agregar al carrito. |
| 1:25–2:25 | Carrito + checkout + éxito | Compra completa y resumen. |
| 2:25–2:50 | Cuenta | Login, perfil y “Mis pedidos”. |
| 2:50–3:50 | Admin | Escritorio, reportes (CSV/PDF), productos y boletas. |
| 3:50–4:20 | Arquitectura + tests | Árbol de carpetas, `npm test`, diagrama. |
| 4:20–4:40 | Cierre | Recap y gracias. |

**Qué eliminar en la versión corta:** registro, blog, nosotros, contacto, categorías del admin, edición de usuarios e historial.

---

## 🗺️ Mapa de rutas (para navegar rápido durante la grabación)

**Público:**
`/` · `/productos` · `/producto-detalle?codigo=HK-PLUSH-001` · `/carrito` · `/ofertas` · `/categorias` · `/checkout` · `/pago/exito` · `/pago/error` · `/resumen-compra` · `/login` · `/registro` · `/perfil` · `/nosotros` · `/blog` · `/blog/detalle-1` · `/contacto`

**Administración (Dueño/Admin):**
`/admin` · `/admin/reportes` · `/admin/productos` · `/admin/productos/form` · `/admin/categorias` · `/admin/usuarios` · `/admin/usuarios/form` · `/admin/usuarios/:run/historial` · `/admin/boletas` · `/admin/boletas/:id` · `/admin/perfil`

---

## 🆘 Plan B (si algo falla en vivo)

| Problema | Solución rápida |
| --- | --- |
| Aparece aviso de “backend apagado” | Levantar Gateway (9000) y los microservicios; recargar. |
| El login falla | Verificar `ms-usuarios` (9001) y el gateway; usar las cuentas del seed. |
| No aparece “Usuarios” en el admin | Estás como Admin; entra como **Dueño**. |
| El pago sale rechazado | Es aleatorio (20 %). Muestra la pantalla de error y repite la compra. |
| Una imagen no carga | La app muestra el logo; evita ese producto o corrige la ruta antes de grabar. |
| `npm test` no está en verde | No grabes ese plano; corrige o muestra solo el árbol de carpetas. |
| Puerto 5173 ocupado | Cierra el otro proceso o cambia el puerto en `vite.config.ts`. |

---

## 🎤 Frases puente útiles

- “Como pueden ver, la interfaz se actualiza sin recargar la página.”
- “Estos datos no están escritos en el HTML: vienen del backend y React los renderiza.”
- “El mismo catálogo se reutiliza en Ofertas y Categorías.”
- “El panel no es solo visual: permite crear, editar y exportar información.”
- “No vamos a entrar en el código; lo importante es cómo se conectan las partes.”

---

## 📌 Resumen de lo que el video debe dejar claro

1. Es una **tienda completa**, no solo una maqueta: catálogo, carrito, checkout, cuentas y administración.
2. El **cliente** puede buscar, filtrar, reseñar y comprar.
3. El **administrador** puede gestionar productos, categorías, usuarios, boletas y reportes.
4. Hay **roles y rutas protegidas** (JWT).
5. El frontend **se conecta a un backend real de microservicios** (Gateway + Eureka + PostgreSQL).
6. El proyecto tiene **pruebas unitarias** y una **organización clara** por carpetas.
7. Se mostró **muy poco código**, priorizando la experiencia web.
