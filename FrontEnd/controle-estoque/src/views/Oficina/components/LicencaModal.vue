<template lang="pug">
div(v-if="visible" class="modal-overlay")
  div.modal-content
    h2.text-xl.font-bold.mb-4 Oficina
    form(@submit.prevent="salvar")
      label.block.mb-2 Nome
      input.border.rounded.px-2.py-1.w-full(v-model="localOficina.nome" required)
      label.block.mb-2 CNPJ
      input.border.rounded.px-2.py-1.w-full(v-model="localOficina.cnpj" required)
      label.block.mb-2 Telefone
      input.border.rounded.px-2.py-1.w-full(v-model="localOficina.telefone" required)
      div.flex.justify-end.gap-2.mt-4
        button.btn-secondary(type="button" @click="$emit('fechar')") Cancelar
        button.btn-primary(type="submit") Salvar
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  visible: boolean
  oficina: any
}>()
const emit = defineEmits(['fechar', 'salvar'])

const localOficina = ref({ nome: '', cnpj: '', telefone: '' })

watch(
  () => props.oficina,
  (newVal) => {
    if (newVal) localOficina.value = { ...newVal }
  },
  { immediate: true }
)

function salvar() {
  emit('salvar', localOficina.value)
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  background: #fff;
  color: #000;
  padding: 1.5rem;
  border-radius: 12px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
}
input {
  width: 100%;
  padding: 0.5rem;
  margin-bottom: 1rem;
  border-radius: 6px;
  border: 1px solid #333;
  color: #000;
}
button {
  padding: 0.5rem 1rem;
  border-radius: 6px;
  border: none;
  cursor: pointer;
}
.btn-primary {
  background: #000;
  color: #fff;
  font-weight: bold;
}
.btn-primary:hover {
  background: #333;
}
.btn-secondary {
  background: #f0f0f0;
  color: #000;
  font-weight: bold;
}
.btn-secondary:hover {
  background: #d9d9d9;
}
</style>
