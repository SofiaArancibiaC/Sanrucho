// ============================================================
// pages/BlogDetalle2.tsx
// Reemplaza blog-detalle-2.html. Misma barra de progreso que el
// detalle 1, pero con el contenido de la guía de compra.
// ============================================================

import { useEffect, useRef } from "react";
import { Link } from "react-router-dom";

export default function BlogDetalle2() {
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
        <p className="text-muted small mb-1">04 de Septiembre, 2026</p>
        <h1 className="mb-4">
          Cómo crear tu cuenta y hacer tu primer pedido en Sanrucho
        </h1>
        <p>
          Ya abrimos las puertas y queremos que tu primera experiencia sea
          excelente. En esta guía te contamos paso a paso cómo crear tu cuenta,
          recorrer el catálogo y completar tu primer pedido sin complicaciones.
        </p>
        <p>
          Lo primero es <strong>crear tu cuenta</strong>: solo necesitas un email,
          un nombre de usuario y una contraseña. En menos de un minuto ya formas
          parte de Sanrucho. Tu perfil guardará tus datos de envío, tu historial de
          pedidos y tus favoritos, para que todo esté a un clic.
        </p>
        <p>
          Después, recorre el <strong>catálogo</strong>: estará organizado por
          categorías — accesorios, figuras, maquillaje, ropa y más — y cada producto
          tendrá una ficha completa con descripción y precio. Usa el buscador o los
          filtros para llegar directo a lo que buscas.
        </p>
        <p>
          Cuando encuentres un producto, <strong>añádelo al carrito</strong>. Lo
          mejor de todo: tu carrito se guarda automáticamente aunque cierres el
          navegador, así puedes armar tu compra con calma y volver cuando quieras.
        </p>
        <p>
          El último paso es el <strong>checkout</strong>: confirma tus datos y el
          método de pago, y listo. Recibirás la confirmación de tu pedido al instante
          y podrás seguir su estado desde tu perfil. Si tienes dudas, nuestro equipo
          de soporte está a un mensaje de distancia.
        </p>
        <Link to="/blog" className="btn btn-accent mt-3">
          &larr; Volver al Blog
        </Link>
      </article>
    </>
  );
}
