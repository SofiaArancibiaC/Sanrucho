import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), "");
  return {
    plugins: [react()],
    // Se inyecta como constante para que el mismo código funcione en Vite y en Karma
    define: {
      __API_URL__: JSON.stringify(env.VITE_API_URL || "http://localhost:9000"),
    },
    server: {
      port: 5173,
      open: true,
    },
  };
});
