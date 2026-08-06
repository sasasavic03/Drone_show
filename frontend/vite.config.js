import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// API Gateway (8080) je tek Faza 2, pa u razvoju idemo direktno na servise.
// Kad gateway proradi: obrisi ove proxy unose, ostavi samo '/api' -> 8080
// i u .env postavi VITE_API_URL=/api. Putanje u kodu se ne menjaju.
const service = (port) => ({ target: `http://localhost:${port}`, changeOrigin: true })

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 5173,
    proxy: {
      '/auth': service(3001),
      '/users': service(3002),
      '/packages': service(3003),
      '/bookings': service(3004),
      '/media': service(3005),
    },
  },
})
