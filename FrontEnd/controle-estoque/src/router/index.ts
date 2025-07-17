import { createRouter, createWebHistory } from 'vue-router'
import Estoque from '@/views/Estoque.vue'
import Home from '@/views/Home.vue'
import Login from '@/views/Login/Login.vue'
import { useToast } from 'vue-toastification'
import { Permissao } from '@/enums/Permissao'
import CadastroUsuario from '@/views/Cadastro/CadastroUsuario.vue'
import Cliente from '@/views/List/ClienteList.vue'
import Veiculo from '@/views/List/VeiculoList.vue'

const toast = useToast()
const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/estoque',
    name: 'Estoque',
    component: Estoque,
    meta: { requiresAuth: true },
    roles: [Permissao.ADMIN]
  },
  {
    path: '/cadastro-usuario',
    name: 'CadastroUsuario',
    component: CadastroUsuario,
    meta: { requiresAuth: true, roles: [Permissao.MECANICO] }
  },
  {
    path: '/cadastro-cliente',
    name: 'CadastroCliente',
    component: Cliente,
    meta: { requiresAuth: true, roles: [Permissao.MECANICO] }
  },
  {
    path: '/cadastro-veiculo',
    name: 'CadastroVeiculo',
    component: Veiculo,
    meta: { requiresAuth: true, roles: [Permissao.MECANICO] }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('authToken')

  if (to.meta.requiresAuth && !token) {
    toast.warning('Você precisa estar logado para acessar essa página.')
    return next({ name: 'Login' })
  }

  next()
})

export default router
