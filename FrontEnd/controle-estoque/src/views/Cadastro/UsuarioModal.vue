<template lang="pug">
div.modal-overlay(v-if="isOpen")
  div.modal-wrapper
    button.close-btn(@click="fechar") ×
    h2.modal-title {{ title }}

    form(@submit.prevent="submitForm" @input="mensagem = ''")
      fieldset
        legend Dados Pessoais
        label(for="nome") Nome completo *
        input#nome(type="text" v-model="nome" placeholder="Nome completo" required)

        label(for="email") Email *
        input#email(type="email" v-model="email" placeholder="email@exemplo.com" required)

        label(for="telefone") Telefone *
        input#telefone(type="tel" v-model="telefone" placeholder="(99) 99999-9999" required)

        label(for="cpf") CPF *
        input#cpf(type="text" v-model="cpfFormatado" maxlength="14" placeholder="000.000.000-00" required readonly)

      fieldset
        legend Endereço
        label(for="cep") CEP *
        input#cep(type="text" v-model="cep" placeholder="00000-000" maxlength="8" required)

        label(for="rua") Rua *
        input#rua(type="text" v-model="rua" placeholder="Rua" readonly)

        label(for="numero") Número *
        input#numero(type="text" v-model="numero" placeholder="Número" required)

        label(for="complemento") Complemento
        input#complemento(type="text" v-model="complemento" placeholder="Complemento")

        label(for="bairro") Bairro *
        input#bairro(type="text" v-model="bairro" placeholder="Bairro" readonly)

        label(for="cidade") Cidade *
        input#cidade(type="text" v-model="cidade" placeholder="Cidade" readonly)

        label(for="estado") Estado *
        input#estado(type="text" v-model="estado" placeholder="Estado" readonly)

      fieldset
        legend Permissão / Cargo *
        select#role(v-model="roleId" required)
          option(disabled value="") Selecione a permissão
          option(v-for="r in roles" :key="r.id" :value="r.id") {{ r.name }}

      button.submit-btn(type="submit") {{ submitLabel }}
      p.mensagem(v-if="mensagem") {{ mensagem }}
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import { listarRoles } from '@/services/roleService'
import { buscarEnderecoPorCep } from '@/services/viacepService'
import { cadastrarUsuario } from '@/services/authService'
import { usuarioLogado } from '@/stores/authState'
import type { UsuarioDTO } from '@/types/UsuarioDTO'

const toast = useToast()

// Props
const props = defineProps<{
  isOpen: boolean
  onClose: () => void
  usuario?: UsuarioDTO | null
  title?: string
  submitLabel?: string
  isAtualizacao?: boolean // nova prop
}>()

const emit = defineEmits(['saved'])

// 🔹 Campos do formulário
const nome = ref('')
const email = ref('')
const telefone = ref('')
const cpf = ref('')
const cep = ref('')
const rua = ref('')
const numero = ref('')
const complemento = ref('')
const bairro = ref('')
const cidade = ref('')
const estado = ref('')
const roleId = ref<number | null>(null)
const isAtualizacao = ref(false)
const roles = ref<{ id: number; name: string }[]>([])
const mensagem = ref('')

// Carrega roles do backend
onMounted(async () => {
  try {
    roles.value = await listarRoles()
  } catch {
    toast.error('Erro ao carregar permissões.')
  }
})

// CPF formatado
const cpfFormatado = computed({
  get: () => {
    const cpfNumeros = cpf.value.replace(/\D/g, '')
    let formatado = cpfNumeros
    if (cpfNumeros.length > 3) formatado = cpfNumeros.replace(/(\d{3})(\d)/, '$1.$2')
    if (cpfNumeros.length > 6) formatado = formatado.replace(/(\d{3})(\d)/, '$1.$2')
    if (cpfNumeros.length > 9) formatado = formatado.replace(/(\d{3})(\d{1,2})$/, '$1-$2')
    return formatado
  },
  set(valor: string) {
    cpf.value = valor.replace(/\D/g, '')
  }
})

// Preencher endereço pelo CEP
watch(cep, async (novoCep) => {
  if (novoCep.length === 8) {
    try {
      const data = await buscarEnderecoPorCep(novoCep)
      if (!data.erro) {
        rua.value = data.logradouro || ''
        bairro.value = data.bairro || ''
        cidade.value = data.localidade || ''
        estado.value = data.uf || ''
        mensagem.value = ''
      } else {
        toast.error('CEP não encontrado.')
      }
    } catch {
      toast.error('Erro ao buscar CEP.')
    }
  }
})

// Watch para preencher ou limpar formulário ao abrir o modal
watch(
  () => props.usuario,
  (usuario) => {
    if (usuario) {
      nome.value = usuario.nome
      email.value = usuario.email
      telefone.value = usuario.telefone || ''
      cpf.value = usuario.cpf
      roleId.value = usuario.roleId || null

      cep.value = usuario.endereco?.cep || ''
      rua.value = usuario.endereco?.rua || ''
      numero.value = usuario.endereco?.numero || ''
      complemento.value = usuario.endereco?.complemento || ''
      bairro.value = usuario.endereco?.bairro || ''
      cidade.value = usuario.endereco?.cidade || ''
      estado.value = usuario.endereco?.estado || ''

      isAtualizacao.value = !!usuario
    } else {
      limparFormulario()
    }
  },
  { immediate: true }
)

function limparFormulario() {
  nome.value = ''
  email.value = ''
  telefone.value = ''
  cpf.value = ''
  cep.value = ''
  rua.value = ''
  numero.value = ''
  complemento.value = ''
  bairro.value = ''
  cidade.value = ''
  estado.value = ''
  roleId.value = null
  isAtualizacao.value = false
  mensagem.value = ''
}

// Submit do formulário
async function submitForm() {
  if (
    !nome.value ||
    !email.value ||
    !telefone.value ||
    !cpf.value ||
    !cep.value ||
    !numero.value ||
    !roleId.value
  ) {
    toast.error('Preencha todos os campos obrigatórios.')
    return
  }

  const empresaId = usuarioLogado.value?.empresaId
  if (!empresaId) {
    toast.error('Não foi possível determinar a empresa do usuário logado.')
    return
  }
  try {
    await cadastrarUsuario({
      nome: nome.value,
      email: email.value,
      telefone: telefone.value,
      cpf: cpf.value,
      empresaId: empresaId,
      ativo: true,
      endereco: {
        cep: cep.value,
        rua: rua.value,
        numero: numero.value,
        complemento: complemento.value,
        bairro: bairro.value,
        cidade: cidade.value,
        estado: estado.value
      },
      roleId: roleId.value,
      atualizar: isAtualizacao.value
    })

    toast.success('Usuário salvo com sucesso!')
    emit('saved')
    fechar()
  } catch {
    toast.error('Erro ao salvar usuário.')
  }
}

function fechar() {
  limparFormulario()
  props.onClose()
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 1rem;
}
.modal-wrapper {
  background-color: #fff;
  border-radius: 16px;
  max-width: 700px;
  width: 100%;
  padding: 2rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  position: relative;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  overflow-y: auto; /* habilita a rolagem vertical quando necessário */
}
.close-btn {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: #e74c3c;
  color: white;
  border: none;
  font-size: 1.5rem;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.close-btn:hover {
  background: #c0392b;
}
.modal-title {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #2c3e50;
  font-size: 1.8rem;
  font-weight: bold;
}
fieldset {
  border: 1px solid #42b983;
  border-radius: 12px;
  padding: 1.5rem 2rem;
  margin-bottom: 1.8rem;
  background-color: #f9fcfb;
}
legend {
  font-weight: 600;
  font-size: 1.1rem;
  color: #2c3e50;
}
label {
  display: block;
  margin-top: 1rem;
  font-weight: 500;
  font-size: 0.95rem;
  color: #34495e;
}
input,
select {
  width: 100%;
  padding: 0.7rem 0.9rem;
  margin-top: 0.3rem;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 1rem;
  transition: 0.3s ease;
  background-color: white;
}
input:focus,
select:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 8px #42b98360;
}
.submit-btn {
  margin-top: 1.5rem;
  padding: 0.9rem;
  width: 100%;
  border: none;
  border-radius: 12px;
  background-color: #42b983;
  color: white;
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
  transition: background-color 0.25s ease;
}
.submit-btn:hover {
  background-color: #369b70;
}
.mensagem {
  color: #e74c3c;
  margin-top: 1rem;
  font-weight: 600;
  text-align: center;
}
.buscar-btn {
  margin-top: 0.5rem;
  padding: 0.5rem 0.8rem;
  border-radius: 8px;
  border: none;
  background-color: #3498db;
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: 0.25s ease;
}
.buscar-btn:hover {
  background-color: #2980b9;
}
.cpf-busca {
  margin-bottom: 1rem;
  display: flex;
  gap: 0.5rem;
  align-items: flex-end;
}
.cpf-busca input {
  flex: 1;
}
</style>
