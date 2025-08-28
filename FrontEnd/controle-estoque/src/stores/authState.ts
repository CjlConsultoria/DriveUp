import { ref } from 'vue'

export interface Role {
  id: number
  name: string
}

export interface UsuarioLogado {
  id: number
  nome: string
  cpf: string
  email: string
  empresaId?: number
  role?: Role
}

// 🔹 ao iniciar, tenta recuperar do localStorage
export const isLoggedIn = ref(!!localStorage.getItem('authToken'))
export const usuarioLogado = ref<UsuarioLogado | null>(
  JSON.parse(localStorage.getItem('usuarioLogado') || 'null')
)

export function setLoggedIn(value: boolean) {
  isLoggedIn.value = value
}

export function setUsuarioLogado(usuario: (UsuarioLogado & { roleId?: number }) | null) {
  if (usuario) {
    const roleMap: Record<number, string> = {
      1: 'ADMIN',
      2: 'ADMINISTRATIVO',
      3: 'USUARIO'
    }

    // Garante que roleId seja um número válido
    const id = usuario.roleId ?? 3
    usuario.role = { id, name: roleMap[id] || 'USUARIO' }

    localStorage.setItem('usuarioLogado', JSON.stringify(usuario))
    isLoggedIn.value = true
  } else {
    localStorage.removeItem('usuarioLogado')
    isLoggedIn.value = false
  }
  usuarioLogado.value = usuario
}

// 🔹 helper para logout
export function logout() {
  localStorage.removeItem('authToken')
  localStorage.removeItem('usuarioLogado')
  isLoggedIn.value = false
  usuarioLogado.value = null
}
