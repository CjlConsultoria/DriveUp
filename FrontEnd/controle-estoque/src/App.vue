<script setup lang="ts">
import { isLoggedIn, setLoggedIn, usuarioLogado } from '@/stores/authState'
import { useRouter } from 'vue-router'

const router = useRouter()

function goToLogin() {
  router.push({ name: 'Login' })
}

function logout() {
  localStorage.removeItem('authToken')
  setLoggedIn(false)
  // Limpa os dados do usuário também ao sair
  usuarioLogado.value = null
  router.push({ name: 'Login' })
}
</script>

<template lang="pug">
header.main-header
  .logo-wrapper
    img.logo(src="@/assets/logo.svg" alt="Logo")

  nav.nav-menu
    router-link(:to="{ name: 'Home' }") Home
    router-link(:to="{ name: 'Estoque' }") Estoque
    router-link(:to="{ name: 'CadastroUsuario' }") Cadastro de Usuario
    router-link(:to="{ name: 'CadastroCliente' }") Cadastro de Clientes
    router-link(:to="{ name: 'CadastroVeiculo' }") Cadastro de Usuario

  .login-wrapper
    button.btn-login(v-if="!isLoggedIn" @click="goToLogin") Login
    div.user-info(v-else)
      div.user-text
        span.user-name {{ usuarioLogado?.nome }}
        span.user-role {{ usuarioLogado?.role }}
      button.btn-logout(@click="logout") Sair


main
  router-view
</template>

<style scoped>
:global(html, body, #app) {
  height: 100%;
  margin: 0;
}

.main-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 2rem;
  background-color: #ffffff;
  border-bottom: 1px solid #e0e0e0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.03);
  height: 70px;
  box-sizing: border-box;
}

/* Layout dos elementos */
.logo-wrapper {
  flex: 1;
}
.logo {
  height: 40px;
  object-fit: contain;
}
.nav-menu {
  flex: 2;
  display: flex;
  justify-content: center;
  gap: 2rem;
}
.nav-menu a {
  text-decoration: none;
  color: #333;
  font-weight: 500;
  position: relative;
  padding-bottom: 4px;
  transition: color 0.3s;
}
.nav-menu a.router-link-exact-active {
  color: #007bff;
}
.nav-menu a::after {
  content: '';
  display: block;
  width: 0%;
  height: 2px;
  background: #007bff;
  transition: width 0.3s;
  position: absolute;
  bottom: 0;
  left: 0;
}
.nav-menu a:hover::after {
  width: 100%;
}
.login-wrapper {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}
.btn-login {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  font-weight: 500;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}
.btn-login:hover {
  background-color: #0056b3;
}

/* Faz o main ocupar o restante da altura da tela */
.main-container {
  flex: 1;
  overflow-y: auto;
  height: calc(100vh - 70px);
  background-color: #fafafa;
  padding: 0;
}

button.btn-login,
button.btn-logout {
  padding: 8px 16px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-weight: 600;
  background-color: #42b983;
  color: white;
  transition: background-color 0.3s ease;
}

button.btn-login:hover,
button.btn-logout:hover {
  background-color: #369b70;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1.5rem; /* espaçamento entre o texto e o botão */
  color: #2c3e50;
  font-weight: 600;
}

.user-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.user-name {
  font-size: 1rem;
  font-weight: 700;
}

.user-role {
  font-size: 0.85rem;
  color: #555;
  margin-top: 2px; /* pequeno espaçamento entre nome e cargo */
  align-self: center;
}

.btn-logout {
  margin-left: 1rem; /* separa o botão para direita */
  background-color: #42b983;
  border: none;
  padding: 0.4rem 0.8rem;
  border-radius: 5px;
  color: white;
  cursor: pointer;
  transition: background-color 0.25s ease;
  font-weight: 600;
}

.btn-logout:hover {
  background-color: #369b70;
}
</style>
