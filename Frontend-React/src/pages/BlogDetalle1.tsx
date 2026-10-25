// ============================================================
// pages/BlogDetalle1.tsx
// Reemplaza blog-detalle-1.html. La barra de progreso de lectura
// se calcula escuchando el scroll con useEffect.
// ============================================================

import { useEffect, useRef } from "react";
import { Link } from "react-router-dom";

export default function BlogDetalle1() {
  const barraRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const actualizar = () => {
      const barra = barraRef.current;
      if (!barra) return;
      const altoTotal = document.documentElement.scrollHeight - window.innerHeight;
      const progreso =
        altoTotal <= 0 ? 100 : Math.round((window.scrollY / altoTotal) * 100);
      barra.style.width = progreso + "%";
    };
    window.addEventListener("scroll", actualizar);
    actualizar();
    return () => window.removeEventListener("scroll", actualizar);
  }, []);

  return (
    <>
      <div className="reading-progress" ref={barraRef}></div>

      <article className="container blog-post py-5" style={{ maxWidth: 800 }}>
        <img
          src="/static/img/logo-sanrucho.png"
          className="img-fluid rounded mb-4"
          alt="logo sanrucho"
        />
        <p className="text-muted small mb-1">05 de Septiembre, 2026</p>
        <h1 className="mb-4">Bienvenidos a Sanrucho</h1>
        <p>
          Despues de meses de trabajo, hoy nace <strong>Sanrucho</strong>, una nueva
          tienda online pensada por y para la comodidad de nuestros clientes.
          Nuestro objetivo es simple: ofrecer un catalogo variado de figuras,
          productos, maquillaje, accesorios, con una experiencia de compra rapida,
          clara y facil.
        </p>
        <p>
          Desde el primer momento contaras con un catalogo organizado por
          categorias, fichas de producto con toda la informacion que necesitas, y un
          carrito de compras que recuerda tus productos aunque cierres el navegador.
        </p>
        <p>
          Este es solo el comienzo. En las proximas semanas seguiremos sumando
          promociones y sorpresas que les encantara. Gracias por ser parte de este
          lanzamiento.
        </p>
        <Link to="/blog" className="btn btn-accent mt-3">
          &larr; Volver al Blog
        </Link>
      </article>
    </>
  );
}
