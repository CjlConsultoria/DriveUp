import { ref } from 'vue'

export const isLoggedIn = ref(!!localStorage.getItem('authToken'))

// Guardar o usuário (nome e cargo) em reativo também
export const usuarioLogado = ref<{ nome: string; role: string } | null>(null)

export function setLoggedIn(value: boolean) {
  isLoggedIn.value = value
}

export function setUsuarioLogado(usuario: { nome: string; role: string } | null) {
  usuarioLogado.value = usuario
}
