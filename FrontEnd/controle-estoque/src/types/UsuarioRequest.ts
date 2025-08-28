export interface UsuarioRequest {
  nome: string
  email: string
  telefone: string
  cpf: string
  empresaId: number
  roleId: number
  ativo: boolean
  endereco: {
    cep: string
    rua: string
    numero: string
    complemento?: string
    bairro: string
    cidade: string
    estado: string
  }
  atualizar: boolean // 🔹 adiciona aqui
}
