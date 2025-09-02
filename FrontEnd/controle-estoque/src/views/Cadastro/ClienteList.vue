<template lang="pug">
section.tela-inteira
  .listagem-wrapper
    h2 Usuários

    .top-actions
      input.search-input(type="text" placeholder="Pesquisar por nome, email ou CPF" v-model="filtro")
      button.add-btn(@click="abrirModalCriar") Adicionar Usuário

    table
      thead
        tr
          th Nome
          th Email
          th Telefone
          th CPF
          th Permissão
          th Ações
      tbody
        tr(v-for="u in usuariosFiltrados" :key="u.id" @click="abrirModalVisualizar(u)" class="clickable-row")
          td {{ u.nome }}
          td {{ u.email }}
          td {{ u.telefone || '-' }}
          td {{ u.cpf }}
          td {{ rolesMap[u.roleId] || '-' }}
          td
            button.edit-btn(@click.stop="editarUsuario(u)") Editar
            button.delete-btn(@click.stop="abrirModalExcluir(u)") Excluir

      // Modal criação
    ClienteModalCriar(
      v-if="modalAberto && !isAtualizacao"
      @saved="carregarUsuarios"
      :isOpen="modalAberto"
      @fechar="fecharModal"
      :closeOnOverlayClick="false"
      :closeOnEsc="false"
    )

    // Modal edição
    ClienteModalEditar(
      v-if="modalAberto && isAtualizacao"
      :usuario="usuarioSelecionado"
      @saved="carregarUsuarios"
      :isOpen="modalAberto"
      @fechar="fecharModal"
      :closeOnOverlayClick="false"
      :closeOnEsc="false"
    )

  // Modal visualização (somente leitura)
  // Modal visualização (somente leitura)
  div.modal-overlay(v-if="modalVisualizacaoAberto")
    div.modal-wrapper.read-only
      button.btn-fechar(@click="modalVisualizacaoAberto = false") ×
      h3 Dados do Usuário
      p
        b Nome: 
        | {{ usuarioSelecionado?.nome }}
      p
        b Email: 
        | {{ usuarioSelecionado?.email || '-' }}
      p
        b Telefone: 
        | {{ usuarioSelecionado?.telefone || '-' }}
      p
        b CPF: 
        | {{ usuarioSelecionado?.cpf }}
      p
        b Permissão: 
        | {{ rolesMap[usuarioSelecionado?.roleId] || '-' }}
      p(v-if="usuarioSelecionado?.empresa")
        b Empresa: 
        | {{ usuarioSelecionado.empresa.nome }}


    // Modal exclusão
  div.modal-overlay(v-if="modalExcluirAberto")
    div.modal-wrapper
      h3 Confirmação
      p
        | Tem certeza que deseja excluir o usuário 
        strong {{ usuarioParaExcluir?.nome }}
        |  (CPF: 
        strong {{ usuarioParaExcluir?.cpf }}
        | )?

      div.actions
        button.cancel-btn(@click="cancelarExclusao") Cancelar
        button.confirm-btn(@click="confirmarExclusao") Excluir

</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useToast } from 'vue-toastification'
import ClienteModalEditar from '@/views/Cadastro/ClienteEditarModal.vue'
import ClienteModalCriar from '@/views/Cadastro/CriarCliente.vue'
import { listarUsuarios, deletarUsuario, buscarUsuarioPorCpf } from '@/services/authService'
import type { UsuarioDTO } from '@/types/UsuarioDTO'
import { usuarioLogado } from '@/stores/authState'
import { listarRoles } from '@/services/roleService'
const roles = ref<{ id: number; name: string }[]>([])
const rolesMap = ref<Record<number, string>>({})
const toast = useToast()
const usuarios = ref<UsuarioDTO[]>([])
const modalAberto = ref(false)
const modalVisualizacaoAberto = ref(false)
const usuarioSelecionado = ref<UsuarioDTO | null>(null)
const isAtualizacao = ref(false)
const filtro = ref('')
const modalExcluirAberto = ref(false)
const usuarioParaExcluir = ref<UsuarioDTO | null>(null)

// Abrir modal criação
// Abrir modal criação
function abrirModalCriar() {
  usuarioSelecionado.value = null
  isAtualizacao.value = false
  modalAberto.value = true
}

// Abrir modal edição
async function editarUsuario(usuario: UsuarioDTO) {
  try {
    const empresaId = usuarioLogado.value?.empresaId
    if (!empresaId) {
      toast.error('Empresa do usuário logado não encontrada.')
      return
    }

    const dadosCompletos = await buscarUsuarioPorCpf(usuario.cpf, empresaId)
    usuarioSelecionado.value = { ...dadosCompletos }
    isAtualizacao.value = true
    modalAberto.value = true
  } catch {
    toast.error('Erro ao buscar dados do usuário.')
  }
}

// Abrir modal de visualização (somente leitura)
function abrirModalVisualizar(usuario: UsuarioDTO) {
  usuarioSelecionado.value = usuario
  modalVisualizacaoAberto.value = true
}

// Fechar modal criação/edição
function fecharModal() {
  carregarUsuarios()
  modalAberto.value = false
  usuarioSelecionado.value = null
}

// Carregar usuários
async function carregarUsuarios() {
  try {
    const empresaId = usuarioLogado.value?.empresaId
    if (!empresaId) return
    const lista = await listarUsuarios(empresaId, filtro.value)
    usuarios.value = lista
  } catch {
    toast.error('Erro ao carregar usuários.')
  }
}

// Filtro
const usuariosFiltrados = computed(() => {
  if (!filtro.value) return usuarios.value
  const term = filtro.value.toLowerCase()
  return usuarios.value.filter(
    (u) =>
      u.nome.toLowerCase().includes(term) ||
      u.email.toLowerCase().includes(term) ||
      u.cpf.includes(term)
  )
})

// Modal exclusão
function abrirModalExcluir(usuario: UsuarioDTO) {
  usuarioParaExcluir.value = usuario
  modalExcluirAberto.value = true
}

async function confirmarExclusao() {
  if (!usuarioParaExcluir.value) return
  try {
    await deletarUsuario(usuarioParaExcluir.value.cpf)
    toast.success(`Usuário ${usuarioParaExcluir.value.nome} excluído com sucesso.`)
    carregarUsuarios()
  } catch {
    toast.error('Erro ao excluir usuário.')
  } finally {
    modalExcluirAberto.value = false
    usuarioParaExcluir.value = null
  }
}

function cancelarExclusao() {
  modalExcluirAberto.value = false
  usuarioParaExcluir.value = null
}
onMounted(async () => {
  try {
    roles.value = await listarRoles()
    rolesMap.value = roles.value.reduce(
      (acc, r) => {
        acc[r.id] = r.name
        return acc
      },
      {} as Record<number, string>
    )

    carregarUsuarios()
  } catch {
    toast.error('Erro ao carregar tipos de perfil.')
  }
})
</script>
<style scoped>
.modal-wrapper p strong {
  font-weight: bold !important; /* força o negrito */
}

.modal-wrapper h3{
  color: #000000!important;
  font-weight: bold;
  margin-bottom: 1rem;
}
.modal-wrapper p{
  color: #000000!important;
  margin-bottom: 1rem;
}
/* Modal visualização somente leitura */
.modal-wrapper.read-only {
  background-color: #f9f9f9;
  padding: 2rem;
  border-radius: 12px;
  max-width: 450px;
  width: 90%;
  text-align: left;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.25);
  color: #1a1a1a;
}

.modal-wrapper.read-only h3 {
  text-align: center;
  margin-bottom: 1rem;
  font-size: 1.5rem;
  color: #000000;
}

.modal-wrapper.read-only p {
  margin: 0.5rem 0;
  font-size: 1rem;
}

.modal-wrapper.read-only b {
  font-weight: 600;
  color: #34495e;
}

.clickable-row {
  cursor: pointer;
}
.clickable-row:hover {
  background-color: #f0f8ff;
}
.tela-inteira {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  padding: 2rem;
  background: #f2f4f8;
}

.listagem-wrapper {
  width: 100%;
  max-width: 1000px;
  background-color: white;
  padding: 2rem 3rem;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

h2 {
  text-align: center;
  margin-bottom: 2rem;
  font-size: 2rem;
  color: #1a1a1a;
}

.top-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.search-input {
  width: 60%;
  padding: 0.6rem 0.75rem;
  border-radius: 8px;
  border: 1px solid #000000;
  font-size: 1rem;
  outline: none;
}
.search-input:focus {
  border-color: #7a6e2a; /* cor desejada ao clicar/focar */
  box-shadow: 0 0 0 2px rgba(231, 200, 0, 0.322); /* opcional: sombra sutil */
}

.add-btn {
  padding: 0.6rem 1.2rem;
  background: #aec437;
  color: rgb(0, 0, 0);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: 0.25s;
}
.add-btn:hover {
  background: #d0d888;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
  font-size: 0.95rem;
}

thead {
  background-color: #222222;
  color: white;
}

th,
td {
  border: 1px solid #ccc;
  padding: 0.2rem;
  text-align: left;
}

tr:nth-child(even) {
  background-color: #f9f9f9;
}

.edit-btn,
.delete-btn {
  padding: 0.3rem 0.6rem;
  margin-right: 0.3rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.85rem;
}

.edit-btn {
  background-color: #61b961;
  color: rgb(0, 0, 0);
}
.edit-btn:hover {
  background-color: #91d691;
}

.delete-btn {
  background-color: #c0392b;
  color: white;
}
.delete-btn:hover {
  background-color: #ffc1c1;
  color: #000000;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-wrapper {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  max-width: 400px;
  width: 100%;
  text-align: center;
}

.actions {
  margin-top: 1.5rem;
  display: flex;
  justify-content: center; /* centraliza os botões horizontalmente */
  gap: 0.5rem;             /* mantém o espaçamento entre eles */
}



.cancel-btn {
  background-color: #ebebeb;  /* cor de fundo */
  padding: 0.7rem 1rem;
  border-radius: 5px;
  outline: none;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.cancel-btn:hover {
  background-color: #dfdfdf; /* muda o fundo ao passar o mouse */
  color: #000;                /* opcional: cor do texto */
}

.confirm-btn {
  background-color: #c0392b;  /* cor de fundo */
  color: #fff;                /* texto branco */
  padding: 0.5rem 1rem;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.confirm-btn:hover {
  background-color: #ffc1c1; /* tom mais escuro ao passar o mouse */
  color: #000;
}

.modal-wrapper.read-only {
  position: relative;
  background-color: #f9f9f9;
  padding: 2rem 2.5rem;
  border-radius: 12px;
  max-width: 450px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  text-align: left;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.25);
  color: #1a1a1a;
  animation: fadeInScale 0.25s ease-out;
}

.modal-wrapper.read-only h3 {
  text-align: center;
  margin-bottom: 1rem;
  font-size: 1.5rem;
  color: #000000;
}

.modal-wrapper.read-only p {
  margin: 0.5rem 0;
  font-size: 1rem;
}

.modal-wrapper.read-only b {
  font-weight: 600;
  color: #34495e;
}

/* Botão fechar */
.btn-fechar {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  background-color: #f2d16b;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.2rem;
  color: #5c4a00;
  display: flex;
  align-items: center;
  justify-content: center;
  transition:
    background-color 0.2s ease,
    transform 0.15s ease;
}

.btn-fechar:hover {
  background-color: #e8c44c;
  transform: rotate(90deg);
}
</style>
