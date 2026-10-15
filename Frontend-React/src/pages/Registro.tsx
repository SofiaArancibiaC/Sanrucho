// ============================================================
// pages/Registro.tsx
// Reemplaza registro.html. Usa useAuth().registrar y las mismas
// validaciones de correo/contraseña.
// ============================================================

import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { alertaError, alertaExito } from "../utils/alertas";
import { validarCorreo, validarPassword } from "../utils/validaciones";

export default function Registro() {
  const { registrar } = useAuth();
  const navigate = useNavigate();

  const [nombre, setNombre] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const enviar = async (e: FormEvent) => {
    e.preventDefault();

    if (!nombre.trim()) {
      await alertaError("Falta tu nombre", "Por favor ingresa tu nombre.");
      return;
    }
    if (!validarCorreo(email)) {
      await alertaError(
        "Correo inválido",
        "Usa un correo @duoc.cl, @profesor.duoc.cl o @gmail.com."
      );
      return;
    }
    if (!validarPassword(password)) {
      await alertaError("Contraseña inválida", "Debe tener entre 4 y 10 caracteres.");
      return;
    }
    if (password !== confirmPassword) {
      await alertaError("Las contraseñas no coinciden", "Vuelve a escribirlas.");
      return;
    }

    const resultado = registrar({ nombre, correo: email, password });
    if (!resultado.ok) {
      await alertaError("No se pudo registrar", resultado.mensaje);
      return;
    }

    await alertaExito("¡Cuenta creada!", resultado.mensaje);
    navigate("/");
  };

  return (
    <main className="container my-5 d-flex justify-content-center">
      <div className="login-container registro-container">
        <Link to="/" className="boton-volver">
          ← Volver
        </Link>
        <h2>Crear Cuenta</h2>

        <form onSubmit={enviar} className="row g-3" noValidate>
          <div>
            <label htmlFor="nombre">Nombre completo</label>
            <input
              type="text"
              id="nombre"
              placeholder="Tu nombre"
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              required
            />
          </div>

          <div>
            <label htmlFor="email">Correo electrónico</label>
            <input
              type="email"
              id="email"
              placeholder="tu@correo.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div>
            <label htmlFor="password">Contraseña</label>
            <input
              type="password"
              id="password"
              placeholder="Entre 4 y 10 caracteres"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <div>
            <label htmlFor="confirm_password">Confirmar contraseña</label>
            <input
              type="password"
              id="confirm_password"
              placeholder="Repite tu contraseña"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit">Registrarse</button>

          <div className="login-footer">
            ¿Ya tienes una cuenta? <Link to="/login">Inicia sesión</Link>
          </div>
        </form>
      </div>
    </main>
  );
}
