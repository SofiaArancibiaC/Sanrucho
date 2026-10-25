import { useState } from "react";
import { Link } from "react-router-dom";
import { Container, Card, Form, Button, Table, Alert } from "react-bootstrap";
import { leerColeccion, escribirColeccion } from "../../storage/useColeccion";
import { SANRUCHO_KEYS } from "../../storage/claves";
import type { Categoria } from "../../types";
import { generarSiguienteId } from "../../data/seed";

export default function AdminCategorias() {
  const [categorias, setCategorias] = useState<Categoria[]>(() => leerColeccion<Categoria>(SANRUCHO_KEYS.categorias));
  const [nombre, setNombre] = useState("");
  const [mensaje, setMensaje] = useState("");
  const [editandoId, setEditandoId] = useState<number | null>(null);
  const [editNombre, setEditNombre] = useState("");

  const recargar = () => setCategorias(leerColeccion<Categoria>(SANRUCHO_KEYS.categorias));

  const agregar = (e: React.FormEvent) => {
    e.preventDefault();
    if (!nombre.trim()) return;
    const nuevas = [...categorias, { id: generarSiguienteId(categorias), nombre: nombre.trim() }];
    escribirColeccion(SANRUCHO_KEYS.categorias, nuevas);
    setNombre("");
    setMensaje("Categoría agregada.");
    recargar();
  };

  const eliminar = (id: number) => {
    const nuevas = categorias.filter((c) => c.id !== id);
    escribirColeccion(SANRUCHO_KEYS.categorias, nuevas);
    recargar();
  };

  const guardarEdicion = (id: number) => {
    if (!editNombre.trim()) return;
    const nuevas = categorias.map((c) => (c.id === id ? { ...c, nombre: editNombre.trim() } : c));
    escribirColeccion(SANRUCHO_KEYS.categorias, nuevas);
    setEditandoId(null);
    setEditNombre("");
    setMensaje("Categoría actualizada.");
    recargar();
  };

  return (
    <Container className="py-4">
      <h2 className="mb-3">Categorías</h2>
      {mensaje && <Alert variant="success">{mensaje}</Alert>}
      <Card className="mb-3">
        <Card.Body>
          <Form onSubmit={agregar} className="d-flex gap-2">
            <Form.Control
              placeholder="Nueva categoría"
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
            />
            <Button type="submit" className="shadow-effect">Agregar</Button>
          </Form>
        </Card.Body>
      </Card>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th style={{ width: "1%", whiteSpace: "nowrap" }}>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {categorias.map((c) => (
            <tr key={c.id}>
              <td>{c.id}</td>
              <td>
                {editandoId === c.id ? (
                  <Form.Control
                    size="sm"
                    value={editNombre}
                    onChange={(e) => setEditNombre(e.target.value)}
                  />
                ) : (
                  c.nombre
                )}
              </td>
              <td className="d-flex gap-2">
                {editandoId === c.id ? (
                  <>
                    <Button size="sm" onClick={() => guardarEdicion(c.id)}>Guardar</Button>
                    <Button size="sm" variant="secondary" onClick={() => setEditandoId(null)}>Cancelar</Button>
                  </>
                ) : (
                  <>
                    <Button size="sm" variant="warning" onClick={() => { setEditandoId(c.id); setEditNombre(c.nombre); }}>Editar</Button>
                    <Button variant="danger" size="sm" onClick={() => eliminar(c.id)}>Eliminar</Button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      <Link to="/admin" className="btn btn-outline-accent">Volver</Link>
    </Container>
  );
}