<template lang="pug">
Teleport(to="body")
  div(v-if="visivel" class="modal-backdrop")
    div(class="modal-container")
      header.modal-header
        h3 Extrato de Movimentação
        button.btn-close(@click="fechar") ✕

      section.modal-body
        dl
          dt Produto
          dd {{ movimentacao.produto.descricao }} ({{ movimentacao.produto.codigo }})

          dt Categoria
          dd {{ movimentacao.produto.categoria.nome }}

          dt Valor Fornecedor
          dd {{ formatarValor(movimentacao.produto.valorFornecedor) }}

          dt Status
          dd {{ movimentacao.produto.ativo ? 'Ativo' : 'Inativo' }}

          dt Tipo de Movimentação
          dd {{ movimentacao.tipo }}

          dt Quantidade
          dd {{ movimentacao.quantidade }}

          dt Estoque Atual
          dd {{ movimentacao.produto.quantidadeEstoque }}

          dt {{ rotuloValor }}
          dd {{ movimentacao.valorVenda ? formatarValor(movimentacao.valorVenda) : '-' }}

          dt Data da Movimentação
          dd {{ formatarData(movimentacao.dataVenda) }}

          dt Descrição
          dd {{ movimentacao.descricao }}
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, computed } from 'vue'

interface Categoria {
  id: number
  nome: string
}

interface Produto {
  id: number
  codigo: string
  descricao: string
  categoria: Categoria
  valorFornecedor: number
  quantidadeEstoque: number
  ativo: boolean
}

interface Movimentacao {
  id: number
  produto: Produto
  tipo: string
  valorVenda: number | null
  dataVenda: string
  quantidade: number
  descricao: string
}

const props = defineProps<{
  movimentacao: Movimentacao
  visivel: boolean
}>()

const rotuloValor = computed(() => {
  return props.movimentacao.tipo === 'ENTRADA' ? 'Valor de Compra' : 'Valor de Venda'
})

const emit = defineEmits(['fechar'])

function fechar() {
  emit('fechar')
}

function formatarData(dataStr: string) {
  if (!dataStr) return '-'
  const data = new Date(dataStr)
  return data.toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function formatarValor(valor: number) {
  return valor.toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  })
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background-color: rgba(100, 100, 100, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1100;
}

.modal-container {
  background: #fff;
  border-radius: 16px;
  max-width: 500px;
  width: 95%;
  padding: 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;

  /* NOVO */
  max-height: 80vh; /* altura máxima 80% da viewport */
  overflow-y: auto; /* permite rolagem vertical se ultrapassar */
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.modal-header h3 {
  font-size: 1.5rem;
  color: #2c3e50;
  font-weight: bold;
  margin: 0;
}

.btn-close {
  background: none;
  border: none;
  font-size: 2rem;
  line-height: 1;
  cursor: pointer;
  color: #777;
  transition: color 0.2s ease;
}

.btn-close:hover {
  color: #000;
}

.modal-body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

dt {
  font-weight: 600;
  color: #2c3e50;
  margin-top: 1rem;
  font-size: 1rem;
}

dd {
  margin-left: 0;
  margin-bottom: 0.5rem;
  font-size: 0.95rem;
  color: #34495e;
}
</style>
