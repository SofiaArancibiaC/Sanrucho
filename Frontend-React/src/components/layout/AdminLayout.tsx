// ============================================================
// components/layout/AdminLayout.tsx
// Reemplaza admin.html: barra lateral fija en escritorio +
// offcanvas en móvil, y <Outlet/> para las vistas hijas.
// ============================================================

import { Outlet } from "react-router-dom";
import AdminSidebar from "./AdminSidebar";
import AdminOffcanvas from "./AdminOffcanvas";

export default function AdminLayout() {
  return (
    <div className="admin-body">
      <AdminOffcanvas />
      <div className="container-fluid">
        <div className="row">
          <aside className="col-lg-2 d-none d-lg-block bg-white border-end min-vh-100 p-3">
            <AdminSidebar />
          </aside>
          <main className="col-lg-10 p-4">
            <Outlet />
          </main>
        </div>
      </div>
    </div>
  );
}
