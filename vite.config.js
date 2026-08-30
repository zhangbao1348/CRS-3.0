import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (!id.includes('node_modules')) return undefined
          if (id.includes('@ant-design/plots') || id.includes('@antv/')) return 'vendor-charts'
          if (id.includes('@wangeditor/') || id.includes('slate')) return 'vendor-editor'
          if (id.includes('/react/') || id.includes('/react-dom/') || id.includes('/react-router') || id.includes('/scheduler/')) return 'vendor-react'
          if (id.includes('/antd/') || id.includes('@ant-design/icons') || id.includes('/rc-')) return 'vendor-antd'
          return 'vendor-common'
        }
      }
    }
  },
  server: {
    port: 3001,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path
      }
    }
  }
})
