import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
    plugins: [react()],
    define: {
        global: {}, // Define an empty global object for libraries relying on `global`
    },
    server: {
        // proxy: {
        //     '/ws': { // Proxy WebSocket traffic directly to the backend
        //         target: 'http://localhost:8080',
        //         ws: true, // Enable WebSocket proxying
        //         changeOrigin: true,
        //         secure: false,
        //     },
        // },
        proxy: {}
    },
});