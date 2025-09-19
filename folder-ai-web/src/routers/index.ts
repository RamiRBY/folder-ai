import { createRouter, createWebHistory } from "vue-router";



const routes = [
  {
    path: '/',
    component: () => import('@/views/FolderChatView.vue'),
    children: [],
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})


export default router