import { createApp } from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia'
import router from './routers'
import i18n from './locales'
import './assets/tailwind.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(i18n)
app.mount('#app')
