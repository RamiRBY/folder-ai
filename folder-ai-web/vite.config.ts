import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      // Keep your existing '@' alias
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      // Add the new 'vue' alias to enable the runtime template compiler
      'vue': 'vue/dist/vue.esm-bundler.js',
    },
  },
  // --- NEW: Add this server proxy configuration ---
  server: {
    proxy: {
      // Proxy any request that starts with /api to our Spring Boot backend
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
