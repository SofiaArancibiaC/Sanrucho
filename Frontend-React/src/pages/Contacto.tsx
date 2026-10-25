// ============================================================
// pages/Contacto.tsx
// Reemplaza contacto.html + contacto.js. Al enviar, guarda el
// mensaje en la colección sanrucho_contactos.
// ============================================================

import { useState } from "react";
import type { FormEvent } from "react";
import type { Mensaje } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";
import { leerColeccion, useColeccion } from "../storage/useColeccion";
import { alertaError, alertaExito } from "../utils/alertas";
import { validarCorreo, textoRequerido } from "../utils/validaciones";

export default function Contacto() {
  const { datos: mensajes, guardar } = useColeccion<Mensaje>(SANRUCHO_KEYS.contactos);

  const [nombre, setNombre] = useState("");
  const [email, setEmail] = useState("");
  const [mensaje, setMensaje] = useState("");

  const enviar = async (e: FormEvent) => {
    e.preventDefault();

    if (!textoRequerido(nombre, 100)) {
      await alertaError("Falta tu nombre", "Por favor ingresa tu nombre.");
      return;
    }
    if (!validarCorreo(email)) {
      await alertaError("Correo inválido", "Ingresa un correo electrónico válido.");
      return;
    }
    if (!textoRequerido(mensaje)) {
      await alertaError("Falta el mensaje", "Escribe tu duda o sugerencia.");
      return;
    }

    const todos = leerColeccion<Mensaje>(SANRUCHO_KEYS.contactos);
    const nuevo: Mensaje = {
      id: todos.length === 0 ? 1 : Math.max(...todos.map((m) => m.id)) + 1,
      nombre: nombre.trim(),
      email: email.trim(),
      mensaje: mensaje.trim(),
      fecha: new Date().toISOString().slice(0, 10),
    };
    guardar([...todos, nuevo]);

    setNombre("");
    setEmail("");
    setMensaje("");
    await alertaExito("¡Mensaje enviado!", "Te responderemos lo antes posible.");
  };

  return (
    <>
      <section className="problema-hero-contacto">
        <div className="container hero-content">
          <div className="row align-items-center justify-content-center text-center text-lg-start g-4">
            <div className="col-12 col-lg-7">
              <div className="badge-sugerencia">
                <i className="bi bi-heart-fill text-danger"></i> Buzón de Mensajes &
                Soporte
              </div>
              <h1 className="titulo-contacto">
                ¿Tienes algún problema o sugerencia?
              </h1>
              <p className="subtitulo-contacto">
                Cuéntanos qué pasó o qué te gustaría ver en la web.{" "}
                <br className="d-none d-sm-inline" /> ¡Estaremos felices de leerte! ✨
              </p>
            </div>

            <div className="col-12 col-lg-5 text-center">
              <div className="mascota-wrapper">
                <img
                  src="/static/img/sanrio-contacto.png"
                  alt="Mascota de contacto"
                  className="img-banner img-fluid"
                />
              </div>
            </div>
          </div>
        </div>
      </section>

      <main className="contacto-container my-5">
        <div className="row align-items-center justify-content-center g-4">
          <div className="col-12 col-md-5 text-center">
            <div className="columna-info-contacto">
              <img
                className="img-fluid contactanos-img mb-3"
                src="/static/img/contactanos.png"
                alt="¡Contáctanos!"
              />
              <div className="d-block">
                <p className="contacto-text">
                  Al llenar este formulario, estarás contactándonos a nuestro email.
                  <br />
                  <span>Te responderemos lo antes posible.</span>
                </p>
              </div>
            </div>
          </div>

          <div className="col-12 col-md-7 col-lg-5">
            <div className="formulario-contacto shadow-sm">
              <form onSubmit={enviar} noValidate>
                <div className="mb-3 text-start">
                  <label htmlFor="nombre" className="form-label">
                    Nombre
                  </label>
                  <input
                    type="text"
                    className="form-control form-control-custom"
                    id="nombre"
                    placeholder="Tu nombre"
                    value={nombre}
                    onChange={(e) => setNombre(e.target.value)}
                    required
                  />
                </div>

                <div className="mb-3 text-start">
                  <label htmlFor="email" className="form-label">
                    Correo electrónico
                  </label>
                  <input
                    type="email"
                    className="form-control form-control-custom"
                    id="email"
                    placeholder="nombre@correo.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>

                <div className="mb-4 text-start">
                  <label htmlFor="mensaje" className="form-label">
                    Mensaje
                  </label>
                  <textarea
                    className="form-control form-control-custom"
                    id="mensaje"
                    rows={4}
                    placeholder="Escribe aquí tu duda o sugerencia..."
                    value={mensaje}
                    onChange={(e) => setMensaje(e.target.value)}
                    required
                  ></textarea>
                </div>

                <button type="submit" className="btn btn-enviar w-100">
                  <i className="bi bi-send me-2"></i>Enviar mensaje
                </button>
              </form>
            </div>
          </div>
        </div>
      </main>

      {mensajes.length > 0 && (
        <p className="text-center text-muted small mb-4">
          Gracias — llevas {mensajes.length} mensaje(s) enviado(s) en esta demo.
        </p>
      )}
    </>
  );
}
