<template lang="pug">
section.tela-inteira
  .listagem-wrapper
    h2 Usuários

    .top-actions
      input(type="text" placeholder="Pesquisar por nome, email ou CPF" v-model="filtro" class="search-input")
      button.add-btn(@click="abrirModal") Adicionar Usuário

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
        tr(v-for="u in usuariosFiltrados" :key="u.id")
          td {{ u.nome }}
          td {{ u.email }}
          td {{ u.telefone || '-' }}
          td {{ u.cpf }}
          td {{ u.role?.name || '-' }}
          td
            button.edit-btn(@click="editarUsuario(u)") Editar
            button.delete-btn(@click="abrirModalExcluir(u)") Excluir

    UsuarioModal(
      :isOpen="modalAberto"
      :onClose="fecharModal"
      @saved="carregarUsuarios"
      :usuario="usuarioSelecionado"
      :isAtualizacao="!!usuarioSelecionado"   
      title="Usuário"
      submitLabel="Salvar"
    )

    div.modal-overlay(v-if="modalExcluirAberto")
      div.modal-wrapper
        h3 Confirmação
        p Tem certeza que deseja excluir o usuário {{ usuarioParaExcluir?.nome }} (CPF: {{ usuarioParaExcluir?.cpf }})?
        div.actions
          button.cancel-btn(@click="cancelarExclusao") Cancelar
          button.confirm-btn(@click="confirmarExclusao") Excluir

</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useToast } from 'vue-toastification'
import UsuarioModal from '@/views/Cadastro/UsuarioModal.vue'
import { listarUsuarios, deletarUsuario, buscarUsuarioPorCpf } from '@/services/authService'
import type { UsuarioDTO } from '@/types/UsuarioDTO'
import { usuarioLogado } from '@/stores/authState'

const toast = useToast()
const usuarios = ref<UsuarioDTO[]>([])
const modalAberto = ref(false)
const usuarioSelecionado = ref<UsuarioDTO | null>(null)
const isAtualizacao = ref(false)
const filtro = ref('')
const modalExcluirAberto = ref(false)
const usuarioParaExcluir = ref<UsuarioDTO | null>(null)

// Abrir modal vazio para criar
function abrirModal() {
  usuarioSelecionado.value = null
  isAtualizacao.value = false
  modalAberto.value = true
}

async function editarUsuario(usuario: UsuarioDTO) {
  try {
    const empresaId = usuarioLogado.value?.empresaId
    if (!empresaId) {
      toast.error('Empresa do usuário logado não encontrada.')
      return
    }

    const dadosCompletos = await buscarUsuarioPorCpf(usuario.cpf, empresaId)

    // Apenas passar o usuário completo para o modal
    usuarioSelecionado.value = { ...dadosCompletos }
    isAtualizacao.value = true
    modalAberto.value = true
  } catch {
    toast.error('Erro ao buscar dados do usuário.')
  }
}

// Fechar modal
function fecharModal() {
  modalAberto.value = false
}

// Carregar usuários da API
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

// Filtrar tabela
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

onMounted(() => {
  carregarUsuarios()
})
</script>

<style scoped>
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
  color: #2c3e50;
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
  border: 1px solid #ccc;
  font-size: 1rem;
}

.add-btn {
  padding: 0.6rem 1.2rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: 0.25s;
}
.add-btn:hover {
  background: #369b70;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
  font-size: 0.95rem;
}

thead {
  background-color: #42b983;
  color: white;
}

th,
td {
  border: 1px solid #ccc;
  padding: 0.7rem;
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
  background-color: #3498db;
  color: white;
}
.edit-btn:hover {
  background-color: #2980b9;
}

.delete-btn {
  background-color: #e74c3c;
  color: white;
}
.delete-btn:hover {
  background-color: #c0392b;
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
  justify-content: space-around;
}

.cancel-btn {
  background: #ccc;
  padding: 0.5rem 1rem;
  border-radius: 5px;
}

.confirm-btn {
  background: #e53935;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 5px;
}
</style>
