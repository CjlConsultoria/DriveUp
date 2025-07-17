import api from './api'
import type { UsuarioRequest } from '@/types/UsuarioRequest'

export const login = async (username: string, password: string) => {
  const response = await api.post('/auth/login', { username, password })
  const token = response.data.token
  localStorage.setItem('authToken', token)
  api.defaults.headers['Authorization'] = `Bearer ${token}`
  return token
}

export const logout = () => {
  localStorage.removeItem('authToken')
  delete api.defaults.headers['Authorization']
}
export const cadastrarUsuario = async (usuario: UsuarioRequest) => {
  return await api.post('/usuarios', usuario)
}

export const buscarUsuarioLogado = async () => {
  const response = await api.get('/auth/me')
  return response.data
}
