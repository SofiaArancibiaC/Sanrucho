// ============================================================
// pages/Login.tsx
// Reemplaza login.html. Formulario controlado que usa useAuth().
// ============================================================

import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { alertaError, alertaExito } from "../utils/alertas";
import { validarCorreo, validarPassword } from "../utils/validaciones";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const enviar = async (e: FormEvent) => {
    e.preventDefault();

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

    const resultado = login(email, password);
    if (!resultado.ok) {
      await alertaError("No se pudo iniciar sesión", resultado.mensaje);
      return;
    }

    await alertaExito("¡Bienvenido!", resultado.mensaje);
    navigate(resultado.usuario && resultado.usuario.rolId <= 2 ? "/admin" : "/");
  };

  return (
    <main className="container my-5 d-flex justify-content-center">
      <div className="login-container">
        <Link to="/" className="d-inline-block mb-3 text-decoration-none">
          ← Volver
        </Link>
        <h2 className="mb-4">Iniciar Sesión</h2>

        <form onSubmit={enviar} noValidate>
          <div className="mb-3">
            <label htmlFor="email" className="form-label">
              Correo electrónico
            </label>
            <input
              type="email"
              className="form-control"
              id="email"
              placeholder="tu@correo.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="mb-3">
            <label htmlFor="password" className="form-label">
              Contraseña
            </label>
            <input
              type="password"
              className="form-control"
              id="password"
              placeholder="********"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="btn btn-primary w-100 mb-3">
            Entrar
          </button>

          <div className="login-footer text-center">
            ¿No tienes una cuenta? <Link to="/registro">Regístrate aquí</Link>
          </div>
        </form>

        <p className="text-muted small text-center mt-3 mb-0">
          Demo: dueno@gmail.com / dueno123 · admin@gmail.com / admin123
        </p>
      </div>
    </main>
  );
}
