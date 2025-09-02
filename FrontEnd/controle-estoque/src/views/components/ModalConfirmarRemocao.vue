<template lang="pug">
  .modal-backdrop(@click.self="fechar")
    .modal-container(role="dialog" aria-modal="true" tabindex="-1" ref="modal")
      header.modal-header
        h3.modal-title Desativar Produto
        button.btn-close(@click="fechar" aria-label="Fechar modal") &times;
      section.modal-body
        p.text-confirm O produto será desativado e poderá ser reativado posteriormente na opção "Reativar Produto" da Lista de Produtos.
        p.text-confirm Tem certeza de que deseja desativar o seguinte produto?
        .produto-info
          p
            span.label Nome:
            span.valor {{ produto?.nome }}
          p
            span.label Código:
            span.valor {{ produto?.codigo }}
          p
            span.label Tipo:
            span.valor {{ produto?.tipo }}
          p
            span.label Valor de Compra do Fornecedor:
            span.valor-dinheiro {{ formatarMoeda(produto?.valorFornecedor) }}
          p
            span.label Quantidade:
            span.valor {{ produto?.quantidade }}
      footer.modal-footer
        button.btn-cancel(@click="fechar") Cancelar
        button.btn-remove(@click="confirmarRemocao") Desativar
  </template>

<script setup lang="ts">
import { defineEmits, defineProps } from 'vue'

interface Produto {
  id: number
  nome: string
  codigo: string
  tipo: string
  valorFornecedor: number
  quantidade: number
}

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'confirmar', id: number): void
}>()

const props = defineProps<{
  produto: Produto | null
}>()

function fechar() {
  emit('close')
}

function confirmarRemocao() {
  if (props.produto) {
    emit('confirmar', props.produto.id)
  }
  fechar()
}

function formatarMoeda(valor?: number) {
  return (
    valor?.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }) ?? 'R$ 0,00'
  )
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
  max-width: 480px;
  width: 92%;
  padding: 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.modal-title {
  font-size: 1.4rem;
  font-weight: bold;
  color: #030303;
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.8rem;
  cursor: pointer;
  color: #999;
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

.text-confirm {
  font-size: 1rem;
  color: #555;
  margin-bottom: 1rem;
  text-align: justify;
}

.produto-info {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  margin-top: 0.5rem;
  font-size: 0.95rem;
  color: #333;
}

.label {
  font-weight: 600;
  color: #000000;
  margin-right: 0.4rem;
}

.valor {
  font-weight: 500;
  color: #353535;
}

.valor-dinheiro {
  font-weight: 600;
  color: #740000;
}

.modal-footer {
  margin-top: 2rem;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.btn-cancel {
  background-color: #ebebeb;
  border: none;
  padding: 0.5rem 1.2rem;
  border-radius: 8px;
  font-weight: 600;
  color: #000000;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-cancel:hover {
  background-color: #dfdfdf;
}

.btn-remove {
  background-color: #c0392b;
  border: none;
  padding: 0.8rem 1.4rem;
  border-radius: 8px;
  font-weight: 700;
  color: white;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-remove:hover {
  background-color: #ffc1c1;
  color: #000;
}
</style>
