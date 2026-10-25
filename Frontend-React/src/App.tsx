// ============================================================
// src/App.tsx
// Enrutador principal. Es el reemplazo de las rutas de app.py.
// - Layout (Navbar + Footer) para las páginas públicas.
// - RutaProtegida + AdminLayout (Sidebar/Offcanvas) para /admin/*.
// ============================================================

import { Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import { CartProvider } from "./context/CartContext";
import Layout from "./components/layout/Layout";
import AdminLayout from "./components/layout/AdminLayout";
import RutaProtegida from "./components/common/RutaProtegida";

import Home from "./pages/Home";
import Productos from "./pages/Productos";
import ProductoDetalle from "./pages/ProductoDetalle";
import Carrito from "./pages/Carrito";
import Login from "./pages/Login";
import Registro from "./pages/Registro";
import Nosotros from "./pages/Nosotros";
import Blog from "./pages/Blog";
import BlogDetalle1 from "./pages/BlogDetalle1";
import BlogDetalle2 from "./pages/BlogDetalle2";
import Contacto from "./pages/Contacto";

import Ofertas from "./pages/Ofertas";
import Checkout from "./pages/Checkout";
import PagoExito from "./pages/PagoExito";
import PagoError from "./pages/PagoError";
import ResumenCompra from "./pages/ResumenCompra";

import AdminDashboard from "./pages/admin/AdminDashboard";
import AdminProductos from "./pages/admin/AdminProductos";
import AdminProductoForm from "./pages/admin/AdminProductoForm";
import AdminUsuarios from "./pages/admin/AdminUsuarios";
import AdminUsuarioForm from "./pages/admin/AdminUsuarioForm";

export default function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <Routes>
          {/* ---------- Sitio público ---------- */}
          <Route element={<Layout />}>
            <Route path="/" element={<Home />} />
            <Route path="/productos" element={<Productos />} />
            <Route path="/ofertas" element={<Ofertas />} />
            <Route path="/producto-detalle" element={<ProductoDetalle />} />
            <Route path="/carrito" element={<Carrito />} />
            <Route path="/checkout" element={<Checkout />} />
            <Route path="/pago/exito" element={<PagoExito />} />
            <Route path="/pago/error" element={<PagoError />} />
            <Route path="/resumen-compra" element={<ResumenCompra />} />
            <Route path="/login" element={<Login />} />
            <Route path="/registro" element={<Registro />} />
            <Route path="/nosotros" element={<Nosotros />} />
            <Route path="/blog" element={<Blog />} />
            <Route path="/blog/detalle-1" element={<BlogDetalle1 />} />
            <Route path="/blog/detalle-2" element={<BlogDetalle2 />} />
            <Route path="/contacto" element={<Contacto />} />
          </Route>

          {/* ---------- Panel administrativo (solo Dueño=1 y Admin=2) ---------- */}
          <Route element={<RutaProtegida rolesPermitidos={[1, 2]} />}>
            <Route element={<AdminLayout />}>
              <Route path="/admin" element={<AdminDashboard />} />
              <Route path="/admin/productos" element={<AdminProductos />} />
              <Route path="/admin/productos/form" element={<AdminProductoForm />} />
              <Route path="/admin/usuarios" element={<AdminUsuarios />} />
              <Route path="/admin/usuarios/form" element={<AdminUsuarioForm />} />
            </Route>
          </Route>

          {/* Cualquier ruta desconocida vuelve al inicio. */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </CartProvider>
    </AuthProvider>
  );
}
