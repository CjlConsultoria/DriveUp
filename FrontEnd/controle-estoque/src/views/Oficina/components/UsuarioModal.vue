<template lang="pug">
div(v-if="visible" class="modal-overlay")
  div.modal-content
    h2.text-xl.font-bold.mb-4 Usuários
    div(v-for="(usuario, index) in usuarios" :key="usuario.id" class="usuario-row")
      input(type="text" v-model="usuario.nome" :readonly="!usuario.editando" placeholder="Nome" required)
      input(type="text" v-model="usuario.cpf" :readonly="!usuario.editando" placeholder="CPF" required)
      input(type="email" v-model="usuario.email" :readonly="!usuario.editando" placeholder="Email" required)
      input(type="text" v-model="usuario.telefone" :readonly="!usuario.editando" placeholder="Telefone")
      select(v-model="usuario.roleId" :disabled="!usuario.editando")
        option(value="1") Admin
        option(value="2") Administrativo
        option(value="3") Usuário
      button.btn-secondary(type="button" @click="confirmarExclusao(usuario, index)") ×
      button.btn-primary(type="button" v-if="!usuario.editando" @click="editarUsuario(usuario)") ✏️
      button.btn-primary(type="button" v-else @click="salvarUsuario(usuario)") 💾
    button.btn-primary.mt-2(type="button" @click="adicionarUsuario") + Usuário
    div.modal-footer
      button.btn-secondary(type="button" @click="$emit('fechar')") Fechar

    // Modal de confirmação
    div.modal-confirm(v-if="usuarioParaExcluir")
      div.modal-confirm-content
        h3.text-lg.font-bold.mb-2 Confirmar Exclusão
        p.mb-1 Deseja realmente excluir o seguinte usuário?
        p.mb-1
          strong Nome: 
          | {{ usuarioParaExcluir.usuario.nome }}
        p.mb-2
          strong CPF: 
          | {{ usuarioParaExcluir.usuario.cpf || '-' }}
        div.flex.justify-end.gap-2.mt-4
          button.btn-secondary(@click="usuarioParaExcluir=null") Cancelar
          button.btn-primary(@click="excluirConfirmado") Confirmar

</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { listarUsuarios, salvarUsuarios, excluirUsuarioAPI } from '@/services/usuarioService'
import type { UsuarioDTO } from '@/services/usuarioService'

interface Usuario extends UsuarioDTO {
  editando?: boolean
  novo?: boolean
  cpf?: string
  telefone?: string
}

const props = defineProps<{
  visible: boolean
  oficina: { id: string } | null
}>()

const emit = defineEmits(['fechar', 'salvar'])

const usuarios = ref<Usuario[]>([])
const usuarioParaExcluir = ref<{ usuario: Usuario; index: number } | null>(null)

// Carrega usuários da API
async function carregarUsuarios() {
  if (!props.oficina?.id) return
  try {
    const lista = await listarUsuarios(props.oficina.id)
    usuarios.value = lista.map((u) => ({
      ...u,
      cpf: u.cpf,
      telefone: u.telefone,
      editando: false,
      novo: false
    }))
  } catch (err) {
    console.error('Erro ao carregar usuários', err)
  }
}

watch(
  () => props.visible,
  (novo) => {
    if (novo) carregarUsuarios()
  }
)

function adicionarUsuario() {
  usuarios.value.push({
    nome: '',
    cpf: '',
    email: '',
    telefone: '',
    roleId: '3',
    editando: true,
    novo: true
  })
}

function editarUsuario(usuario: Usuario) {
  usuario.editando = true
}

async function salvarUsuario(usuario: Usuario) {
  if (!props.oficina?.id) return
  try {
    const dto: UsuarioDTO = {
      nome: usuario.nome,
      cpf: usuario.cpf,
      email: usuario.email,
      telefone: usuario.telefone,
      ativo: usuario.ativo ?? true,
      empresaId: Number(props.oficina.id),
      roleId: usuario.roleId,
      roleNome: usuario.roleNome
    }

    const listaSalva = await salvarUsuarios(props.oficina.id, [dto])
    Object.assign(usuario, listaSalva[0])
    usuario.editando = false
    usuario.novo = false
  } catch (err) {
    console.error('Erro ao salvar usuário', err)
  }
}

// Abre modal de confirmação
function confirmarExclusao(usuario: Usuario, index: number) {
  usuarioParaExcluir.value = { usuario, index }
}

async function excluirConfirmado() {
  if (!usuarioParaExcluir.value) return
  const { usuario, index } = usuarioParaExcluir.value

  if (usuario.cpf) {
    try {
      // agora enviamos CPF + empresaId para excluir
      await excluirUsuarioAPI(usuario.cpf, props.oficina!.id)
    } catch (err) {
      console.error('Erro ao excluir usuário', err)
      usuarioParaExcluir.value = null
      return
    }
  }

  usuarios.value.splice(index, 1)
  usuarioParaExcluir.value = null
}
</script>

<style scoped>
.modal-confirm {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-confirm-content {
  background: #fff;
  padding: 2rem;
  border-radius: 10px;
  min-width: 350px;
  max-width: 400px;
  text-align: center;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
  animation: fadeInScale 0.2s ease-out;
}

.modal-confirm h3 {
  font-size: 1.25rem;
  margin-bottom: 1rem;
}

.modal-confirm p {
  font-size: 1rem;
  margin: 0.25rem 0;
}

.modal-confirm .flex button {
  min-width: 100px;
  padding: 0.5rem 1rem;
  font-size: 0.95rem;
}

.usuario-row {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}
.usuario-row input,
.usuario-row select {
  flex: 1;
  padding: 0.5rem;
  border-radius: 6px;
  border: 1px solid #333;
  color: #000;
  background-color: #fff;
  font-size: 0.95rem;
}
.usuario-row input[readonly],
.usuario-row select:disabled {
  background-color: #f5f5f5;
}
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
  background-color: #ffffff; /* fundo branco */
  color: #000000; /* texto preto */
  padding: 1.5rem;
  border-radius: 12px;
  width: 100%;
  max-width: 1300px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  animation: fadeInScale 0.25s ease-out;
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.97);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

h2 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

/* Linha de usuário */
.usuario-row {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.usuario-row input,
.usuario-row select {
  flex: 1;
  padding: 0.5rem;
  border-radius: 6px;
  border: 1px solid #333;
  color: #000;
  background-color: #fff;
  font-size: 0.95rem;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

.usuario-row input:focus,
.usuario-row select:focus {
  outline: none;
  border-color: #f2d16b;
  box-shadow: 0 0 4px rgba(242, 209, 107, 0.6);
}

/* Footer do modal */
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  margin-top: 1rem;
}

/* Botões */
button {
  border: none;
  border-radius: 6px;
  cursor: pointer;
  padding: 0.5rem 1rem;
  font-weight: bold;
  transition:
    background-color 0.2s ease,
    transform 0.15s ease;
}
.btn-primary {
  background-color: #000;
  color: #fff;
}
.btn-primary:hover {
  background-color: #333;
  transform: translateY(-1px);
}
.btn-secondary {
  background-color: #f0f0f0;
  color: #000;
}
.btn-secondary:hover {
  background-color: #d9d9d9;
  transform: translateY(-1px);
}
</style>
