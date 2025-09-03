<script setup lang="ts">
import { defineEmits, ref } from 'vue'

const emit = defineEmits<{
  (e: 'close'): void
}>()

const email = ref('')

function sendResetLink() {
  if (!email.value) {
    alert('Preencha o email')
    return
  }
  // Aqui você chamaria a API para enviar o link
  alert(`Link de recuperação enviado para ${email.value}`)
  emit('close')
}
</script>

<template lang="pug">
.modal-backdrop(@click.self="emit('close')")
  .modal-content
    h3 Recuperar senha
    label(for="email") Email
    input#email(type="email" v-model="email" placeholder="Digite seu email" required)
    .btn-group
      button.btn-send(@click="sendResetLink") Enviar link
      button.btn-cancel(@click="emit('close')") Cancelar
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background-color: rgba(100, 100, 100, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1100;
  user-select: none;
  padding: 1rem;
  box-sizing: border-box;
}

.modal-content {
  background: #fff;
  border-radius: 16px;
  max-width: 400px;
  width: 100%;
  padding: 2.5rem 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  user-select: text;
}

h3 {
  margin: 0;
  font-weight: 700;
  font-size: 1.75rem;
  color: #2e2e2e;
  text-align: center;
}

label {
  font-weight: 600;
  font-size: 1rem;
  color: #2c3e50;
}

input[type='email'] {
  padding: 0.6rem 0.75rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  width: 100%;
  transition: border-color 0.3s ease;
  font-family: inherit;
}

input[type='email']:focus {
  outline: none;
  border-color: #575616;
  background-color: #fff;
}

.btn-group {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

button {
  flex: 1;
  padding: 0.7rem 0;
  font-weight: 700;
  font-size: 1.1rem;
  border-radius: 12px;
  border: none;
  cursor: pointer;
  transition:
    background-color 0.25s ease,
    box-shadow 0.25s ease,
    transform 0.15s ease;
  user-select: none;
}

.btn-send {
  background-color: #676e22;
  color: white;
  box-shadow: 0 8px 15px rgba(136, 134, 23, 0.3);
}

.btn-send:hover {
  background-color: #7c852d;
  box-shadow: 0 12px 20px rgba(110, 109, 31, 0.45);
  transform: translateY(-2px);
}

.btn-send:active {
  transform: translateY(0);
  box-shadow: 0 6px 10px rgba(158, 150, 37, 0.35);
}

.btn-cancel {
  background-color: #eee;
  color: #555;
  box-shadow: 0 5px 12px rgba(0, 0, 0, 0.1);
}

.btn-cancel:hover {
  background-color: #ddd;
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.15);
  transform: translateY(-1px);
}

.btn-cancel:active {
  transform: translateY(0);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

@media (max-width: 480px) {
  .modal-content {
    padding: 2rem 1.5rem;
  }
}
</style>
