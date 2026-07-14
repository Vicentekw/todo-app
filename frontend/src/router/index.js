import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    redirect: '/todo'
  },
  {
    path: '/todo',
    name: 'Todo',
    component: () => import('@/views/Todo.vue'),
    meta: { title: '待办事项', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫：未登录访问受保护页面则跳转登录
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 待办管理系统` : '待办管理系统'
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else if (to.path === '/login' && userStore.token) {
    // 已登录则不再进入登录页
    next('/todo')
  } else {
    next()
  }
})

export default router
