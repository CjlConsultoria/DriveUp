<template lang="pug">
  .modal-backdrop(@click.self="fechar")
    .modal-container(role="dialog" aria-modal="true" tabindex="-1" ref="modal")
      header.modal-header
        h3 Nova Categoria
        button.btn-close(@click="fechar" aria-label="Fechar modal") &times;

      section.modal-body
        form(@submit.prevent="salvar")
          label Nome da Categoria
          input(type="text" v-model="nomeCategoria" required placeholder="Ex: Informática")

          footer.modal-footer
            button.btn-cancel(type="button" @click="fechar") Cancelar
            button.btn-save(type="submit") Salvar
</template>

<script setup lang="ts">
import { ref, defineEmits } from 'vue'

const nomeCategoria = ref('')

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'salvar', nome: string): void
  (e: 'categoriaCriada', nome: string): void
}>()

function fechar() {
  emit('close')
}

function salvar() {
  const nome = nomeCategoria.value.trim()
  if (nome) {
    emit('salvar', nome)
    emit('categoriaCriada', nome) // EMITE AMBOS
    fechar()
    nomeCategoria.value = ''
  }
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
  max-width: 400px;
  width: 95%;
  padding: 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.modal-header h3 {
  font-size: 1.5rem;
  color: #000000;
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

label {
  font-weight: 600;
  color: #000;
  margin-top: 1rem;
  display: block;
  font-size: 0.95rem;
}

input {
  width: 100%;
  padding: 0.6rem 0.75rem;
  margin-top: 0.3rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 1rem;
  background-color: #ffffff;
  transition:
    border-color 0.3s ease,
    background-color 0.3s ease;
}

input:focus {
  outline: none;
  border-color: #5e5f26;
  background-color: #fff;
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
  padding: 0.7rem 1.4rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  color: #000000;
  transition: background-color 0.2s ease;
  
}

.btn-cancel:hover {
  background-color: #dfdfdf;
}

.btn-save {
  background-color: #aec437;
  border: none;
  padding: 0.6rem 1.6rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 700;
  color: rgb(0, 0, 0);
  transition: background-color 0.2s ease;
}

.btn-save:hover {
  background-color: #d0d888;
}
</style>
