# Por que se rompen las claves foraneas (FK) con una base por tabla

Este documento explica, con detalle, el problema de integridad referencial que
aparece al usar **una base de datos PostgreSQL por cada tabla** y como se
resuelve en este proyecto.

---

## 1. Que es una FK y donde vive

Una clave foranea es una restriccion que garantiza que el valor de una columna
exista en la columna referenciada de **otra tabla**:

```sql
ALTER TABLE productos
  ADD CONSTRAINT fk_categoria
  FOREIGN KEY (id_categoria)
  REFERENCES categorias (id_categoria);
```

En PostgreSQL, una FK solo puede apuntar a una tabla **de la misma base de
datos** (mismo *catalog*). El motor resuelve `categorias` usando los catalogos
del sistema de **la base a la que pertenece la conexion** (`pg_class`,
`pg_constraint`, etc.). No existe sintaxis para indicar "otra base":

```sql
-- NO es posible en PostgreSQL
REFERENCES otra_base.public.categorias(id)
```

Aunque ambas bases esten en el mismo servidor y usen el mismo usuario, para
PostgreSQL son **catalogos distintos** y una conexion solo ve una base a la vez.
Tampoco existe un mecanismo distribuido de verificacion de FK entre bases.

---

## 2. El diseno original y por que falla

Los scripts originales declaraban estas relaciones:

| Tabla | Columna | Referencia | Base de la tabla referenciada |
|-------|---------|------------|-------------------------------|
| `productos` | `id_categoria` | `categorias.id_categoria` | `categorias` |
| `pedidos` | `id_usuario` | `usuarios.id_usuario` | `usuarios` |
| `detalle_pedidos` | `id_pedido` | `pedidos.id_pedido` | `pedidos` |
| `detalle_pedidos` | `id_producto` | `productos.id_producto` | `productos` |
| `transacciones` | `id_pedido` | `pedidos.id_pedido` | `pedidos` |

Como cada script se ejecuta contra su propia base
(`psql -d productos < 04_productos.sql`), la tabla referenciada **no existe en
ese catalog**. Al ejecutar, PostgreSQL responde:

```text
ERROR:  relation "categorias" does not exist
```

o, si la tabla existe pero en otra base:

```text
ERROR:  relation "pedidos" does not exist
```

Por eso los scripts del proyecto (`04_productos.sql`, `05_pedidos.sql`,
`06_detalle_pedidos.sql`, `07_transacciones.sql`) **ya no declaran FK fisicas**.
Las columnas siguen existiendo como `INT` y se documentan como
*referencias logicas*:

```sql
-- Base de datos: productos
CREATE TABLE productos (
    ...
    id_categoria INT NOT NULL, -- referencia logica a categorias.id_categoria
    ...
    -- sin FOREIGN KEY: la tabla categorias esta en otra base de datos
);
```

---

## 3. Que se pierde exactamente al eliminar la FK

| Garantia de la FK | Sin FK fisica |
|-------------------|----------------|
| No se puede insertar un hijo con padre inexistente | Se puede: hay que validarlo en el servicio |
| No se puede borrar un padre con hijos (`ON DELETE RESTRICT`) | Se puede: el servicio debe contar hijos antes de borrar |
| Borrado en cascada (`ON DELETE CASCADE`) | No ocurre automaticamente; hay que borrar por codigo |
| Actualizacion en cascada del id | No ocurre; hay que actualizar por codigo |
| Integridad garantizada por el motor | Depende de la aplicacion (validaciones + compensaciones) |

Consecuencias concretas en este dominio:

- Un `producto` podria quedar apuntando a una `categoria` eliminada.
- Un `detalle_pedido` podria apuntar a un `pedido` inexistente si falla la
  limpieza.
- Un `detalle_pedido` podria apuntar a un `producto` inexistente si el producto
  se elimina despues de la compra (por eso se guarda `precio_unitario` como
  historico).
- No se puede hacer `JOIN` SQL directo entre `productos` y `categorias`, ni
  entre `pedidos` y `detalle_pedidos`: hay que consultar por separado y unir en
  la capa de servicio (o via Feign entre microservicios).

---

## 4. Como se mantiene la integridad en este backend

### 4.1 Validaciones en la capa de servicio

`ms-catalogo`:

```java
// ProductoService.validarCategoria()
Categoria categoria = categoriaRepository.findById(idCategoria)
        .orElseThrow(() -> new EntityNotFoundException("Categoria", "ID", idCategoria));
```

- Antes de crear/actualizar un producto se verifica que la categoria exista.
- Antes de borrar una categoria se cuenta cuantos productos la usan:

```java
long productos = productoRepository.countByIdCategoria(id);
if (productos > 0) {
    throw new ReferentialIntegrityException("Categoria", id, productos + " producto(s)...");
}
```

Esto reemplaza el `ON DELETE RESTRICT` que tendria una FK.

`ms-pedidos`:

- Antes de crear un pedido consulta cada producto en `ms-catalogo` por Feign.
- Si el producto no existe, esta inactivo o no hay stock, aborta antes de
  escribir.
- Al eliminar un pedido borra primero el detalle (`detalle_pedidos`) y luego la
  cabecera (`pedidos`), imitando el `ON DELETE CASCADE`.
- Las transiciones de estado y las compensaciones de stock evitan datos
  huerfanos cuando falla un paso distribuido.

### 4.2 Consistencia transaccional

- Cada base tiene su propio `PlatformTransactionManager`.
- `@Transactional(transactionManager = "...")` garantiza atomicidad **dentro de
  cada base**.
- No hay transaccion ACID entre bases distintas. Para el checkout se usa el
  patron **Saga / compensacion**:

```
guardar cabecera -> guardar detalle -> descontar stock
                        |                  |
                        | falla            | falla
                        v                  v
                  borrar cabecera     reponer stock ya descontado
                                      y borrar cabecera + detalle
```

- Al cancelar o borrar un pedido `Pendiente`, `reponerStock()` devuelve las
  unidades a `ms-catalogo`.

### 4.3 Verificaciones periodicas (recomendado)

Si se quisiera endurecer la consistencia, se podria agregar un job que detecte
referencias huerfanas, por ejemplo:

```sql
-- En la base productos (no puede hacer JOIN con categorias)
SELECT p.id_producto, p.id_categoria
FROM productos p
WHERE p.id_categoria NOT IN (
    -- los IDs validos se obtienen desde ms-catalogo, no por SQL cruzado
    ...
);
```

En la practica se implementa como un endpoint interno en `ms-catalogo` que
compara IDs de productos contra IDs de categorias y reporta diferencias.

---

## 5. Alternativas de diseno

| Alternativa | Ventajas | Desventajas |
|-------------|----------|-------------|
| Una base por tabla (actual) | Aislamiento por servicio, coincide con la consigna de microservicios | Sin FK fisicas; integridad a cargo del codigo |
| Una sola base con un *schema* por tabla (`productos.productos`, `categorias.categorias`) | FK fisicas entre schemas, `JOIN` directo, transacciones ACID | Menos aislamiento; un solo punto de fallo |
| Una base por microservicio (catalogo, pedidos) | Equilibrio entre aislamiento e integridad | FKs solo dentro de cada base |
| Saga / 2PC distribuido | Consistencia fuerte entre servicios | Complejidad alta; PostgreSQL no soporta 2PC entre bases |

> **Conclusion:** con una base por tabla, la FK fisica es *imposible* en
> PostgreSQL. La integridad se mantiene con validaciones de servicio,
> transacciones locales, borrado ordenado y compensaciones. El proyecto
> documenta esta decision y la implementa de forma consistente.

---

## 6. Resumen para la defensa

1. PostgreSQL no permite FK entre bases de datos: una conexion ve un solo
   catalog.
2. Al ejecutar cada script en su propia base, las tablas referenciadas no
   existen -> error `relation ... does not exist`.
3. Se eliminaron las FK fisicas y las columnas quedaron como referencias
   logicas (`id_categoria`, `id_usuario`, `id_pedido`, `id_producto`).
4. `ms-catalogo` valida categoria existente y bloquea el borrado con productos.
5. `ms-pedidos` valida producto/stock por Feign, borra detalle antes que
   cabecera y compensa stock ante fallos.
6. Si se exigiera integridad declarativa, habria que unificar las tablas en una
   sola base (o en esquemas de una misma base), sacrificando el aislamiento.
