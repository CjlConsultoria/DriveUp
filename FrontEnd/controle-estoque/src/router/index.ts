import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useToast } from 'vue-toastification'
import { usuarioLogado } from '@/stores/authState'

// Views
import Home from '@/views/Home.vue'
import Login from '@/views/Login/Login.vue'
import Estoque from '@/views/Estoque.vue'
import CadastroUsuario from '@/views/Cadastro/ClienteList.vue'
import Cliente from '@/views/List/ClientList.vue'
import Veiculo from '@/views/List/VeiculoList.vue'
import Gestao from '@/views/Oficina/GestaoOficinas.vue'

// Tipagem para o meta
interface RouteMeta {
  requiresAuth?: boolean
  roles?: string[]
}

// Rotas
const routes: Array<RouteRecordRaw & { meta?: RouteMeta }> = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  {
    path: '/estoque',
    name: 'Estoque',
    component: Estoque,
    meta: { requiresAuth: true, roles: ['USUARIO'] }
  },
  {
    path: '/cadastro-usuario',
    name: 'CadastroUsuario',
    component: CadastroUsuario,
    meta: { requiresAuth: true, roles: ['ADMINISTRATIVO'] }
  },
  {
    path: '/cadastro-cliente',
    name: 'CadastroCliente',
    component: Cliente,
    meta: { requiresAuth: true, roles: ['USUARIO'] }
  },
  {
    path: '/cadastro-veiculo',
    name: 'CadastroVeiculo',
    component: Veiculo,
    meta: { requiresAuth: true, roles: ['USUARIO'] }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: Gestao,
    meta: { requiresAuth: true, roles: ['ADMIN'] }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// Guard global
router.beforeEach((to, from, next) => {
  const toast = useToast()
  const token = localStorage.getItem('authToken')

  // Recupera meta tipado
  const meta = (to.meta || {}) as RouteMeta

  // Verifica autenticação
  if (meta.requiresAuth && !token) {
    toast.warning('Você precisa estar logado para acessar essa página.')
    return next({ name: 'Login' })
  }

  if (meta.roles && meta.roles.length > 0) {
    const userRole = usuarioLogado.value?.role?.name
    if (userRole !== 'ADMIN' && (!userRole || !meta.roles.includes(userRole))) {
      toast.error('Você não tem permissão para acessar esta página.')
      return next({ name: 'Home' })
    }
  }

  next()
})

export default router
