# Documento de Cobertura del Testing — Sanrucho

**Fecha:** 24 de noviembre de 2026
**Proyecto:** Sanrucho (Duoc UC — Analista Programador)
**Alcance:** Pruebas unitarias del **frontend React + TypeScript**

---

## 1. Objetivo

Validar la lógica y el comportamiento de los componentes y funciones clave del frontend, incluyendo la manipulación del DOM, mediante pruebas unitarias automatizadas. Esto cubre los requerimientos 5 y 6 de la pauta:

- Existencia de pruebas que validen funciones clave y comportamientos de componentes.
- Proceso de testeo configurado con las herramientas adecuadas y cobertura de las funcionalidades principales.

---

## 2. Herramientas y configuración

| Herramienta | Versión / rol |
|---|---|
| Jasmine | Framework de pruebas (behaviors y assertions `expect`) |
| Karma | Test runner en navegador real |
| karma-typescript | Compila y empaqueta TypeScript para Karma |
| Chrome Headless | Navegador donde se ejecutan las pruebas |
| React 18 `act()` + `react-dom/client` | Renderizado y simulación de eventos en el DOM |

**Archivos de configuración**

- `Frontend-React/karma.conf.cjs`: frameworks, archivos, preprocessors, reporter `spec`, reporter de cobertura y mocks de dependencias.
- `Frontend-React/tsconfig.json`: opciones del compilador.
- `Frontend-React/package.json`: scripts `test` y `test:watch`.

Ajustes realizados para que el proceso funcione:

1. `module: "CommonJS"` y `moduleResolution: "Node"` en `karmaTypescriptConfig.compilerOptions` (Karma no ejecuta módulos ESM directamente).
2. Se reemplazó `import.meta.env` por la constante `__API_URL__` inyectada por Vite (`vite.config.ts`), de modo que el mismo código compile en Vite y en Karma.
3. Se agregaron stubs en `src/test-mocks/` para `axios` y `sweetalert2`, ya que sus builds ESM no son compatibles con el bundler de karma-typescript.
4. Se habilitaron reportes de cobertura: `html`, `text-summary` y `json`.

---

## 3. Cómo ejecutar las pruebas

```bash
cd Frontend-React
npm test            # ejecuta una vez (single run)
npm run test:watch  # vuelve a ejecutar al guardar un archivo
```

El reporte HTML se genera en:

```
Frontend-React/coverage/Chrome Headless <version> (Windows 10)/html/index.html
```

El resumen también se imprime en la terminal al finalizar.

---

## 4. Suite de pruebas (34 pruebas)

| Archivo | Casos | Qué valida |
|---|---:|---|
| `src/utils/validaciones.spec.ts` | 7 | Correo (dominios permitidos, vacío, longitud), contraseña (4–10 caracteres) y RUN (módulo 11, con/sin guion) |
| `src/utils/formato.spec.ts` | 3 | Formato de precio CLP y rutas de imágenes (absoluta, relativa, sin imagen) |
| `src/utils/pedidos.spec.ts` | 6 | Filtros de boletas (texto, estado, fechas, orden), etiqueta de pago y asociación por correo |
| `src/api/pedidos.spec.ts` | 4 | Adaptador DTO backend → `Pedido`: estados, resumen, detalles y datos del cliente |
| `src/components/common/DataTable.spec.tsx` | 2 | Renderiza encabezados/filas y mensaje de estado vacío (DOM) |
| `src/components/common/ProductCard.spec.tsx` | 4 | Nombre/precio, stock agotado (botón deshabilitado), insignia de oferta y click en "Agregar" que modifica `localStorage` (DOM + evento) |
| `src/components/common/BoletaDetalle.spec.tsx` | 4 | Título, código, estado de pago, filas por producto, datos de cliente/despacho y cambio a rechazado (DOM) |
| `src/components/layout/Navbar.spec.tsx` | 1 | Click en "Cerrar sesión" limpia la sesión y muestra el bloque de invitado (DOM + evento) |
| `src/pages/Nosotros.spec.tsx` | 3 | Título, descripción, avatares y enlace al formulario (DOM) |

**Técnicas aplicadas**

- **AAA (Arrange–Act–Assert):** cada prueba prepara datos, ejecuta la acción y verifica el resultado.
- **Fixtures:** objetos `USUARIO`, `PRODUCTO` y `PEDIDO` reutilizables.
- **Manipulación del DOM:** `createRoot`, `act()`, `querySelector`, `querySelectorAll`, `textContent` y `.click()`.
- **Aislamiento:** `beforeEach` / `afterEach` limpian `localStorage` y desmontan el root de React.
- **Mocks:** dependencias externas se sustituyen por stubs controlados.
- **Pruebas puras:** funciones de validación, formato y adaptadores se prueban sin renderizado.

---

## 5. Resultado de la ejecución

```
Chrome Headless 157.0.0.0 (Windows 10): Executed 34 of 34 SUCCESS
TOTAL: 34 SUCCESS
```

### Cobertura global (instrumentación de todo `src/`)

| Métrica | Porcentaje | Sentencias cubiertas |
|---|---:|---:|
| Statements | **37,06 %** | 742 / 2002 |
| Branches | **14,73 %** | 154 / 1045 |
| Functions | **23,49 %** | 121 / 515 |
| Lines | **38,93 %** | 714 / 1834 |

> El porcentaje global es bajo porque la instrumentación incluye **todas** las páginas y hooks del proyecto, no solo los archivos bajo prueba. En las unidades efectivamente probadas la cobertura es alta (ver tabla siguiente).

### Cobertura por unidad probada

| Archivo | Sentencias | Cobertura |
|---|---:|---:|
| `components/common/BoletaDetalle.tsx` | 6 / 6 | 100 % |
| `components/common/DataTable.tsx` | 7 / 7 | 100 % |
| `components/common/ProductCard.tsx` | 12 / 12 | 100 % |
| `pages/Nosotros.tsx` | 3 / 3 | 100 % |
| `components/layout/Navbar.tsx` | 36 / 42 | 85,7 % |
| `utils/pedidos.ts` | 36 / 42 | 85,7 % |
| `hooks/useTablasMaestras.ts` | 23 / 28 | 82,1 % |
| `utils/validaciones.ts` | 31 / 49 | 63,3 % |
| `utils/formato.ts` | 11 / 20 | 55 % |
| `api/pedidos.ts` (adaptador) | 19 / 43 | 44,2 % |

---

## 6. Interpretación y próximos pasos

**Logro:** el proceso de testeo quedó configurado y funcionando, con 34 pruebas que pasan y cobertura sobre componentes, utils y capa de adaptación. Se validan tanto la lógica pura como la manipulación del DOM y los eventos de usuario.

**Mejoras sugeridas (fuera del alcance actual):**

- Agregar pruebas de los formularios de admin (`AdminProductoForm`, `AdminUsuarioForm`) y de `Checkout`.
- Elevar la cobertura global incorporando pruebas de hooks con respuestas HTTP simuladas.
- Definir umbrales mínimos de cobertura en `coverageOptions.threshold`.
- Integrar `npm test` en un pipeline de CI (GitHub Actions).

---

## 7. Archivos involucrados

```
Frontend-React/
├─ karma.conf.cjs                 ← configuración de Jasmine + Karma + cobertura
├─ vite.config.ts                 ← inyección de __API_URL__ compatible con tests
├─ src/test-mocks/
│  ├─ axios.ts
│  └─ sweetalert2.ts
├─ src/**/*.spec.ts / .spec.tsx   ← las 9 suites de pruebas
└─ coverage/                      ← reportes generados (HTML, JSON, resumen)
```
