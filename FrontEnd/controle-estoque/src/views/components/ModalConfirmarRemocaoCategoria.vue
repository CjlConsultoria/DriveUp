<template lang="pug">
.modal-backdrop(@click.self="fechar")
  .modal-container(role="dialog" aria-modal="true" tabindex="-1" ref="modal")
    header.modal-header
      h3.modal-title Remover Categoria
      button.btn-close(@click="fechar" aria-label="Fechar modal") &times;
    section.modal-body
      p.text-confirm Tem certeza de que deseja remover a categoria abaixo?
      p.text-confirm Ao removê-la, todos os produtos vinculados ficarão sem categoria associada.
      .categoria-info
        p
          span.label Nome:
          span.valor-destaque {{ categoria?.nome }}
    footer.modal-footer
      button.btn-cancel(@click="fechar") Cancelar
      button.btn-remove(@click="confirmarRemocao") Remover
</template>

<script setup lang="ts">
import { defineEmits, defineProps } from 'vue'

interface Categoria {
  id: number
  nome: string
}

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'confirmar', id: number): void
}>()

const props = defineProps<{
  categoria: Categoria | null
}>()

console.log('Categoria recebida:', props.categoria)

function fechar() {
  emit('close')
}

function confirmarRemocao() {
  if (props.categoria) {
    emit('confirmar', props.categoria.id)
  }
  fechar()
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
  color: #c0392b;
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
}

.categoria-info {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  margin-top: 0.5rem;
  font-size: 0.95rem;
  color: #333;
}

.label {
  font-weight: 600;
  color: #444;
  margin-right: 0.4rem;
}

.valor {
  font-weight: 500;
  color: #222;
}

.modal-footer {
  margin-top: 2rem;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.btn-cancel {
  background-color: #f0f0f0;
  border: none;
  padding: 0.5rem 1.2rem;
  border-radius: 8px;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-cancel:hover {
  background-color: #e0e0e0;
}

.btn-remove {
  background-color: #e74c3c;
  border: none;
  padding: 0.5rem 1.4rem;
  border-radius: 8px;
  font-weight: 700;
  color: white;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-remove:hover {
  background-color: #c0392b;
}

.categoria-info {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  margin-top: 0.5rem;
  font-size: 1.1rem;
  color: #222;
}

.valor-destaque {
  font-weight: 500;
  font-size: 1.2rem;
  color: #e74c3c; /* Vermelho forte para destacar */
  margin-left: 0.4rem;
  user-select: text;
}
</style>
