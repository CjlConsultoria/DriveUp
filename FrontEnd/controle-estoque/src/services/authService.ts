import api from './api'
import type { UsuarioRequest } from '@/types/UsuarioRequest'
import type { UsuarioDTO } from '@/types/UsuarioDTO' // crie uma tipagem para o retorno
import { usuarioLogado } from '@/stores/authState'

export const login = async (cpf: string, password: string) => {
  const response = await api.post('/auth/login', { cpf, password })
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

// 🔹 Nova função para listar usuários
export const listarUsuarios = async (empresaId: number, filtro?: string): Promise<UsuarioDTO[]> => {
  const response = await api.get(`/usuarios/empresa/${empresaId}`, {
    params: { filtro }
  })
  return response.data
}

// 🔹 Deletar usuário pelo ID
export const deletarUsuario = async (cpf: string): Promise<void> => {
  const empresaId = usuarioLogado.value?.empresaId
  if (!empresaId) throw new Error('Empresa do usuário logado não encontrada.')

  await api.delete('/usuarios', {
    params: {
      cpf,
      empresaId
    }
  })
}

// Buscar usuário por CPF + empresaId
export const buscarUsuarioPorCpf = async (cpf: string, empresaId: number) => {
  const response = await api.get(`/usuarios/cpf/${cpf}/empresa/${empresaId}`)
  return response.data
}
