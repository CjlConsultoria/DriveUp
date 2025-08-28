<template lang="pug">
.login-wrapper
  form(@submit.prevent="fazerLogin")
    h2 Login

    label(for="cpf") CPF
    input#cpf(type="cpf" v-model="cpf" placeholder="Digite seu CPF" required)

    label(for="password") Senha
    input#password(type="password" v-model="password" placeholder="Digite sua senha" required)

    button(type="submit") Entrar

    a.forgot-password(href="#" @click.prevent="openForgotModal") Esqueci a senha

  ForgotPasswordModal(v-if="showForgotModal" @close="closeForgotModal")
</template>
<script setup lang="ts">
import { ref } from 'vue'
import ForgotPasswordModal from './ForgotPasswordModal.vue'
import { useRouter } from 'vue-router'
import { login, buscarUsuarioLogado } from '@/services/authService'
import { setLoggedIn, setUsuarioLogado } from '@/stores/authState'

const cpf = ref('')
const password = ref('')
const showForgotModal = ref(false)
const router = useRouter()
async function fazerLogin() {
  try {
    const token = await login(cpf.value, password.value)
    setLoggedIn(true)
    localStorage.setItem('authToken', token)

    const dadosUsuario = await buscarUsuarioLogado()

    // Passa o usuário sem roleId dentro do objeto
    // roleId é passado como segundo argumento
    setUsuarioLogado(
      {
        id: dadosUsuario.id,
        nome: dadosUsuario.nome,
        cpf: dadosUsuario.cpf,
        email: dadosUsuario.email,
        empresaId: dadosUsuario.empresaId
      },
      dadosUsuario.roleId // roleMap será usado dentro de setUsuarioLogado
    )

    router.push({ name: 'Home' })
  } catch (error) {
    console.error('Erro no login')
  }
}

function openForgotModal() {
  showForgotModal.value = true
}

function closeForgotModal() {
  showForgotModal.value = false
}
</script>

<style scoped>
.login-wrapper {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  max-width: 400px;
  margin: 6rem auto;
  padding: 2.5rem 2rem;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  color: #2c3e50;
  user-select: none;
}

h2 {
  margin: 0;
  font-weight: 700;
  font-size: 1.75rem;
  text-align: center;
  color: #2c3e50;
}

label {
  font-weight: 600;
  color: #2c3e50;
  margin-top: 1rem;
  font-size: 1rem;
  display: block;
}

input[type='cpf'],
input[type='password'] {
  width: 100%;
  padding: 0.6rem 0.75rem;
  margin-top: 0.3rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #fff;
  transition: border-color 0.3s ease;
  font-family: inherit;
}

input[type='email']:focus,
input[type='password']:focus {
  outline: none;
  border-color: #42b983;
  background-color: #fff;
}

button[type='submit'] {
  margin-top: 1.5rem;
  padding: 0.8rem 1.5rem;
  background-color: #42b983;
  border: none;
  border-radius: 8px;
  font-weight: 700;
  color: white;
  font-size: 1.2rem;
  cursor: pointer;
  box-shadow: 0 8px 30px rgba(66, 185, 131, 0.3);
  transition:
    background-color 0.2s ease,
    box-shadow 0.2s ease;
  user-select: none;
}

button[type='submit']:hover {
  background-color: #369b70;
  box-shadow: 0 12px 20px rgba(54, 151, 112, 0.45);
  transform: translateY(-2px);
}

button[type='submit']:active {
  transform: translateY(0);
  box-shadow: 0 6px 10px rgba(54, 151, 112, 0.35);
}

a.forgot-password {
  margin-top: 1rem;
  font-size: 0.95rem;
  color: #28a745;
  cursor: pointer;
  text-align: center;
  text-decoration: underline;
  user-select: text;
  display: block;
  transition: color 0.3s ease;
}

a.forgot-password:hover {
  color: #218838;
}
</style>
