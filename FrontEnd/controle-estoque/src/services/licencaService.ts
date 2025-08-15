import api from './api'

// Tipos
export interface LicencaDTO {
  id?: number // se enviado, atualiza a licença existente
  empresaId?: number // se não enviar, cria nova empresa
  empresaNome: string // necessário se criar nova empresa
  empresaCnpj: string // mesmo que vazio, enviar
  telefone: string // telefone da empresa
  dataInicio: string // início da licença
  dataFim: string // fim da licença
  tipo: string // código ou nome da licença
  limiteUsuarios: number
  ativa?: boolean // opcional, default true
}

export interface LicencaStatusDTO {
  valida: boolean
  expirada: boolean
  diasRestantes: number
  excedeu: boolean
}

export interface LicencaDetalhadaDTO {
  id: number
  empresaId: number
  empresaNome: string
  empresaCnpj: string
  dataInicio: string
  dataFim: string
  ativa: boolean
  tipo: string
  limiteUsuarios: number
  responsavelId?: string
  responsavelNome?: string
  responsavelCpf?: string
  perfil?: string
  validadeExpirada: boolean
  diasRestantes: number
  qtdUsuarios?: number
}

export interface TipoLicenca {
  id: number
  nome: string
  codigo: string
  limiteUsuarios: number
  descricao?: string
}

// Service
export async function listarTiposLicenca(): Promise<TipoLicenca[]> {
  const response = await api.get('/licencas/all')
  return response.data
}

export async function listarLicencas(params?: {
  empresaNome?: string
  usuarioNome?: string
  cpf?: string
  page?: number
  size?: number
  sort?: string
}): Promise<{ content: LicencaDetalhadaDTO[]; totalPages: number; totalElements: number }> {
  const response = await api.get('/licencas/search', { params })
  return response.data
}

// Criação ou atualização de licença (sempre envia todos os campos)
export async function salvarLicenca(licenca: LicencaDTO): Promise<LicencaDTO> {
  // envia para a API exatamente como está
  const response = await api.post('/licencas', licenca)
  return response.data
}

export const desativarLicenca = async (id: number): Promise<void> => {
  await api.delete(`/licencas/${id}`)
}

export const isLicencaValida = async (empresaId: number): Promise<boolean> => {
  const response = await api.get(`/licencas/empresa/${empresaId}/valida`)
  return response.data
}

export const getStatusLicenca = async (empresaId: number): Promise<LicencaStatusDTO> => {
  const response = await api.get(`/licencas/empresa/${empresaId}/status`)
  return response.data
}
