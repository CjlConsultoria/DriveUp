import api from './api' // seu axios configurado

export interface Veiculo {
  id: string
  clienteId: string
  placa: string
  marca: string
  modelo: string
  ano: number
  cor: string
  quilometragem: number
}

// Cadastro de veículo (sem id)
export async function cadastrarVeiculo(veiculo: Omit<Veiculo, 'id'>): Promise<void> {
  await api.post('/veiculos', veiculo)
}

// Listar veículos com filtros, paginação e ordenação
export async function listarVeiculos(params?: {
  placa?: string
  modelo?: string
  cpfCnpj?: string
  page?: number
  size?: number
  sort?: string
}): Promise<{
  content: Veiculo[]
  totalElements: number
  totalPages: number
  number: number
}> {
  const response = await api.get('/veiculos', { params })
  return response.data
}

// Excluir veículo por id
export async function excluirVeiculo(id: string | number): Promise<void> {
  await api.delete(`/veiculos/${id}`)
}
