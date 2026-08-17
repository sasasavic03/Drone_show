import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 5173,
    host: '0.0.0.0',
    proxy: {
      '/auth': { target: 'http://api_gateway:8080', changeOrigin: true },
      '/users': { target: 'http://api_gateway:8080', changeOrigin: true },
      '/packages': { target: 'http://api_gateway:8080', changeOrigin: true },
      '/bookings': { target: 'http://api_gateway:8080', changeOrigin: true },
      '/media': { target: 'http://api_gateway:8080', changeOrigin: true },
    },
  },
})