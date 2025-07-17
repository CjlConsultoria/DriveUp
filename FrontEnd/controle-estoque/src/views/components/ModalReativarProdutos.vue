<template lang="pug">
div(v-if="visible" class="modal")
  div.modal-content
    h3 Reativar Produtos Desativados

    .filtros
      label(for="filtroDescricao") Filtro por nome
      input#filtroDescricao(type="text" v-model="filtroDescricao" placeholder="Digite o nome do produto...")

      label(for="filtroCategoriaId") Categoria
      select#filtroCategoriaId(v-model="filtroCategoriaId")
        option(value="") Selecione uma categoria
        option(v-for="cat in categorias" :key="cat.id" :value="cat.id") {{ cat.nome }}
    table.tabela-produtos
      thead
        tr
          th Código
          th Produto
          th Categoria
          th Estoque
          th Valor
          th Ação
      tbody
        tr(v-if="produtosContent.length === 0")
          td(colspan="6" style="text-align: center; padding: 1rem") Nenhum produto desativado encontrado.
        tr(v-for="p in produtosContent" :key="p.id" v-else)
          td {{ p.codigo }}
          td {{ p.descricao }}
          td {{ p.categoria?.nome || '—' }}
          td {{ p.quantidadeEstoque }}
          td R$ {{ p.valorFornecedor?.toFixed(2).replace('.', ',') }}
          td
            button.btn-small(@click="emitirReativar(p.id)") Reativar


    .paginacao
      button(@click="mudarPagina(-1)" :disabled="paginaAtual === 0") ← Anterior
      span Página {{ paginaAtual + 1 }} de {{ totalPaginas }}
      button(@click="mudarPagina(1)" :disabled="paginaAtual + 1 >= totalPaginas") Próxima →

    button.btn-close(@click="$emit('close')") Fechar
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { debounce } from 'lodash'
import { listarProdutosDesativadosComFiltro } from '@/services/produtoService'
import { listarCategorias } from '@/services/categoriaService'

defineProps<{ visible: boolean }>()
const emit = defineEmits(['reativar', 'close'])

const filtroDescricao = ref('')
const filtroCategoriaId = ref<number | ''>('')

const categorias = ref<{ id: number; nome: string }[]>([])
const produtosPaginados = ref<any | null>(null)
const paginaAtual = ref(0)
const tamanhoPagina = 5

const produtosContent = computed(() => produtosPaginados.value?.content || [])
const totalPaginas = computed(() => produtosPaginados.value?.totalPages || 0)

// Função com debounce para a busca (evita chamadas repetidas)
const debouncedBuscarProdutos = debounce(async () => {
  await buscarProdutos()
}, 400)

// Watchers para filtros (descrição e categoria)
watch([filtroDescricao, filtroCategoriaId], () => {
  paginaAtual.value = 0 // sempre volta pra página 0 ao mudar filtro
  debouncedBuscarProdutos()
})

// Watch para visibilidade do modal
watch(
  () => __props.visible,
  async (visivel) => {
    if (visivel) {
      categorias.value = await listarCategorias()
      filtroDescricao.value = ''
      filtroCategoriaId.value = ''
      paginaAtual.value = 0
      await buscarProdutos()
    }
  }
)

// Watch para paginaAtual para mudar página
watch(paginaAtual, async () => {
  await buscarProdutos()
})

async function buscarProdutos() {
  try {
    produtosPaginados.value = await listarProdutosDesativadosComFiltro({
      page: paginaAtual.value,
      size: tamanhoPagina,
      descricao: filtroDescricao.value || undefined,
      categoriaId: filtroCategoriaId.value === '' ? undefined : filtroCategoriaId.value
    })
  } catch (e) {
    console.error(e)
    alert('Erro ao buscar produtos desativados.')
  }
}

function mudarPagina(delta: number) {
  const nova = paginaAtual.value + delta
  if (nova >= 0 && nova < totalPaginas.value) {
    paginaAtual.value = nova
  }
}

function emitirReativar(produtoId: number) {
  emit('reativar', produtoId)
}
</script>

<style scoped>
.modal {
  position: fixed;
  inset: 0; /* shorthand top,right,bottom,left = 0 */
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
  padding: 1rem;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.modal-content {
  background: #fff;
  color: #222;
  border-radius: 12px;
  width: 100%;
  max-width: 900px;
  max-height: 90vh;
  padding: 2rem 2.5rem;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
}

h3 {
  font-size: 1.75rem;
  margin-bottom: 1.5rem;
  font-weight: 700;
  color: #333;
  text-align: center;
}

/* Filtros */
.filtros {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem 1.5rem;
  margin-bottom: 1.8rem;
  justify-content: center;
  align-items: center;
}

.filtros label {
  font-weight: 600;
  color: #555;
  min-width: 110px;
  user-select: none;
}

.filtros input,
.filtros select {
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  border: 1.8px solid #ddd;
  font-size: 1rem;
  transition: border-color 0.25s ease;
  min-width: 250px;
}

.filtros input:focus,
.filtros select:focus {
  outline: none;
  border-color: #4caf50;
  box-shadow: 0 0 8px rgba(76, 175, 80, 0.4);
}

/* Tabela */
.tabela-produtos {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 8px;
  font-size: 1rem;
  color: #444;
}

.tabela-produtos thead th {
  background-color: #369b70;
  color: #fff;
  font-weight: 600;
  padding: 0.75rem 1rem;
  text-align: left;
  border-radius: 8px 8px 0 0;
  user-select: none;
  letter-spacing: 0.03em;
}

.tabela-produtos tbody tr {
  background-color: #fafafa;
  box-shadow: 0 0 3px rgba(0, 0, 0, 0.08);
  transition: background-color 0.2s ease;
  cursor: default;
}

.tabela-produtos tbody tr:hover {
  background-color: #e8f5e9;
}

.tabela-produtos tbody td {
  padding: 0.65rem 1rem;
  vertical-align: middle;
  border-left: 1px solid #eee;
  border-right: 1px solid #eee;
}

.tabela-produtos tbody td:first-child {
  border-left: none;
}

.tabela-produtos tbody td:last-child {
  border-right: none;
  width: 110px;
  text-align: center;
}

/* Botão reativar */
.btn-small {
  background-color: #4caf50;
  color: #fff;
  border: none;
  padding: 0.4rem 0.85rem;
  border-radius: 6px;
  font-weight: 600;
  font-size: 0.85rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
  box-shadow: 0 2px 6px rgba(76, 175, 80, 0.35);
}

.btn-small:hover {
  background-color: #388e3c;
  box-shadow: 0 4px 12px rgba(56, 142, 60, 0.5);
}

/* Paginação */
.paginacao {
  margin-top: 1.8rem;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1.2rem;
  font-weight: 600;
  color: #555;
  user-select: none;
}

.paginacao button {
  background-color: #4caf50;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  color: #fff;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(76, 175, 80, 0.6);
  transition: background-color 0.3s ease;
}

.paginacao button:hover:not(:disabled) {
  background-color: #388e3c;
  box-shadow: 0 4px 14px rgba(56, 142, 60, 0.7);
}

.paginacao button:disabled {
  opacity: 0.45;
  cursor: default;
  box-shadow: none;
}

.paginacao span {
  font-size: 1rem;
  color: #444;
}

/* Botão fechar modal */
.btn-close {
  margin-top: 2rem;
  align-self: center;
  background-color: #ddd;
  color: #333;
  border: none;
  padding: 0.6rem 1.4rem;
  font-size: 1rem;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.3s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  user-select: none;
  min-width: 120px;
  text-align: center;
}

.btn-close:hover {
  background-color: #bbb;
}

/* Responsividade */
@media (max-width: 780px) {
  .modal-content {
    padding: 1.2rem 1.5rem;
    width: 95vw;
  }

  .filtros {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
  }

  .filtros label {
    min-width: auto;
  }

  .filtros input,
  .filtros select {
    min-width: auto;
    width: 100%;
  }

  .tabela-produtos tbody td {
    padding: 0.5rem 0.6rem;
  }

  .btn-small {
    padding: 0.3rem 0.6rem;
    font-size: 0.75rem;
  }

  .paginacao button {
    padding: 0.4rem 0.8rem;
    font-size: 0.9rem;
  }

  .btn-close {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
    min-width: auto;
  }
}
</style>
