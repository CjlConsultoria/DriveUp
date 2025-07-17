<template lang="pug">
.main-container
  h1 Painel de Controle de Estoque

  .secao-container
    .card
      h2 Entrada / Saída de Produto

      form(@submit.prevent="registrarMovimentacao")
        .form-row.centralizado
          .flex-1.categoria-wrapper
            .categoria-top
              h3.titulo-categoria Categoria
              .botoes-categoria
                button.btn-adicionar(type="button" @click="abrirModalCategoria") + Adicionar Categoria
                button.btn-excluir(type="button" @click="abrirModalExcluirCategoria") Excluir Categoria

            select#categoria(v-model="categoriaFormulario" required)
              option(value="" disabled selected) Selecione a categoria
              option(v-for="cat in categorias" :key="cat.id" :value="cat.id") {{ cat.nome }}

          .flex-1.produto-wrapper
            label(for="produto") Produto
            select(v-model.number="idProdutoDetalheSelecionado" @change="onSelecionarProduto")
              option(value="") Selecione um produto
              option(v-for="produto in produtosFormulario" :key="produto.id" :value="produto.id") {{ produto.nome }}

            .mensagem-sem-produto(v-if="categoriaFormulario && produtosFormulario.length === 0")
              p.texto-aviso Nenhum produto encontrado para esta categoria.
              button.btn-adicionar-produto(type="button" @click="abrirAdicionarProduto") + Adicionar Produto

            .produto-detalhes(v-if="produtoParaDetalhe")
              small Código: {{ produtoParaDetalhe.codigo || '-' }}
              br
              small Tipo: {{ produtoParaDetalhe.tipo || '-' }}
              br
              small Estoque: {{ produtoParaDetalhe.quantidade ?? '-' }}
              br
              small Valor Fornecedor: {{ produtoParaDetalhe.valorFornecedor ? formatarReais(produtoParaDetalhe.valorFornecedor) : '-' }}
              br
              small Venda: {{ produtoParaDetalhe.valorVenda ? formatarReais(produtoParaDetalhe.valorVenda) : '-' }}


          .flex-1.tipo-toggle-wrapper
            label Tipo
            .tipo-toggle.centralizado
              button(
                :class="{ ativo: movimento.tipo === 'ENTRADA' }"
                type="button"
                @click="movimento.tipo = 'ENTRADA'"
              ) Entrada
              button(
                :class="{ ativo: movimento.tipo === 'SAIDA' }"
                type="button"
                @click="movimento.tipo = 'SAIDA'"
              ) Saída

        .form-row.centralizado(v-if="movimento.tipo === 'SAIDA'")
          div.flex-1.valor-wrapper
            label Valor de Venda
            .valor-formatado
              input(type="number" step="0.01" v-model.number="movimento.valor" required)
              span.valor-texto {{ formatarReais(movimento.valor) }}

        .form-row.centralizado
          div.flex-1.quantidade-wrapper
            label Quantidade
            .input-quantidade
              button.btn-quantidade(type="button" @click="diminuirQuantidade") –
              input(type="number" v-model.number="movimento.quantidade" min="1")
              button.btn-quantidade(type="button" @click="aumentarQuantidade") +

        .form-row.centralizado
          button.btn-submit(type="submit") Registrar
          button.btn-limpar(type="button" @click="limparSelects") Limpar

        p.alert(v-if="erro") {{ erro }}
        p.success(v-if="sucesso") {{ sucesso }}


  .secao-container
    .card
      h2 Lista de Produtos
      .filtros-produtos
        input(type="text" v-model="filtroProduto.codigo" placeholder="Filtrar por código")
        input(type="text" v-model="filtroProduto.descricao" placeholder="Filtrar por nome")
        select(v-model="filtroProduto.tipoProduto")
          option(value="") Todos os tipos
          option(value="SEM_CATEGORIA") SEM CATEGORIA
          option(v-for="cat in categorias" :key="cat.id" :value="cat.nome") {{ cat.nome }}
        button.btn-outline(type="button" @click="limparFiltrosProduto") Limpar filtros
        button.btn-reativer(@click="abrirReativarProduto()") Reativar Produto
        button.btn-outline-gren(@click="abrirAdicionarProduto()") Adicionar Produto + 
      table
        thead 
          tr
            th Código
            th Produto
            th Tipo
            th Estoque
            th Saídas
            th Fornecedor (R$)
            th Ações
        tbody
          tr(
              v-for="produto in produtos"
              :key="produto.id"
              @click="abrirExtrato(produto.id)"
              style="cursor: pointer;"
            )
            td
              strong {{ produto.codigo }}
            td {{ produto.nome }}
            td {{ produto.categoria && produto.categoria.nome ? produto.categoria.nome : 'SEM CATEGORIA' }}
            td {{ produto.quantidade }}
            td(v-if="!isLoading") {{ saidasPorProduto[produto.id] || 0 }}
            td {{ formatarReais(produto.valorFornecedor.toFixed(2)) }}
            td
              button.btn-outline(@click.stop="abrirEditar(produto)") Editar
              button.btn-outline-red(@click.stop="abrirConfirmarRemocao(produto)") Desativar

      .pagination
        button(:disabled="paginaProduto === 0" @click="buscarProdutos(paginaProduto - 1)") Anterior
        span Página {{ paginaProduto + 1 }} de {{ totalPaginasProduto }}
        button(:disabled="paginaProduto + 1 >= totalPaginasProduto" @click="buscarProdutos(paginaProduto + 1)") Próximo


  .secao-container
    .card
      h2 Histórico de Movimentações
      .filtros-movimentacoes
        input(type="text" v-model="filtroMov.codigo" placeholder="Filtrar por código do produto")
        select(v-model="filtroMov.tipo")
          option(value="") Todos os tipos
          option(value="ENTRADA") Entrada
          option(value="SAIDA") Saída
          option(value="EXCLUSAO") Excluido
          option(value="EDITADO") Editado
          option(value="CRIADO") Criado
        input(type="date" v-model="filtroMov.dataInicio")
        input(type="date" v-model="filtroMov.dataFim")
        button.btn-outline(type="button" @click="limparFiltrosMovimentacoes") Limpar filtros
      table
        thead
          tr
            th Código
            th Produto
            th Tipo Produto
            th Tipo Movimentação
            th Quantidade
            th Valor Fornecedor (R$)
            th Valor Venda/Compra (R$)
            th Lucro (R$)
            th Data Venda/Entrada
        tbody
          tr(
            v-for="m in movimentacoes"
            :key="m.id"
            @click="abrirModalMovimentacao(m)"
            style="cursor: pointer;"
          )
            td
              strong {{ m.produto.codigo }}
            td {{ m.produto.descricao }}
            td {{ m.produto.categoria?.nome || 'SEM CATEGORIA' }}
            td(:class="{'entrada': m.tipo === 'ENTRADA','saida': m.tipo === 'SAIDA','exclusao': m.tipo === 'EXCLUSAO', 'editado': m.tipo === 'EDITADO', 'criado': m.tipo === 'CRIADO'}") {{ m.tipo }}
            td {{ m.quantidade }}
            td {{ formatarReais(m.produto.valorFornecedor) }}
            td {{ m.valorVenda ? formatarReais(m.valorVenda) : '—' }}
            td {{ calcularLucro(m) }}
            td {{ m.dataVenda ? new Date(m.dataVenda).toLocaleDateString('pt-BR') : '—' }}

      .pagination
        button(:disabled="paginaMov === 0" @click="buscarMovimentacoes(paginaMov - 1)") Anterior
        span Página {{ paginaMov + 1 }} de {{ totalPaginasMov }}
        button(:disabled="paginaMov + 1 >= totalPaginasMov" @click="buscarMovimentacoes(paginaMov + 1)") Próximo

  // Modals (sem alteração)
  ModalEditarProduto(
    v-if="modalEditarAberto"
    :produto="produtoSelecionado"
    :categorias="categorias"
    @close="modalEditarAberto = false"
    @salvar="salvarProduto"
  )

  ModalConfirmarRemocao(
    v-if="modalRemocaoAberto"
    :produto="produtoSelecionado"
    @close="modalRemocaoAberto = false"
    @confirmar="confirmarRemocao"
  )

  ModalProduto(
    v-if="modalAberto"
    :produto="produtoParaEditar"
    :categorias="categorias"
    @close="modalAberto = false"
    @salvar="salvarProduto"
    @criar-categoria="adicionarCategoria"
  )

  ModalExtrato(
    v-if="modalExtratoAberto"
    :extrato="extratoProduto"
    @close="modalExtratoAberto = false"
  )

  ModalNovoCategoria(
    v-if="modalNovaCategoriaAberto" 
    @close="modalNovaCategoriaAberto = false" 
    @salvar="adicionarCategoria"
  )

  ModalConfirmarRemocaoCategoria(
    v-if="modalExcluirCategoriaAberto"
    :categoria="categoriaSelecionadaParaExcluir"
    @close="modalExcluirCategoriaAberto = false"
    @confirmar="confirmarRemocaoCategoria"
  )

  ModalExtratoMovimentacao(
    v-if="modalMovimentacaoAberto && movimentacaoSelecionada"
    :movimentacao="movimentacaoSelecionada"
    :visivel="modalMovimentacaoAberto"
    @fechar="modalMovimentacaoAberto = false"
  )

  ModalReativarProdutos(
    :visible="modalReativarAberto"
    @close="modalReativarAberto = false"
    @reativar="onReativarProduto"
  )

</template>

<script setup lang="ts">
import { ref, onMounted, computed, reactive, watch } from 'vue'
import {
  listarProdutos,
  criarProduto,
  atualizarProduto,
  deletarProduto,
  listarProdutosPorCategoria,
  reativarProduto
} from '@/services/produtoService'
import {
  listarMovimentacao,
  criarMovimentacao,
  listarExtratoPorProduto,
  listarResumoSaidas
} from '@/services/movimentacaoService'
import type { ExtratoProduto } from '@/services/movimentacaoService'
import { buscarMovimentacao } from '@/services/movimentacaoService'

import { listarCategorias, salvarCategoria, excluirCategoria } from '@/services/categoriaService'
import { login } from '@/services/authService'
import ModalExtrato from '@/views/components/ModalExtrato.vue'
import ModalExtratoMovimentacao from '@/views/components/ModalExtratoMovimentacao.vue'
import ModalReativarProdutos from '@/views/components/ModalReativarProdutos.vue' // ajuste caminho se necessário
import ModalNovoCategoria from '@/views/components/ModalNovoCategoria.vue'
import ModalProduto from '@/views/components/ModalProdutoNovo.vue'
import ModalConfirmarRemocaoCategoria from '@/views/components/ModalConfirmarRemocaoCategoria.vue'

import ModalEditarProduto from '@/views/components/ModalEditarProduto.vue'
import ModalConfirmarRemocao from '@/views/components/ModalConfirmarRemocao.vue'
import { useToast } from 'vue-toastification'

const toast = useToast()
interface Produto {
  id: number
  codigo: string
  nome: string
  tipo: string
  categoria: {
    id: number
    nome: string
  }
  quantidade: number
  valorFornecedor: number
  valorVenda?: number
  saidas?: number
}

interface Movimento {
  produtoId: number | null
  tipo: 'ENTRADA' | 'SAIDA'
  quantidade: number
  valor: number
}

interface ProdutoMovimentado {
  id: number
  codigo: string
  descricao: string
  tipoProduto: string
  valorFornecedor: number
  quantidadeEstoque: number
  categoria: {
    id: number
    nome: string
  }
}

interface Movimentacao {
  id: number
  produto: ProdutoMovimentado
  tipo: 'ENTRADA' | 'SAIDA'
  valorVenda: number | null
  dataVenda: string | null
  quantidade: number
}
const modalNovaCategoriaAberto = ref(false)
interface Categoria {
  id: number
  nome: string
}

const categorias = ref<Categoria[]>([])
const totalProdutos = ref(0)
const paginaAtual = ref(0)
const tamanhoPagina = ref(10)

const modalExtratoAberto = ref(false)
const extratoProduto = ref<ExtratoProduto | null>(null)
const modalEditarAberto = ref(false)
const modalRemocaoAberto = ref(false)
const produtoParaEditar = ref<Produto | null>(null)
const modalAberto = ref(false)
const produtos = ref<Produto[]>([])
const movimento = ref<Movimento>({
  produtoId: null,
  tipo: 'ENTRADA',
  quantidade: 1,
  valor: 0
})
const filtroProduto = reactive({
  codigo: '',
  descricao: '',
  tipoProduto: ''
})
const isLoading = ref(true)
const categoriaFormulario = ref<number | ''>('')
const paginaProduto = ref(0)
const totalPaginasProduto = ref(1)

const filtroMov = reactive({
  tipo: '',
  codigo: '',
  dataInicio: '',
  dataFim: ''
})
const produtoParaDetalhe = computed(() => {
  return produtosFormulario.value.find((p) => p.id === idProdutoDetalheSelecionado.value) || null
})

function onSelecionarProduto() {
  const produto = produtosFormulario.value.find((p) => p.id === idProdutoDetalheSelecionado.value)
}

async function refreshLists() {
  await carregarCategorias()
  await buscarProdutos()
  await buscarMovimentacoes()
}

const movimentacaoSelecionada = ref<any | null>(null)
const modalMovimentacaoAberto = ref(false)
async function abrirModalMovimentacao(movimentacao: { id: number }) {
  try {
    console.log('Tentando abrir modal para movimentacao id:', movimentacao.id)
    const dados = await buscarMovimentacao(movimentacao.id)
    console.log('Dados recebidos da API:', dados)

    movimentacaoSelecionada.value = dados
    modalMovimentacaoAberto.value = true
    console.log('Modal aberto:', modalMovimentacaoAberto.value)
  } catch (err) {
    console.error('Erro ao buscar movimentação:', err)
  }
}

const modalReativarAberto = ref(false)

// Função para abrir modal
function abrirReativarProduto() {
  modalReativarAberto.value = true
}

async function onReativarProduto(produtoId: number) {
  try {
    await reativarProduto(produtoId)
    toast.success('Produto reativado com sucesso!')
    modalReativarAberto.value = false
    await refreshLists()
  } catch (error) {
    toast.error((error as Error).message || 'Erro ao reativar produto')
  }
}
const movimentacoes = ref<Movimentacao[]>([])
const erro = ref('')
const sucesso = ref('')
const saidasPorProduto = reactive<Record<number, number>>({})

const produtoSelecionado = ref<Produto | null>(null)
const idProdutoDetalheSelecionado = ref<number | null>(null)

const produtoCategoriaSelecionadoId = ref<string | null>(null)
const produtosFormulario = ref<Produto[]>([])

function formatarReais(valor: number | undefined): string {
  return valor == null
    ? '—'
    : new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor)
}

const abrirExtrato = async (produtoId: number) => {
  try {
    modalExtratoAberto.value = false
    extratoProduto.value = null
    extratoProduto.value = await listarExtratoPorProduto(produtoId)
    modalExtratoAberto.value = true
  } catch {
    toast.error('Erro ao carregar extrato do produto.')
    modalExtratoAberto.value = false
  }
}
function limparSelects() {
  categoriaFormulario.value = ''
  idProdutoDetalheSelecionado.value = null
  produtoSelecionado.value = null
  movimento.value = {
    produtoId: null,
    tipo: 'ENTRADA',
    quantidade: 1,
    valor: 0
  }
  buscarProdutos()
}

async function buscarProdutos(pagina = 0) {
  paginaProduto.value = pagina
  isLoading.value = true
  erro.value = ''

  try {
    const params = {
      codigo: filtroProduto.codigo || '',
      descricao: filtroProduto.descricao || '',
      categoria: filtroProduto.tipoProduto || '',
      page: paginaProduto.value,
      size: tamanhoPagina.value,
      sort: 'descricao,asc'
    }
    const resposta = await listarProdutos(params)

    produtos.value = resposta.content.map((p: any) => ({
      id: p.id,
      codigo: p.codigo,
      nome: p.descricao,
      tipo: p.categoria?.nome || '',
      categoria: {
        id: p.categoria?.id,
        nome: p.categoria?.nome || ''
      },
      quantidade: p.quantidadeEstoque,
      valorFornecedor: p.valorFornecedor,
      saidas: 0
    }))

    totalPaginasProduto.value = resposta.totalPages
    totalProdutos.value = resposta.totalElements
  } catch (e) {
    toast.error('Erro ao carregar produtos')
    produtos.value = []
  } finally {
    isLoading.value = false
  }
}

const paginaMov = ref(0)
const totalPaginasMov = ref(1)

async function buscarMovimentacoes(pagina = 0) {
  paginaMov.value = pagina
  try {
    const resposta = await listarMovimentacao({
      tipo: filtroMov.tipo,
      codigo: filtroMov.codigo,
      dataInicio: filtroMov.dataInicio,
      dataFim: filtroMov.dataFim,
      page: paginaMov.value,
      size: 10
    })
    movimentacoes.value = resposta.content
    totalPaginasMov.value = resposta.totalPages
  } catch (e) {
    toast.error('Erro ao carregar movimentações')
  }
}

async function carregarCategorias() {
  try {
    categorias.value = await listarCategorias()
  } catch (error) {
    toast.error('Erro ao carregar categorias')
  }
}

const modalExcluirCategoriaAberto = ref(false)
const categoriaSelecionadaParaExcluir = ref<Categoria | null>(null)

function abrirModalExcluirCategoria() {
  const categoria = categorias.value.find((c) => c.id === categoriaFormulario.value) || null
  if (categoria) {
    categoriaSelecionadaParaExcluir.value = categoria
    modalExcluirCategoriaAberto.value = true
  } else {
    toast.error('Por favor, selecione uma categoria para excluir.')
  }
}

async function confirmarRemocaoCategoria(id: number) {
  try {
    await excluirCategoria(id)

    categorias.value = categorias.value.filter((cat) => cat.id !== id)

    toast.success('Categoria removida com sucesso!')
    await refreshLists()
  } catch (error: any) {
    console.error(error)

    // Tenta extrair a mensagem personalizada do backend
    const msgErro = error?.response?.data?.message || 'Erro ao remover a categoria.'

    toast.error(msgErro)
  } finally {
    modalExcluirCategoriaAberto.value = false
    categoriaSelecionadaParaExcluir.value = null
  }
}

function limparFiltrosProduto() {
  filtroProduto.codigo = ''
  filtroProduto.descricao = ''
  filtroProduto.tipoProduto = ''
  filtroMov.tipo = ''
  filtroMov.codigo = ''
  filtroMov.dataInicio = ''
  filtroMov.dataFim = ''
  buscarProdutos(0)
}

function limparFiltrosMovimentacoes() {
  filtroProduto.codigo = ''
  filtroProduto.descricao = ''
  filtroProduto.tipoProduto = ''
  filtroMov.tipo = ''
  filtroMov.codigo = ''
  filtroMov.dataInicio = ''
  filtroMov.dataFim = ''
  buscarMovimentacoes(0)
}
watch(produtoCategoriaSelecionadoId, (novoId) => {
  const encontrado = produtosFormulario.value.find((p) => p.id.toString() === novoId)
  if (encontrado) {
    produtoSelecionado.value = encontrado
  }
})

watch(
  () => movimento.value.produtoId,
  (novoProdutoId) => {
    if (novoProdutoId == null) {
      produtoSelecionado.value = null
    } else {
      let produto = produtosFormulario.value.find((p) => p.id === novoProdutoId)
      if (!produto) {
        produto = produtos.value.find((p) => p.id === novoProdutoId)
      }
      produtoSelecionado.value = produto || null
    }
  }
)

watch(
  () => [filtroProduto.codigo, filtroProduto.descricao, filtroProduto.tipoProduto],
  () => buscarProdutos(0)
)

watch(
  () => [filtroMov.tipo, filtroMov.dataInicio, filtroMov.dataFim, filtroMov.codigo],
  () => buscarMovimentacoes(0)
)

onMounted(async () => {
  isLoading.value = true
  erro.value = ''
  sucesso.value = ''

  try {
    const resultado = await listarResumoSaidas()
    Object.assign(saidasPorProduto, resultado)

    await carregarCategorias()
    await buscarProdutos()
    await buscarMovimentacoes()
  } catch (e: any) {
    toast.error(e.response?.data?.message || 'Erro ao carregar dados')
  } finally {
    isLoading.value = false
  }
})

function calcularLucro(mov: Movimentacao): string {
  if (mov.tipo === 'SAIDA' && mov.valorVenda != null) {
    const lucro = (mov.valorVenda - mov.produto.valorFornecedor) * mov.quantidade
    return formatarReais(lucro)
  }
  return '—'
}

watch(
  () => movimento.value.produtoId,
  (novoId) => {
    if (novoId == null) {
      produtoSelecionado.value = null
      movimento.value.valor = 0
    } else {
      const produto = produtos.value.find((p) => p.id === novoId)
      produtoSelecionado.value = produto || null
      if (produto) {
        movimento.value.valor =
          movimento.value.tipo === 'ENTRADA' ? produto.valorFornecedor : produto.valorVenda
      }
    }
  }
)

watch(
  () => movimento.value.tipo,
  (novoTipo) => {
    if (produtoSelecionado.value) {
      movimento.value.valor =
        novoTipo === 'ENTRADA'
          ? produtoSelecionado.value.valorFornecedor
          : produtoSelecionado.value.valorVenda
    }
  }
)

async function registrarMovimentacao() {
  erro.value = ''
  sucesso.value = ''
  const produto = produtosFormulario.value.find((p) => p.id === idProdutoDetalheSelecionado.value)

  if (!produto) {
    toast.error('Produto não encontrado.')
    return
  }

  if (movimento.value.tipo === 'ENTRADA') {
    produto.quantidade += movimento.value.quantidade
  } else {
    if (produto.quantidade < movimento.value.quantidade) {
      toast.warning(`Estoque insuficiente. Saldo: ${produto.quantidade}`)
      return
    }
    produto.quantidade -= movimento.value.quantidade
    produto.saidas = (produto.saidas || 0) + movimento.value.quantidade

    saidasPorProduto[produto.id] = (saidasPorProduto[produto.id] || 0) + movimento.value.quantidade
  }

  const movimentacaoParaEnviar = {
    produto: { id: produto.id },
    tipo: movimento.value.tipo,
    valorVenda: movimento.value.tipo === 'SAIDA' ? movimento.value.valor : produto.valorFornecedor,
    dataVenda: movimento.value.tipo === 'SAIDA' ? new Date().toISOString() : null,
    quantidade: movimento.value.quantidade
  }

  try {
    const novaMovimentacao = await criarMovimentacao(movimentacaoParaEnviar)
    movimentacoes.value.push({
      id: novaMovimentacao.id,
      produto: {
        id: produto.id,
        codigo: produto.codigo,
        descricao: produto.nome,
        tipoProduto: produto.tipo,
        valorFornecedor: produto.valorFornecedor,
        quantidadeEstoque: produto.quantidade
      },
      tipo: movimento.value.tipo,
      valorVenda: movimentacaoParaEnviar.valorVenda,
      dataVenda: movimentacaoParaEnviar.dataVenda,
      quantidade: movimentacaoParaEnviar.quantidade
    })
    toast.success(
      movimento.value.tipo === 'SAIDA'
        ? 'Saída registrada com sucesso.'
        : 'Entrada registrada com sucesso.'
    )
  } catch (e) {
    toast.error('Erro ao registrar movimentação na API.')
  }
  await refreshLists()
}

function abrirEditar(produto: Produto) {
  produtoSelecionado.value = { ...produto }
  modalEditarAberto.value = true
}

async function adicionarCategoria(nome: string) {
  const jaExiste = categorias.value.some((cat) => cat.nome.toLowerCase() === nome.toLowerCase())
  if (jaExiste) {
    toast.warning('Essa categoria já existe.')
    return
  }

  try {
    const novaCategoria = await salvarCategoria(nome)
    categorias.value.push(novaCategoria) // Atualiza localmente
    toast.success('Categoria adicionada com sucesso!')

    // Se o modal de produto estiver aberto, atualiza a lista nele também
    if (modalAberto.value && produtoParaEditar.value === null) {
      // Pré-seleciona a nova categoria no formulário de produto
      categoriaFormulario.value = novaCategoria.id
    }
  } catch (error) {
    toast.error('Erro ao salvar categoria.')
  }
  await refreshLists()
}

function abrirModalCategoria() {
  modalNovaCategoriaAberto.value = true
}

function abrirConfirmarRemocao(produto: Produto) {
  produtoSelecionado.value = produto
  modalRemocaoAberto.value = true
}

function abrirAdicionarProduto() {
  produtoParaEditar.value = null
  modalAberto.value = true
}
async function salvarProduto(produto: Produto) {
  try {
    if (!produto.id || produto.id === 0) {
      await criarProduto(produto)
      toast.success('Produto criado com sucesso.')
    } else {
      await atualizarProduto(produto.id, produto)
      toast.success('Produto atualizado com sucesso.')
    }
    modalAberto.value = false
    await refreshLists()
  } catch {
    toast.error('Erro ao salvar produto.')
  }
}

async function confirmarRemocao(id: number) {
  try {
    await deletarProduto(id)
    const index = produtos.value.findIndex((p) => p.id === id)
    if (index !== -1) {
      produtos.value.splice(index, 1)
      toast.success('Produto removido com sucesso.')
    }
    await refreshLists()
  } catch {
    toast.error('Erro ao remover produto.')
  }
}

function aumentarQuantidade() {
  movimento.value.quantidade++
}

function diminuirQuantidade() {
  if (movimento.value.quantidade > 1) {
    movimento.value.quantidade--
  }
}

const categoriaSelecionada = ref('')

watch(
  () => categoriaFormulario.value,
  async (novaCategoriaId) => {
    if (novaCategoriaId) {
      await buscarProdutosPorCategoria()
      movimento.value.produtoId = null
    } else {
      produtosFormulario.value = []
      movimento.value.produtoId = null
    }
  }
)

async function buscarProdutosPorCategoria() {
  isLoading.value = true
  erro.value = ''
  try {
    const idCat = Number(categoriaFormulario.value)
    if (!idCat) {
      produtosFormulario.value = []
      throw new Error('Categoria não selecionada ou inválida')
    }
    const nomeCategoria = categorias.value.find((c) => c.id === idCat)?.nome || ''
    if (!nomeCategoria) {
      produtosFormulario.value = []
      throw new Error('Nome da categoria não encontrado')
    }
    const resposta = await listarProdutosPorCategoria(nomeCategoria)
    produtosFormulario.value = resposta.map((p: any) => ({
      id: p.id,
      codigo: p.codigo,
      nome: p.descricao,
      tipo: p.categoria?.nome || '',
      categoria: {
        id: p.categoria?.id,
        nome: p.categoria?.nome || ''
      },
      quantidade: p.quantidadeEstoque,
      valorFornecedor: p.valorFornecedor,
      valorVenda: p.valorVenda
    }))
  } catch (e) {
    toast.error('Erro ao carregar produtos por categoria')
    produtosFormulario.value = []
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  isLoading.value = true
  erro.value = ''
  sucesso.value = ''
  try {
    await carregarCategorias()
    await buscarProdutos()
    await buscarMovimentacoes()
  } catch (e: any) {
    toast.error(e.response?.data?.message || 'Erro ao carregar dados')
  } finally {
    isLoading.value = false
  }
})
</script>

<style scoped>
* {
  box-sizing: border-box;
}

html,
body {
  margin: 0;
  padding: 0;
  height: 100%;
  background-color: #f9f9f9;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #000; /* texto padrão preto */
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
  color: #2c3e50;
}

form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-row {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.main-container {
  flex: 1;
  height: calc(100vh - 70px); /* 70px é a altura do header */
  background-color: #ffffff; /* fundo limpo */
  padding: 0;
  margin: 0;
  max-width: 100%;
  width: 100%;
  overflow-y: auto;
}

.card {
  background-color: #fff;
  border-radius: 10px;
  padding: 1.5rem;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  margin-bottom: 2rem;
  color: #000;
}

input,
select {
  width: 100%;
  padding: 0.6rem 0.75rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  background-color: #fff;
  color: #000;
  outline: none;
  transition:
    border-color 0.3s ease,
    box-shadow 0.3s ease;
}

input:focus,
select:focus {
  border-color: #42b983;
  box-shadow: 0 0 0 2px rgba(66, 185, 131, 0.2);
}

label {
  display: block;
  margin-bottom: 0.3rem;
  font-weight: 600;
  font-size: 0.95rem;
  color: #2c3e50;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
  table-layout: auto; /* crescer conforme o conteúdo */
  font-size: 0.95rem;
  min-width: 600px; /* para evitar que fique muito estreita */
  color: #000;
}

th,
td {
  border-bottom: 1px solid #ddd;
  padding: 0.75rem 1rem;
  text-align: left;
  vertical-align: middle;
  white-space: nowrap; /* evita quebra estranha em colunas */
}

thead th {
  background-color: #42b983;
  color: #fff;
  font-weight: 600;
  user-select: none;
}

tbody tr:hover {
  background-color: #f5f9f7;
  cursor: default;
}

tbody td:last-child {
  white-space: normal; /* permite quebra na coluna Ações, para botões */
}

button {
  padding: 0.6rem 1.2rem;
  font-size: 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  user-select: none;
}

button[type='submit'] {
  background-color: #42b983;
  color: #fff;
}

button[type='submit']:hover {
  background-color: #369b70;
}

.alert {
  color: #c0392b;
  font-weight: bold;
}

.success {
  color: #27ae60;
  font-weight: bold;
}

.btn-outline {
  background-color: #ecf0f1;
  color: #2c3e50;
  padding: 0.4rem 0.7rem;
  margin-right: 0.5rem;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s ease;
}

.btn-outline:hover {
  background-color: #dcdfe1;
}

.btn-outline-red {
  background-color: #fbeaea;
  color: #c0392b;
  padding: 0.4rem 0.7rem;
  border-radius: 4px;
  font-size: 0.9rem;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-outline-red:hover {
  background-color: #f5c6cb;
}

.btn-outline-gren {
  background-color: #bdebbd;
  color: #649960;
  padding: 0.4rem 0.7rem;
  margin-right: 0.5rem;
  border-radius: 4px;
  font-size: 0.9rem;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-outline-gren:hover {
  background-color: #bdebbd;
}

.btn-reativer {
  background-color: #c9c632;
  color: #000000;
  padding: 0.4rem 0.7rem;
  margin-right: 0.5rem;
  border-radius: 4px;
  font-size: 0.9rem;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-reativer:hover {
  background-color: #97af3d;
}

.produto-detalhes {
  margin-top: 0.5rem;
  background-color: #f2f2f2;
  padding: 0.5rem;
  border-radius: 6px;
  font-size: 0.85rem;
  color: #333;
  line-height: 1.4;
}

.tipo-toggle {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.4rem;
}

.tipo-toggle button {
  padding: 0.4rem 1rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  background-color: #f9f9f9;
  cursor: pointer;
  font-weight: 600;
  transition:
    background-color 0.2s ease,
    border-color 0.2s ease;
  color: #2c3e50;
}

.tipo-toggle button.ativo {
  background-color: #42b983;
  color: #000;
  border-color: #42b983;
}

.tipo-toggle button:hover {
  background-color: #e0e0e0;
}

.form-row.centralizado {
  justify-content: center;
  align-items: center;
  gap: 1rem;
  display: flex;
  flex-wrap: wrap;
}

.flex-1 {
  flex: 1;
  min-width: 250px;
}

.flex-2 {
  flex: 2;
  min-width: 300px;
}

.btn-submit {
  background-color: #42b983;
  color: #fff;
  border: none;
  padding: 0.5rem 1rem;
  font-size: 0.95rem;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-submit:hover {
  background-color: #369b70;
}

.btn-limpar {
  background-color: #ecf0f1;
  color: #2c3e50;
  border: none;
  padding: 0.5rem 1rem;
  font-size: 0.95rem;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-limpar:hover {
  background-color: #dcdfe1;
}

.input-quantidade {
  display: flex;
  align-items: center;
  max-width: 140px;
  margin-top: 0.3rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  overflow: hidden;
  background-color: #fff;
}

.input-quantidade input[type='number'] {
  border: none;
  outline: none;
  width: 60px;
  padding: 0.4rem;
  text-align: center;
  font-size: 1rem;
  -moz-appearance: textfield; /* remove setas padrão no Firefox */
}

.input-quantidade input[type='number']::-webkit-outer-spin-button,
.input-quantidade input[type='number']::-webkit-inner-spin-button {
  -webkit-appearance: none; /* remove setas padrão no Chrome */
  margin: 0;
}

.btn-quantidade {
  background-color: #42b983;
  color: #fff;
  border: none;
  width: 32px;
  height: 32px;
  font-weight: bold;
  cursor: pointer;
  user-select: none;
  transition: background-color 0.2s ease;
  display: flex;
  justify-content: center;
  align-items: center;
}

.btn-quantidade:hover {
  background-color: #369b70;
}

@media (max-width: 768px) {
  table {
    font-size: 0.85rem;
    min-width: auto;
  }
  th,
  td {
    padding: 0.5rem 0.75rem;
  }
  .btn-outline,
  .btn-outline-gren,
  .btn-reativer .btn-outline-red {
    font-size: 0.75rem;
    padding: 0.25rem 0.5rem;
  }
  .flex-1,
  .flex-2 {
    min-width: 100%;
    flex: 1 1 100%;
  }
  .form-row {
    flex-direction: column;
  }
}

.form-row.centralizado {
  display: flex;
  justify-content: center;
  align-items: flex-start; /* alinhamento topo */
  gap: 1rem;
  flex-wrap: nowrap;
}

.produto-com-detalhes {
  display: flex;
  flex-direction: column;
  min-width: 0; /* importante para que o flex-basis funcione corretamente */
  flex: 1 1 50%; /* ocupa 50% do espaço e pode encolher */
}

.tipo-toggle-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  min-width: 0; /* para que flex-basis funcione */
  flex: 1 1 50%; /* ocupa 50% do espaço */
  max-width: 250px; /* opcional, para limitar o toggle */
}
.entrada {
  background-color: #d0f0fd; /* azul bem suave */
  color: #000000;
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 3px;
  text-align: center;
}

.saida {
  background-color: #a3e091; /* vermelho bem suave */
  color: #000000;
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 3px;
  text-align: center;
}

.exclusao {
  background-color: #f8d7da; /* vermelho claro suave */
  color: #721c24; /* vermelho escuro para texto */
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 3px;
  text-align: center;
}

.editado {
  background-color: #cbd811; /* vermelho claro suave */
  color: #070707; /* vermelho escuro para texto */
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 3px;
  text-align: center;
}
.criado {
  background-color: #5c6960; /* vermelho claro suave */
  color: #ffffff; /* vermelho escuro para texto */
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 3px;
  text-align: center;
}
td strong {
  font-weight: 700;
}

table th,
table td {
  text-align: center;
  vertical-align: middle;
}

.filtros-produtos {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}
.filtros-produtos input,
.filtros-produtos select {
  width: 350px;
  max-width: 100%;
}

.filtros-movimentacoes {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.filtros-movimentacoes input,
.filtros-movimentacoes select {
  width: 300px;
  max-width: 100%;
}

.pagination {
  margin-top: 1rem;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
}

.pagination button {
  border: 1.5px solid #28a745; /* verde limpo */
  background-color: white;
  color: #212529; /* cinza escuro quase preto */
  padding: 0.4rem 0.9rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s ease;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  border-color: #ced4da;
  color: #6c757d;
  background-color: #f8f9fa;
}

.pagination button:not(:disabled):hover,
.pagination button:not(:disabled):focus {
  background-color: #28a745;
  color: white;
  border-color: #28a745;
}

.pagination button:not(:disabled):active {
  background-color: #218838;
  border-color: #1e7e34;
  color: white;
}
.btn-adicionar {
  background-color: #42b983; /* Cor azul padrão (pode ajustar) */
  color: white;
  padding: 0.4rem 0.7rem;
  border-radius: 4px;
  font-size: 0.9rem;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-adicionar:hover {
  background-color: #2d644c; /* Cor mais escura ao passar o mouse */
}

.header-categoria {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 6px; /* espaço entre header e select */
}

.titulo-categoria {
  margin: 0;
  font-weight: 600;
  font-size: 1rem;
}

.mensagem-sem-produto {
  margin-top: 10px;
}

.texto-aviso {
  color: #a81b1b; /* amarelo alerta */
  font-weight: 500;
  margin-bottom: 6px;
}

.btn-adicionar-produto {
  background-color: #42b983; /* verde */
  color: white;
  padding: 0.4rem 0.7rem;
  border-radius: 4px;
  font-size: 0.9rem;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-adicionar-produto:hover {
  background-color: #2d644c;
}

.btn-excluir {
  background-color: #fbeaea;
  color: #c0392b;
  padding: 0.4rem 0.7rem;
  border-radius: 4px;
  font-size: 0.9rem;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-excluir:hover {
  background-color: #f5c6cb;
}

.botoes-categoria {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.secao-container {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 4px rgb(0 0 0 / 0.05);
}

.categoria-wrapper,
.produto-wrapper {
  display: flex;
  flex-direction: column;
}

.categoria-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.botoes-categoria button {
  margin-left: 0.5rem;
}

/* Se quiser, pode limitar largura para ficar proporcional */
.flex-1 {
  flex: 1;
  min-width: 280px; /* para não ficar muito estreito */
  margin-right: 1rem;
}

/* Para form-row centralizado ajustar espaçamento */
.form-row.centralizado {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
}
</style>
