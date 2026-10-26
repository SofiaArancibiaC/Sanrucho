import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Container, Card, Form, Button, Row, Col, Alert } from "react-bootstrap";
import { useAuth, esAdministrativo } from "../context/AuthContext";
import { useTablasMaestras } from "../hooks/useTablasMaestras";
import { leerColeccion, escribirColeccion } from "../storage/useColeccion";
import { SANRUCHO_KEYS } from "../storage/claves";
import type { Usuario, Pedido } from "../types";

export default function Perfil() {
  const { sesion } = useAuth();
  const navigate = useNavigate();
  const editable = true; // admins también pueden editar
  const { regiones, comunas } = useTablasMaestras();

  const [form, setForm] = useState<Usuario | null>(sesion);
  const [guardado, setGuardado] = useState(false);

  if (!sesion || !form) {
    return (
      <Container className="py-4">
        <Alert variant="warning">Debes iniciar sesión para ver tu perfil.</Alert>
      </Container>
    );
  }

  const comunasFiltradas = comunas.filter((c) => c.regionId === form.regionId);

  const misPedidos = leerColeccion<Pedido>(SANRUCHO_KEYS.pedidos).filter(
    (p) => p.cliente?.correo === sesion.correo
  );

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setForm((prev) => (prev ? { ...prev, [name]: name === "regionId" || name === "comunaId" ? Number(value) : value } as Usuario : prev));
  };

  const guardar = () => {
    const usuarios = leerColeccion<Usuario>(SANRUCHO_KEYS.usuarios);
    const actualizados = usuarios.map((u) => (u.correo === form.correo ? form : u));
    escribirColeccion(SANRUCHO_KEYS.usuarios, actualizados);
    localStorage.setItem(SANRUCHO_KEYS.sesion, JSON.stringify(form));
    setGuardado(true);
    setTimeout(() => setGuardado(false), 3000);
  };

  return (
    <Container className="py-4">
      <Card>
        <Card.Body>
          <h2 className="mb-3">Mi Perfil</h2>
          {guardado && <Alert variant="success">Datos actualizados correctamente.</Alert>}
          <Form>
            <Row className="g-3">
              <Col md={6}>
                <Form.Group>
                  <Form.Label>RUT / RUN</Form.Label>
                  <Form.Control value={form.run} disabled />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Nombre</Form.Label>
                  <Form.Control value={form.nombre} disabled />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Apellidos</Form.Label>
                  <Form.Control name="apellidos" value={form.apellidos} disabled />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Correo</Form.Label>
                  <Form.Control value={form.correo} disabled />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Fecha de nacimiento</Form.Label>
                  <Form.Control type="date" name="fechaNacimiento" value={form.fechaNacimiento} onChange={handleChange} disabled={!editable} />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Región</Form.Label>
                  <Form.Select name="regionId" value={form.regionId} onChange={handleChange} disabled={!editable}>
                    {regiones.map((r) => (
                      <option key={r.id} value={r.id}>{r.nombre}</option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Comuna</Form.Label>
                  <Form.Select name="comunaId" value={form.comunaId} onChange={handleChange} disabled={!editable}>
                    {comunasFiltradas.map((c) => (
                      <option key={c.id} value={c.id}>{c.nombre}</option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Teléfono</Form.Label>
                  <Form.Control
                    name="telefono"
                    value={form.telefono ?? ""}
                    onChange={handleChange}
                    disabled={!editable}
                    placeholder="Ej: +56 9 1234 5678"
                  />
                </Form.Group>
              </Col>
              <Col md={12}>
                <Form.Group>
                  <Form.Label>Dirección</Form.Label>
                  <Form.Control name="direccion" value={form.direccion} onChange={handleChange} disabled={!editable} />
                </Form.Group>
              </Col>
            </Row>
            {editable && (
              <div className="mt-3 d-flex gap-2">
                <Button onClick={guardar} className="shadow-effect">Guardar cambios</Button>
              </div>
            )}
          </Form>
        </Card.Body>
      </Card>

      {!esAdministrativo(sesion) && (
      <Card className="mt-4">
        <Card.Body>
          <h3 className="mb-3">Mis pedidos</h3>
          {misPedidos.length === 0 ? (
            <p className="text-muted">Aún no tienes pedidos registrados.</p>
          ) : (
            <ul className="list-group">
              {misPedidos.map((p) => (
                <li key={p.id} className="list-group-item">
                  <div className="d-flex justify-content-between align-items-center">
                    <div>
                      <strong>{p.codigoBoleta}</strong>
                      <div className="small text-muted">{p.fechaCreacion} - {p.estadoPago}</div>
                    </div>
                    <div className="d-flex align-items-center gap-2">
                      <span className="badge bg-primary rounded-pill">${p.total.toLocaleString("es-CL")}</span>
                      <button
                        className="btn btn-sm btn-outline-primary"
                        onClick={() => {
                          localStorage.setItem(SANRUCHO_KEYS.ultimoPedido, JSON.stringify(p));
                          navigate("/resumen-compra");
                        }}
                      >
                        Ver detalle
                      </button>
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </Card.Body>
      </Card>
      )}
    </Container>
  );
}