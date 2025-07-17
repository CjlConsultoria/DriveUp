import api from './api'
interface FiltroMovimentacao {
  tipo?: string // será passado como tipoMovimento
  codigo?: string // será passado como codigoProduto
  dataInicio?: string
  dataFim?: string
  page?: number
  size?: number
}

export const listarMovimentacao = async (filtro?: FiltroMovimentacao) => {
  const params: Record<string, any> = {}

  if (filtro?.tipo) params.tipoMovimento = filtro.tipo
  if (filtro?.codigo) params.codigoProduto = filtro.codigo
  if (filtro?.dataInicio) params.dataInicio = filtro.dataInicio
  if (filtro?.dataFim) params.dataFim = filtro.dataFim
  if (filtro?.page !== undefined) params.page = filtro.page
  if (filtro?.size !== undefined) params.size = filtro.size

  const response = await api.get('/movimentos', { params })
  return response.data
}

export const criarMovimentacao = async (movimentacao: any) => {
  const response = await api.post('/movimentos', movimentacao)
  return response.data
}

export interface Movimento {
  dataVenda: string
  tipo: 'ENTRADA' | 'SAIDA'
  quantidade: number
  valorVenda: number | null
  lucro: number
}

export interface ExtratoProduto {
  produtoId: number
  descricao: string
  quantidadeTotalEntrada: number
  quantidadeTotalSaida: number
  saldoAtual: number
  lucroTotal: number
  movimentos: Movimento[]
}

export const listarExtratoPorProduto = async (produtoId: number): Promise<ExtratoProduto> => {
  const response = await api.get(`/movimentos/extrato/${produtoId}`)
  return response.data
}
export const listarResumoSaidas = async (): Promise<Record<number, number>> => {
  const response = await api.get('/movimentos/saidas')
  return response.data
}
export async function buscarMovimentacao(id: number) {
  const response = await api.get(`/movimentos/extrato/movimentacao/${id}`)
  return response.data
}
