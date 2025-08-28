<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import {
  isLoggedIn as loggedInRef,
  usuarioLogado as usuarioRef,
  setLoggedIn
} from '@/stores/authState'
import { useRouter } from 'vue-router'
import logoImg from '@/assets/logocjl.png'
import userImg from '@/assets/icone.png'

const router = useRouter()
const userDropdownOpen = ref(false)
const isMenuOpen = ref(false)

// Computed para facilitar o template
const isLoggedIn = computed(() => loggedInRef.value)
const usuario = computed(() => usuarioRef.value)

// Nome e role do usuário
const nomeUsuario = computed(() => usuario.value?.nome || 'Usuário')
const roleUsuario = computed(() => usuario.value?.role?.name || '')

// Toggle dropdown
function toggleUserDropdown() {
  userDropdownOpen.value = !userDropdownOpen.value
}

// Toggle mobile menu
function toggleMenu() {
  isMenuOpen.value = !isMenuOpen.value
}

// Logout
function logout() {
  console.log('Logout acionado')
  localStorage.removeItem('authToken')
  setLoggedIn(false)
  usuarioRef.value = null
  router.push({ name: 'Login' })
}

// Fecha dropdown ao clicar fora
function handleClickOutside(event: MouseEvent) {
  const dropdown = document.getElementById('user-dropdown')
  const photo = document.getElementById('user-photo')
  if (
    dropdown &&
    !dropdown.contains(event.target as Node) &&
    photo &&
    !photo.contains(event.target as Node)
  ) {
    userDropdownOpen.value = false
  }
}

document.addEventListener('click', handleClickOutside)

// Função helper para verificar roles
function hasRole(requiredRoles: string[]) {
  const userRole = usuario.value?.role?.name
  if (!userRole) return false
  if (userRole === 'ADMIN') return true
  return requiredRoles.includes(userRole)
}

// Watch para depurar mudanças
watch(usuario, (newVal) => {})
watch(isLoggedIn, (val) => {})
</script>

<template lang="pug">
div.layout-wrapper
  header.fixed-header
    .wrapper
      img.logo(:src="logoImg" alt="Logo CJL" width="60" height="60")

    nav.menu-desktop
      template(v-if="isLoggedIn")
        RouterLink(to="/") Início

        // Mostrar somente se o usuário tiver acesso
        RouterLink(to="/estoque" v-if="hasRole(['ADMINISTRATIVO'])") Estoque
        RouterLink(to="/cadastro-usuario" v-if="hasRole(['ADMINISTRATIVO'])") Cadastro de Usuários
        RouterLink(to="/cadastro-cliente" v-if="hasRole(['USUARIO'])") Cadastro de Clientes
        RouterLink(to="/cadastro-veiculo" v-if="hasRole(['USUARIO'])") Cadastro de Veiculos
        RouterLink(to="/admin" v-if="hasRole(['ADMIN'])") Administração

      template(v-else)
        RouterLink(to="/") Início

    .auth-buttons
      template(v-if="isLoggedIn")
        .user-menu
          img.user-photo#user-photo(
            :src="userImg"
            :alt="nomeUsuario"
            @click="toggleUserDropdown"
            tabindex="0"
            @keydown.enter.prevent="toggleUserDropdown"
            role="button"
            aria-haspopup="true"
            :aria-expanded="userDropdownOpen"
          )
          div.user-dropdown-google#user-dropdown(v-show="userDropdownOpen")
            h3.ola-msg Olá, {{ nomeUsuario }}!
            button.gerenciar-conta Gerenciar sua Conta CJL
            hr
            button.adicionar-conta Visualizar Licenças
            button.sair(@click="logout") Sair
            .links-google
              a(href="#", target="_blank") Política de Privacidade
              span ·
              a(href="#", target="_blank") Termos de Serviço
      template(v-else)
        a.external-btn.link-btn(href="/login") Login

    button.hamburguer(@click="toggleMenu") ☰

    nav.menu-mobile(v-if="isMenuOpen")
      template(v-if="isLoggedIn")
        RouterLink(to="/") Início
        RouterLink(to="/estoque" v-if="hasRole(['ADMINISTRATIVO'])") Estoque
        RouterLink(to="/cadastro-usuario" v-if="hasRole(['ADMINISTRATIVO'])") Cadastro de Usuários
        RouterLink(to="/cadastro-cliente" v-if="hasRole(['USUARIO'])") Cadastro de Clientes
        RouterLink(to="/cadastro-veiculo" v-if="hasRole(['USUARIO'])") Cadastro de Veiculos
        RouterLink(to="/admin" v-if="hasRole(['ADMIN'])") Administração

        .mobile-auth-buttons
          button(@click="logout") Sair
      template(v-else)
        RouterLink(to="/") Início
        .mobile-auth-buttons
          a.external-btn.link-btn(href="/login") Login

  main.main-content
    RouterView

  footer.fixed-footer
    p © 2025 - Todos os direitos reservados
</template>

<style scoped>
.user-dropdown-google {
  position: absolute;
  top: 50px;
  right: 0;
  width: 280px;
  background-color: #ffffff; /* fundo branco */
  color: #000000; /* letras pretas sempre */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  border-radius: 12px;
  padding: 1rem;
  z-index: 9999;
  text-align: center;
  font-family: 'Arial', sans-serif;
}

.foto-perfil-google {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  margin: auto;
}

.ola-msg {
  margin: 0.8rem 0 0.6rem;
  font-size: 1rem;
  font-weight: bold;
  color: #000; /* sempre preto */
}

.gerenciar-conta,
.adicionar-conta,
.sair {
  background: none;
  border: 1px solid #1a73e8;
  color: #000; /* sempre preto */
  padding: 0.5rem;
  width: 100%;
  font-size: 0.9rem;
  border-radius: 5px;
  margin-bottom: 0.5rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.gerenciar-conta:hover,
.adicionar-conta:hover,
.sair:hover {
  background-color: rgba(0, 0, 0, 0.05); /* leve cinza no hover */
  color: #000 !important; /* garante preto sempre */
}

/* Links de política/termos */
.links-google {
  margin-top: 1rem;
  font-size: 0.75rem;
  display: flex;
  justify-content: center;
  gap: 4px;
}

.links-google a {
  color: #000000 !important; /* sempre preto */
  text-decoration: none;
}

.links-google a:hover {
  text-decoration: underline;
  color: #000000 !important; /* sempre preto */
}

.fixed-footer {
  position: relative;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: rgba(0, 0, 0, 0.692);
  color: rgb(0, 0, 0);
  text-align: center;
  padding: 1rem;
  z-index: 1000;
}

:global(html, body) {
  margin: 0;
  padding: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  box-sizing: border-box;
}
/* Estilo para os links (Login e Registrar) */
a.external-btn.link-btn {
  border: 1px solid white;
  color: white;
  background: transparent;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  text-decoration: none;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

a.external-btn.link-btn:hover {
  background-color: #ccc; /* fundo cinza */
  color: #000;
  border-color: white; /* borda branca no hover */
}

/* Estilo padrão para o botão Convivium */
button.external-btn {
  /* Aqui fica o estilo original do seu botão convivium */
  background-color: #007bff; /* Exemplo: azul */
  color: white;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}
a.external-btn.link-btn,
button.external-btn {
  padding: 0.25rem 1rem; /* menos altura, mesma largura */
  font-size: 0.9rem; /* opcional: deixa o texto um pouco menor */
  border-radius: 4px;
  cursor: pointer;
  transition:
    background-color 0.3s ease,
    border-color 0.3s ease;
}

button.external-btn:hover {
  background-color: #0056b3; /* azul mais escuro no hover */
}

.layout-plataforma .user-dropdown-google {
  position: absolute;
  top: 60px;
  right: 0;
  width: 280px;
  background: white;
  color: #202124;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  border-radius: 20px;
  padding: 1.5rem 1rem;
  z-index: 9999;
  text-align: center;
  font-family: 'Arial', sans-serif;
}

.layout-plataforma .foto-perfil-google {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 50%;
  margin: auto;
}

.layout-plataforma .ola-msg {
  margin: 0.8rem 0 0.6rem;
  font-size: 1rem;
  font-weight: bold;
}

.layout-plataforma .gerenciar-conta {
  background-color: transparent;
  border: 1px solid #1a73e8;
  color: #000000;
  padding: 0.5rem;
  width: 100%;
  font-size: 0.9rem;
  border-radius: 5px;
  margin-bottom: 1rem;
  cursor: pointer;
}
.layout-plataforma .gerenciar-conta:hover {
  background-color: #f1ffff; /* amarelo claro */
  color: #000; /* texto preto */
}

.layout-plataforma .adicionar-conta,
.layout-plataforma .sair {
  background: none;
  border: none;
  color: #1a73e8;
  padding: 0.4rem 0;
  width: 100%;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.links-google a:hover {
  background-color: transparent !important;
  box-shadow: none !important;
  border-radius: 0 !important;
  text-decoration: underline;
}

.layout-plataforma .adicionar-conta:hover,
.layout-plataforma .sair:hover {
  background-color: rgba(60, 64, 67, 0.08);
  border-radius: 5px;
}

.layout-plataforma .links-google {
  margin-top: 1.2rem;
  font-size: 0.75rem;
  color: #5f6368;
  display: flex;
  justify-content: center;
  gap: 4px;
}

.layout-plataforma .links-google a {
  color: #5f6368;
  text-decoration: none;
}

.layout-plataforma .links-google a:hover {
  text-decoration: underline;
}

.layout-plataforma .user-photo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgb(0, 0, 0);
  transition: box-shadow 0.3s ease;
}

.layout-plataforma .user-photo:hover,
.layout-plataforma .user-photo:focus {
  box-shadow: 0 0 8px 2px #ffe920;
  outline: none;
}

.layout-plataforma .user-menu {
  position: relative;
  margin-left: auto;
  cursor: pointer;
}

/* Botão de sair (header da plataforma) */
.btn-logoff {
  margin: auto;
  background-color: #ff1900;
  color: white;
  border: none;
  padding: 0.4rem 1rem;
  font-size: 1rem;
  border-radius: 5px;
  font-weight: 400; /* menos grossa */
  cursor: pointer;
  transition: background-color 0.3s ease;
  align-items: center;
}

.btn-logoff:hover {
  background-color: #c0392b;
}

/* Foto do usuário redonda e dropdown */
.user-menu {
  position: relative;
  margin-left: auto;
  cursor: pointer;
}

.user-photo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgb(0, 0, 0);
  transition: box-shadow 0.3s ease;
}

.user-photo:hover,
.user-photo:focus {
  box-shadow: 0 0 8px 2px #18ce00;
  outline: none;
}

.user-dropdown {
  position: absolute;
  right: 0;
  top: 50px;
  background-color: rgba(0, 0, 0, 0.85);
  padding: 1rem;
  border-radius: 8px;
  color: white;
  width: 220px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
  z-index: 1100;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.user-dropdown p {
  margin: 0;
  font-size: 0.9rem;
  line-height: 1.2;
  word-break: break-word; /* força quebra de palavra para não vazar */
  white-space: normal; /* permite quebra de linha */
  overflow-wrap: break-word; /* garante quebra em palavras longas */
  max-width: 100%; /* limita largura máxima ao container */
}

.user-dropdown p.nome {
  font-weight: 600;
}

/* Mantém os outros estilos originais sem alterações */

@media (max-width: 768px) {
  .hamburguer {
    display: block;
  }

  .menu-desktop {
    display: none;
  }

  .auth-buttons {
    display: none !important;
  }

  .menu-mobile {
    flex-direction: column;
    align-items: center;
  }

  .menu-mobile::after {
    content: '';
    display: block;
    margin: 10px 0;
  }

  .menu-mobile .mobile-auth-buttons {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-top: 20px;
  }

  .menu-mobile .mobile-auth-buttons button {
    background-color: transparent;
    border: 1px solid white;
    color: white;
    padding: 0.4rem 1rem;
    font-size: 1rem;
    border-radius: 4px;
    font-weight: 600;
    transition:
      background-color 0.3s,
      color 0.3s;
    cursor: pointer;
  }

  .menu-mobile .mobile-auth-buttons button:hover {
    background-color: white;
    color: #1e1e1e;
  }

  .menu-mobile .mobile-auth-buttons button:last-child {
    border: 1px solid #ffd700;
    color: white;
  }

  .menu-mobile .mobile-auth-buttons button:last-child:hover {
    background-color: #ffd700;
    color: #1e1e1e;
  }
}

.auth-buttons .external-btn {
  background-color: transparent;
  border: 1px solid #ffd700;
  color: #ffffff;
  padding: 0.4rem 1rem;
  font-size: 1rem;
  cursor: pointer;
  border-radius: 4px;
  transition:
    background-color 0.3s,
    color 0.3s,
    border-color 0.3s;
  font-weight: 600;
}

.auth-buttons .external-btn:hover,
.auth-buttons .external-btn:focus {
  background-color: #ffd700;
  color: #1e1e1e;
  border-color: #ffd700;
}

.hamburguer {
  display: none;
  font-size: 28px;
  background: none;
  border: none;
  cursor: pointer;
  color: white;
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1100;
}

.menu-desktop a {
  color: white;
  font-weight: 600;
  font-size: 1.1rem;
  text-decoration: none;
  transition: color 0.3s;
  cursor: pointer;
}

.menu-desktop a:hover,
.menu-desktop a:focus {
  color: #a5a5a5;
  background: transparent;
  outline: none;
  box-shadow: none;
  text-shadow: none;
}

.menu-desktop {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 2rem;
  z-index: 5;
}

.menu-mobile a:hover,
.menu-mobile a:focus {
  background: transparent !important;
  outline: none !important;
  box-shadow: none !important;
}

.menu-mobile {
  position: fixed;
  top: 80px;
  left: 0;
  right: 0;
  width: 100vw;
  height: auto;
  background-color: rgba(0, 0, 0, 0.692);
  padding: 20px 0;
  z-index: 10000;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  box-shadow: none;
  margin: 0;
  box-sizing: border-box;
}

.menu-mobile a {
  color: white;
  font-size: 1rem;
  font-weight: 500;
  text-decoration: none;
  padding: 10px 0;
  text-align: center;
  border-bottom: 2px solid transparent;
  transition:
    color 0.3s,
    border-color 0.3s;
  display: inline-block;
}

.menu-mobile a:hover {
  color: #a5a5a5;
  border-bottom: 2px solid rgb(187, 187, 187);
}

.menu-mobile a.router-link-exact-active {
  border-bottom: none !important;
}

.layout-wrapper {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  width: 100%;
  margin: 0;
  padding: 0;
  background-size: cover;
  position: relative;
}

.layout-wrapper > * {
  position: relative;
  z-index: 1;
}

.fixed-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 70px;
  background-color: rgba(0, 0, 0, 0.7) !important;
  color: white;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  z-index: 1000;
  box-sizing: border-box;
}

.fixed-header.scrolled {
  background-color: rgba(66, 66, 66, 0.95);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.logo {
  width: 130px;
  height: 130px;
  cursor: pointer;
  transform: scale(1);
  transform-origin: center;
  flex-shrink: 0;
  z-index: 10;
  margin-top: 10px;
}

.wrapper {
  position: relative;
  width: 100%;
  display: flex;
  height: 50px;
  align-items: center;
}

nav.menu a {
  color: white;
  font-weight: 600;
  font-size: 1.1rem;
  text-decoration: none;
  outline: none;
  transition: color 0.3s;
  cursor: pointer;
}

nav.menu a:hover,
nav.menu a:focus {
  color: #a5a5a5;
  outline: none !important;
  box-shadow: none !important;
  background: transparent !important;
  text-shadow: none !important;
}

nav a.router-link-exact-active {
  font-weight: bold;
  border-bottom: 2px solid rgb(187, 187, 187);
}

.auth-buttons {
  margin-left: auto;
  display: flex;
  gap: 1rem;
  z-index: 10;
}

.auth-buttons button {
  background-color: transparent;
  border: 1px solid white;
  padding: 0.4rem 1rem;
  font-size: 1rem;
  cursor: pointer;
  border-radius: 4px;
  transition:
    background-color 0.3s,
    color 0.3s;
}

.auth-buttons button:hover {
  background-color: white;
  color: #1e1e1e;
}

.main-content {
  flex: 1;
  padding-top: 80px;
  padding-bottom: 60px;
  width: 100%;
  margin: 0;
  box-sizing: border-box;
}

.fixed-footer {
  position: relative;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: rgba(0, 0, 0, 0.692);
  color: white;
  text-align: center;
  padding: 1.1rem 1rem; /* 1.5rem em cima e embaixo, 1rem nas laterais */
  z-index: 1000;
  margin-top: -60px;
}

:global(html, body) {
  margin: 0;
  padding: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  box-sizing: border-box;
}
</style>
