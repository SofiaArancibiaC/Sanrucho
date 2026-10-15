// ============================================================
// pages/admin/AdminUsuarioForm.tsx
// Reemplaza admin-usuario-form.html + inicializarFormularioUsuarioAdmin.
// Si la URL trae "?run=", pasa a modo edición. Al cambiar la región
// se recargan las comunas (useTablasMaestras.comunasDeRegion).
// ============================================================

import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { useUsuarios } from "../../hooks/useUsuarios";
import { useTablasMaestras } from "../../hooks/useTablasMaestras";
import type { Usuario } from "../../types";
import { alertaError, alertaExito } from "../../utils/alertas";
import { textoRequerido, validarCorreo, validarPassword, validarRun } from "../../utils/validaciones";

export default function AdminUsuarioForm() {
  const [parametros] = useSearchParams();
  const runOriginal = parametros.get("run") ?? "";
  const { datos, guardarUsuario } = useUsuarios();
  const { roles, regiones, comunasDeRegion } = useTablasMaestras();
  const navigate = useNavigate();

  const enEdicion = runOriginal !== "";
  const existente = datos.find((u) => u.run === runOriginal);

  const [run, setRun] = useState(existente?.run ?? "");
  const [nombre, setNombre] = useState(existente?.nombre ?? "");
  const [apellidos, setApellidos] = useState(existente?.apellidos ?? "");
  const [correo, setCorreo] = useState(existente?.correo ?? "");
  const [password, setPassword] = useState(existente?.password ?? "");
  const [fechaNacimiento, setFechaNacimiento] = useState(existente?.fechaNacimiento ?? "");
  const [rolId, setRolId] = useState(existente ? String(existente.rolId) : "");
  const [regionId, setRegionId] = useState(existente ? String(existente.regionId) : "");
  const [comunaId, setComunaId] = useState(existente ? String(existente.comunaId) : "");
  const [direccion, setDireccion] = useState(existente?.direccion ?? "");
  const [estado, setEstado] = useState<Usuario["estado"]>(existente?.estado ?? "Activo");

  const comunas = regionId ? comunasDeRegion(Number(regionId)) : [];

  const cambiarRegion = (valor: string) => {
    setRegionId(valor);
    setComunaId("");
  };

  const guardar = async (e: FormEvent) => {
    e.preventDefault();

    const runLimpio = run.toUpperCase().trim();
    if (!validarRun(runLimpio)) {
      await alertaError("RUN inválido", "Revisa el RUN (sin puntos ni guion).");
      return;
    }
    const duplicado = datos.some((u) => u.run === runLimpio && u.run !== runOriginal);
    if (duplicado) {
      await alertaError("RUN duplicado", "Ya existe un usuario con ese RUN.");
      return;
    }
    if (!textoRequerido(nombre, 50)) {
      await alertaError("Nombre inválido", "Obligatorio (máx. 50).");
      return;
    }
    if (!textoRequerido(apellidos, 100)) {
      await alertaError("Apellidos inválidos", "Obligatorio (máx. 100).");
      return;
    }
    if (!validarCorreo(correo)) {
      await alertaError("Correo inválido", "Solo @duoc.cl, @profesor.duoc.cl o @gmail.com.");
      return;
    }
    if (!validarPassword(password)) {
      await alertaError("Contraseña inválida", "Entre 4 y 10 caracteres.");
      return;
    }
    if (!rolId) {
      await alertaError("Falta el rol", "Selecciona un tipo de usuario.");
      return;
    }
    if (!regionId) {
      await alertaError("Falta la región", "Selecciona una región.");
      return;
    }
    if (!comunaId) {
      await alertaError("Falta la comuna", "Selecciona una comuna.");
      return;
    }
    if (!textoRequerido(direccion, 300)) {
      await alertaError("Dirección inválida", "Obligatoria (máx. 300).");
      return;
    }

    const usuario: Usuario = {
      run: runLimpio,
      nombre: nombre.trim(),
      apellidos: apellidos.trim(),
      correo: correo.trim(),
      password,
      fechaNacimiento: fechaNacimiento || "",
      rolId: Number(rolId),
      regionId: Number(regionId),
      comunaId: Number(comunaId),
      direccion: direccion.trim(),
      estado,
    };

    guardarUsuario(usuario);
    await alertaExito("Correcto", "Usuario guardado correctamente.");
    navigate("/admin/usuarios");
  };

  return (
    <>
      <h1 className="fw-bold mb-4" style={{ color: "var(--primary)" }}>
        {enEdicion ? "Editar usuario" : "Nuevo usuario"}
      </h1>

      <form onSubmit={guardar} className="card p-4" noValidate>
        <div className="row g-3">
          <div className="col-md-4">
            <label htmlFor="runUsuario" className="form-label">
              RUN (sin puntos ni guion)
            </label>
            <input
              type="text"
              className="form-control form-control-custom"
              id="runUsuario"
              placeholder="Ej: 123456785"
              maxLength={9}
              value={run}
              readOnly={enEdicion}
              onChange={(e) => setRun(e.target.value)}
            />
          </div>
          <div className="col-md-4">
            <label htmlFor="nombreUsuario" className="form-label">
              Nombre
            </label>
            <input
              type="text"
              className="form-control form-control-custom"
              id="nombreUsuario"
              maxLength={50}
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
            />
          </div>
          <div className="col-md-4">
            <label htmlFor="apellidosUsuario" className="form-label">
              Apellidos
            </label>
            <input
              type="text"
              className="form-control form-control-custom"
              id="apellidosUsuario"
              maxLength={100}
              value={apellidos}
              onChange={(e) => setApellidos(e.target.value)}
            />
          </div>
          <div className="col-md-6">
            <label htmlFor="correoUsuario" className="form-label">
              Correo
            </label>
            <input
              type="email"
              className="form-control form-control-custom"
              id="correoUsuario"
              maxLength={100}
              value={correo}
              onChange={(e) => setCorreo(e.target.value)}
            />
          </div>
          <div className="col-md-6">
            <label htmlFor="passwordUsuario" className="form-label">
              Contraseña
            </label>
            <input
              type="text"
              className="form-control form-control-custom"
              id="passwordUsuario"
              maxLength={10}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          <div className="col-md-4">
            <label htmlFor="fechaNacimientoUsuario" className="form-label">
              Fecha de nacimiento
            </label>
            <input
              type="date"
              className="form-control form-control-custom"
              id="fechaNacimientoUsuario"
              value={fechaNacimiento}
              onChange={(e) => setFechaNacimiento(e.target.value)}
            />
          </div>
          <div className="col-md-4">
            <label htmlFor="rolUsuario" className="form-label">
              Tipo de usuario
            </label>
            <select
              className="form-select form-control-custom"
              id="rolUsuario"
              value={rolId}
              onChange={(e) => setRolId(e.target.value)}
            >
              <option value="">Seleccione un tipo de usuario</option>
              {roles.map((r) => (
                <option key={r.id} value={r.id}>
                  {r.nombre}
                </option>
              ))}
            </select>
          </div>
          <div className="col-md-4">
            <label htmlFor="estadoUsuario" className="form-label">
              Estado
            </label>
            <select
              className="form-select form-control-custom"
              id="estadoUsuario"
              value={estado}
              onChange={(e) => setEstado(e.target.value as Usuario["estado"])}
            >
              <option value="Activo">Activo</option>
              <option value="Inactivo">Inactivo</option>
            </select>
          </div>
          <div className="col-md-6">
            <label htmlFor="regionUsuario" className="form-label">
              Región
            </label>
            <select
              className="form-select form-control-custom"
              id="regionUsuario"
              value={regionId}
              onChange={(e) => cambiarRegion(e.target.value)}
            >
              <option value="">Seleccione una región</option>
              {regiones.map((r) => (
                <option key={r.id} value={r.id}>
                  {r.nombre}
                </option>
              ))}
            </select>
          </div>
          <div className="col-md-6">
            <label htmlFor="comunaUsuario" className="form-label">
              Comuna
            </label>
            <select
              className="form-select form-control-custom"
              id="comunaUsuario"
              value={comunaId}
              onChange={(e) => setComunaId(e.target.value)}
              disabled={!regionId}
            >
              <option value="">
                {regionId ? "Seleccione una comuna" : "Seleccione una región primero"}
              </option>
              {comunas.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.nombre}
                </option>
              ))}
            </select>
          </div>
          <div className="col-12">
            <label htmlFor="direccionUsuario" className="form-label">
              Dirección
            </label>
            <input
              type="text"
              className="form-control form-control-custom"
              id="direccionUsuario"
              maxLength={300}
              value={direccion}
              onChange={(e) => setDireccion(e.target.value)}
            />
          </div>
        </div>
        <div className="d-flex gap-2 mt-4">
          <button type="submit" className="btn-accent">
            Guardar
          </button>
          <Link to="/admin/usuarios" className="btn btn-outline-accent">
            Cancelar
          </Link>
        </div>
      </form>
    </>
  );
}
