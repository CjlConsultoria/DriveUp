import api from './api'

export interface Cliente {
  id: number
  nome: string
  cpfCnpj: string
  email?: string
  telefone?: string
  endereco?: object
}

export interface ClientePage {
  content: Cliente[]
  totalPages: number
  totalElements: number
}

export async function listarClientes(params: {
  nome?: string
  email?: string
  cpfCnpj?: string
  page?: number
  size?: number
  sort?: string
}): Promise<ClientePage> {
  const response = await api.get('/clientes', { params })
  return response.data
}

export const cadastrarCliente = async (cliente: Omit<Cliente, 'id'>): Promise<void> => {
  await api.post('/clientes', cliente)
}

export const excluirCliente = async (id: string): Promise<void> => {
  await api.delete(`/clientes/${id}`)
}
