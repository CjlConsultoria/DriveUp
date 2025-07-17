import api from './api'

// Tipos (pode mover para types.ts se preferir)
export interface Categoria {
  id: number
  nome: string
}

export interface Produto {
  id: number
  codigo: string
  descricao: string
  categoria: Categoria
  valorFornecedor: number
  quantidadeEstoque: number
  ativo: boolean
}

export interface ProdutoPage {
  content: Produto[]
  totalPages: number
  totalElements: number
  // outros campos, como number, pageable, etc.
}

// Criar produto
export const criarProduto = async (produto: Produto): Promise<Produto> => {
  const { data } = await api.post('/produtos', produto)
  return data
}

// Atualizar produto
export const atualizarProduto = async (id: number, produto: Produto): Promise<Produto> => {
  const { data } = await api.put(`/produtos/${id}`, produto)
  return data
}

// Deletar produto
export const deletarProduto = async (id: number): Promise<void> => {
  await api.delete(`/produtos/${id}`)
}

// Gerar novo código
export const pegarNovoCodigo = async (): Promise<string> => {
  const { data } = await api.get('/produtos/gerarCodigo')
  return data
}

// Listar todos os produtos com filtros opcionais
export const listarProdutos = async (params: Record<string, any> = {}): Promise<Produto[]> => {
  const { data } = await api.get('/produtos', { params })
  return data
}

// Listar produtos por nome de categoria
export const listarProdutosPorCategoria = async (categoria: string): Promise<Produto[]> => {
  const { data } = await api.get(`/produtos/por-categoria/${categoria}`)
  return data
}

// Listar todos os produtos paginados (com ou sem filtros)
export const listarAll = async (page = 0, size = 10): Promise<ProdutoPage> => {
  const { data } = await api.get('/produtos/all', {
    params: { page, size }
  })
  return data
}

// Listar produtos desativados com filtros e paginação
export const listarProdutosDesativadosComFiltro = async (params: {
  descricao?: string
  categoriaId?: number
  page?: number
  size?: number
}): Promise<ProdutoPage> => {
  const { data } = await api.get('/produtos/desativados/all', {
    params: {
      descricao: params.descricao || undefined,
      categoriaId: params.categoriaId || undefined,
      page: params.page ?? 0,
      size: params.size ?? 10
    }
  })
  return data
}

// Reativar produto desativado
export const reativarProduto = async (produtoId: number): Promise<Produto> => {
  const { data } = await api.put(`/produtos/${produtoId}/reativar`)
  return data
}
