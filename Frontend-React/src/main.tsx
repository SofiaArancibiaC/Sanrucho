// ============================================================
// src/main.tsx
// Punto de entrada. Aquí se cargan los estilos globales, las
// librerías (Bootstrap CSS/JS e iconos) y se monta <App />.
// ============================================================

import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import "./estilos.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";

import App from "./App";
import { inicializarDatos } from "./data/seed";

// Se asegura de que localStorage tenga los datos de prueba ANTES
// de que cualquier componente los lea.
inicializarDatos();

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </StrictMode>
);
