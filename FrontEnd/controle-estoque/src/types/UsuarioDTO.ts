// src/types/UsuarioDTO.ts

export interface RoleDTO {
  id: number
  name: string
}

export interface EnderecoDTO {
  cep: string
  rua: string
  numero: string
  complemento?: string | null
  bairro: string
  cidade: string
  estado: string
}

export interface UsuarioDTO {
  id: number
  nome: string
  cpf: string
  email: string
  telefone?: string
  senha?: string
  ativo: boolean
  empresaId?: number // 👈 garantir que existe
  endereco?: EnderecoDTO
  roleId?: number
}
