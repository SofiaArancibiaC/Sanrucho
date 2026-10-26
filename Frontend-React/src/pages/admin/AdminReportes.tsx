import { Container, Card, Row, Col, Button, Table } from "react-bootstrap";
import jsPDF from "jspdf";
import html2canvas from "html2canvas";
import { useRef } from "react";
import { leerColeccion } from "../../storage/useColeccion";
import { SANRUCHO_KEYS } from "../../storage/claves";
import type { Producto, Categoria, Usuario, Pedido } from "../../types";

export default function AdminReportes() {
  const chartRef = useRef<HTMLDivElement>(null);
  const usuarios = leerColeccion<Usuario>(SANRUCHO_KEYS.usuarios);
  const productos = leerColeccion<Producto>(SANRUCHO_KEYS.productos);
  const categorias = leerColeccion<Categoria>(SANRUCHO_KEYS.categorias);

  const clientesRegistrados = usuarios.filter((u) => u.rolId === 3).length;
  const porCategoria = categorias.map((c) => ({
    nombre: c.nombre,
    count: productos.filter((p) => p.categoriaId === c.id).length,
  }));
  const stockCritico = productos.filter((p) => p.stock <= (p.stockCritico ?? 0));
  const totalProductos = productos.length;
  const stockTotal = productos.reduce((a, p) => a + (p.stock ?? 0), 0);
  const pedidosLista = leerColeccion<Pedido>(SANRUCHO_KEYS.pedidos);
  const pedidos = pedidosLista.length;
  const ganancias = pedidosLista
    .filter((p) => p.estadoPago === "aprobado")
    .reduce((a, p) => a + (p.total ?? 0), 0);

  // Ganancias agrupadas por mes (YYYY-MM), incluyendo los últimos 6 meses aunque tengan 0
  const gananciasPorMesMap = new Map<string, number>();
  const hoy = new Date();
  for (let i = 5; i >= 0; i--) {
    const d = new Date(hoy.getFullYear(), hoy.getMonth() - i, 1);
    const mes = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}`;
    gananciasPorMesMap.set(mes, 0);
  }
  pedidosLista
    .filter((p) => p.estadoPago === "aprobado")
    .forEach((p) => {
      const mes = (p.fechaCreacion || "").slice(0, 7);
      if (mes && gananciasPorMesMap.has(mes)) {
        gananciasPorMesMap.set(mes, (gananciasPorMesMap.get(mes) ?? 0) + (p.total ?? 0));
      } else if (mes) {
        gananciasPorMesMap.set(mes, (gananciasPorMesMap.get(mes) ?? 0) + (p.total ?? 0));
      }
    });
  const gananciasPorMes = Array.from(gananciasPorMesMap.entries()).sort();
  const maxGananciaMes = Math.max(...gananciasPorMes.map(([, v]) => v), 1);

  const metricas = [
    { nombre: "Clientes registrados", valor: clientesRegistrados },
    { nombre: "Productos", valor: totalProductos },
    { nombre: "Stock total", valor: stockTotal },
    { nombre: "Pedidos", valor: pedidos },
  ];
  const maxMetrica = Math.max(...metricas.map((m) => m.valor), 1);

  const exportarCSV = () => {
    const filas = [
      ["Métrica", "Valor"],
      ["Clientes registrados", clientesRegistrados],
      ["Productos", totalProductos],
      ["Stock total", stockTotal],
      ["Pedidos", pedidos],
    ["Ganancias", ganancias],
    ];
    const csv = filas.map((f) => f.join(";")).join("\n");
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "reporte_sanrucho.csv";
    a.click();
    URL.revokeObjectURL(url);
  };

  const exportarPDF = async () => {
    const doc = new jsPDF();
    let y = 10;
    doc.setFontSize(18);
    doc.text("Reporte Sanrucho", 105, y, { align: "center" });
    y += 10;
    doc.setFontSize(12);
    doc.text(`Clientes registrados: ${clientesRegistrados}`, 10, y); y += 8;
    doc.text(`Productos: ${totalProductos}`, 10, y); y += 8;
    doc.text(`Stock total: ${stockTotal}`, 10, y); y += 8;
    doc.text(`Pedidos: ${pedidos}`, 10, y); y += 8;
    doc.text(`Ganancias: ${ganancias}`, 10, y); y += 12;
    doc.text("Productos por categoría:", 10, y); y += 8;
    porCategoria.forEach((c) => {
      doc.text(`${c.nombre}: ${c.count}`, 10, y); y += 7;
    });
    y += 5;
    doc.text("Stock crítico:", 10, y); y += 8;
    if (stockCritico.length === 0) {
      doc.text("No hay productos con stock crítico.", 10, y); y += 7;
    } else {
      stockCritico.forEach((p) => {
        doc.text(`${p.nombre} - stock: ${p.stock} (crítico: ${p.stockCritico})`, 10, y); y += 7;
      });
    }
    if (chartRef.current) {
    const canvas = await html2canvas(chartRef.current);
    const imgData = canvas.toDataURL("image/png");
    const imgWidth = 180;
    const imgHeight = (canvas.height * imgWidth) / canvas.width;
    doc.addPage();
    doc.text("Ganancias por mes:", 10, 10);
    doc.addImage(imgData, "PNG", 10, 20, imgWidth, imgHeight);
  }
  doc.save("reporte_sanrucho.pdf");
  };

  return (
    <Container className="py-4">
      <h2 className="mb-3">Reportes</h2>
      <Row className="g-3 mb-4">
        <Col md={3}><Card body><strong>Clientes registrados</strong><div className="fs-4">{clientesRegistrados}</div></Card></Col>
        <Col md={3}><Card body><strong>Productos</strong><div className="fs-4">{totalProductos}</div></Card></Col>
        <Col md={3}><Card body><strong>Stock total</strong><div className="fs-4">{stockTotal}</div></Card></Col>
        <Col md={3}><Card body><strong>Pedidos</strong><div className="fs-4">{pedidos}</div></Card></Col>
        <Col md={3}><Card body><strong>Ganancias</strong><div className="fs-4">${ganancias.toLocaleString("es-CL")}</div></Card></Col>
      </Row>

      <Card className="mb-4">
        <Card.Body>
          <h5>Métricas generales</h5>
          {metricas.map((m) => (
            <div key={m.nombre} className="mb-2">
              <div className="d-flex justify-content-between">
                <span>{m.nombre}</span>
                <span>{m.valor}</span>
              </div>
              <div style={{ background: "#eee", height: 12, borderRadius: 6 }}>
                <div
                  style={{
                    width: `${(m.valor / maxMetrica) * 100}%`,
                    height: "100%",
                    background: "var(--primary)",
                    borderRadius: 6,
                  }}
                />
              </div>
            </div>
          ))}
        </Card.Body>
      </Card>

      <Card className="mb-4">
        <Card.Body>
          <h5>Productos por categoría</h5>
          <Table striped bordered hover size="sm">
            <thead>
              <tr><th>Categoría</th><th>Cantidad</th></tr>
            </thead>
            <tbody>
              {porCategoria.map((c) => (
                <tr key={c.nombre}>
                  <td>{c.nombre}</td>
                  <td>{c.count}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Card.Body>
      </Card>

      <Card className="mb-4">
        <Card.Body>
          <h5>Stock crítico</h5>
          {stockCritico.length === 0 ? (
            <p className="text-muted">No hay productos con stock crítico.</p>
          ) : (
            <Table striped bordered hover size="sm">
              <thead>
                <tr><th>Producto</th><th>Stock</th><th>Stock crítico</th></tr>
              </thead>
              <tbody>
                {stockCritico.map((p) => (
                  <tr key={p.codigo}>
                    <td>{p.nombre}</td>
                    <td>{p.stock}</td>
                    <td>{p.stockCritico}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}
        </Card.Body>
      </Card>

      <Card className="mb-4">
        <Card.Body>
          <h5>Ganancias por mes</h5>
          {gananciasPorMes.length === 0 ? (
            <p className="text-muted">No hay ganancias registradas.</p>
          ) : (
            <div ref={chartRef} style={{ display: "flex", alignItems: "flex-end", gap: 12, height: 220, padding: "10px 0" }}>
              {gananciasPorMes.map(([mes, valor]) => (
                <div key={mes} style={{ flex: 1, textAlign: "center" }}>
                  <div
                    title={`$${valor.toLocaleString("es-CL")}`}
                    style={{
                      height: `${(valor / maxGananciaMes) * 180}px`,
                      background: "var(--primary)",
                      borderRadius: "6px 6px 0 0",
                      minHeight: 4,
                    }}
                  />
                  <div style={{ fontSize: "0.75rem", marginTop: 6 }}>{mes}</div>
                  <div style={{ fontSize: "0.7rem", color: "var(--text-soft)" }}>${valor.toLocaleString("es-CL")}</div>
                </div>
              ))}
            </div>
          )}
        </Card.Body>
      </Card>

      <div className="d-flex gap-2">
        <Button onClick={exportarCSV} className="shadow-effect">Descargar CSV (Excel)</Button>
        <Button onClick={exportarPDF} variant="outline-secondary">Imprimir / PDF</Button>
      </div>
    </Container>
  );
}