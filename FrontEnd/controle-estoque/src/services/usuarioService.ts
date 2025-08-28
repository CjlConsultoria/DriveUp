import api from './api' // seu axios configurado

export interface UsuarioDTO {
  nome: string
  cpf?: string
  email: string
  telefone?: string
  ativo?: boolean
  empresaId?: number | string
  roleId?: string
  roleNome?: string
  endereco?: {
    cep: string
    rua: string
    numero: string
    complemento?: string
    bairro: string
    cidade: string
    estado: string
  }
}

// Criar ou atualizar usuários (sempre POST)
export async function salvarUsuarios(
  empresaId: string,
  usuarios: UsuarioDTO[]
): Promise<UsuarioDTO[]> {
  try {
    const resultados: UsuarioDTO[] = []

    for (const usuario of usuarios) {
      // adiciona empresaId no payload
      const payload = { ...usuario, empresaId }

      // envia sempre POST; backend decide criar ou atualizar
      const res = await api.post<UsuarioDTO>('/usuarios', payload)
      resultados.push(res.data)
    }

    return resultados
  } catch (error) {
    console.error('Erro ao salvar usuários:', error)
    return []
  }
}

// Listar usuários de uma empresa/oficina
export async function listarUsuarios(empresaId: string): Promise<UsuarioDTO[]> {
  try {
    const response = await api.get<UsuarioDTO[]>(`/usuarios/empresa/${empresaId}`)
    return response.data
  } catch (error) {
    console.error('Erro ao listar usuários:', error)
    return []
  }
}

// Mock ou real: excluir usuário
export async function excluirUsuarioAPI(cpf: string, empresaId: string): Promise<void> {
  try {
    await api.delete(`/usuarios/cpf?cpf=${cpf}&empresaId=${empresaId}`)
  } catch (err) {
    console.error('Erro ao excluir usuário:', err)
    throw err
  }
}
