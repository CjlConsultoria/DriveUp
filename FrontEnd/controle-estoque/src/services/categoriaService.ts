import api from './api'

export const listarCategorias = async () => {
  const response = await api.get('/categorias')
  return response.data
}

export const salvarCategoria = async (nome: string) => {
  const response = await api.post('/categorias', { nome })
  return response.data
}

export const excluirCategoria = async (id: number) => {
  await api.delete(`/categorias/${id}`)
}
