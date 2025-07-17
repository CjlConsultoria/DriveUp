export interface UsuarioRequest {
  nome: string
  email: string
  senha: string
  telefone: string
  cpf: string
  endereco: {
    cep: string
    rua: string
    numero: string
    bairro: string
    cidade: string
    estado: string
  }
  role: string
}
