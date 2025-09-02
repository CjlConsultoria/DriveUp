<template lang="pug">
div.modal-container
  .modal-content
    h2 Editar Cliente
    button.btn-fechar(@click="$emit('fechar')") ×
    form(@submit.prevent="atualizar")
      fieldset
        legend Dados do Cliente
        label Nome *
        input(type="text" v-model="nome" required)

        label CPF/CNPJ *
        input(type="text" v-model="cpfCnpj" readonly maxlength="18")

        label Telefone *
        input(type="text" v-model="telefone" @input="formatarTelefone" required)

        label E-mail *
        input(type="email" v-model="email" readonly)

        label Tipo de Perfil *
        select(v-model="roleId" required)
          option(v-for="role in roles" :key="role.id" :value="role.id") {{ role.name }}

      fieldset
        legend Endereço
        label CEP *
        .cep-row
          input(type="text" v-model="cep" maxlength="9" @blur="buscarEndereco" @input="formatarCep" required)
          a.nao-sei(href="https://buscacepinter.correios.com.br/app/endereco/index.php" target="_blank") Não sei o CEP

        label Rua *
        input(type="text" v-model="rua" required)
        label Número *
        input(type="text" v-model="numero" required)
        label Bairro *
        input(type="text" v-model="bairro" required)
        label Cidade *
        input(type="text" v-model="cidade" required)
        label Estado *
        input(type="text" v-model="estado" required)

      .btn-group
        button.btn-primary(type="submit" :disabled="loading") Atualizar
        button.btn-secondary(type="button" @click="$emit('fechar')") Cancelar

  // overlay de loading
  div.loading-overlay(v-if="loading")
    div.spinner

</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import { cadastrarUsuario } from '@/services/authService'
import { buscarEnderecoPorCep } from '@/services/viacepService'
import { listarRoles } from '@/services/roleService'

const emit = defineEmits(['atualizar', 'fechar'])
const props = defineProps<{ usuario: any }>()
const toast = useToast()

const nome = ref('')
const cpfCnpj = ref('')
const telefone = ref('')
const email = ref('')
const cep = ref('')
const rua = ref('')
const numero = ref('')
const bairro = ref('')
const cidade = ref('')
const estado = ref('')
const roles = ref<{ id: number; name: string }[]>([])
const roleId = ref<number | null>(null)

onMounted(async () => {
  try {
    roles.value = await listarRoles()

    // seta roleId inicial do usuário
    roleId.value = props.usuario?.roleId || roles.value[0]?.id || 3
  } catch {
    toast.error('Erro ao carregar tipos de perfil.')
  }
})

watch(
  () => props.usuario,
  (u) => {
    if (!u) return
    nome.value = u.nome
    cpfCnpj.value = u.cpf || u.cpfCnpj || ''
    email.value = u.email
    telefone.value = u.telefone
    cep.value = u.endereco?.cep || ''
    rua.value = u.endereco?.rua || ''
    numero.value = u.endereco?.numero || ''
    bairro.value = u.endereco?.bairro || ''
    cidade.value = u.endereco?.cidade || ''
    estado.value = u.endereco?.estado || ''

    // 🔹 roleId vem direto do props
    roleId.value = u.roleId || roles.value[0]?.id || 3
  },
  { immediate: true }
)

function formatarCep() {
  cep.value = cep.value
    .replace(/\D/g, '')
    .replace(/^(\d{5})(\d{0,3})/, '$1-$2')
    .substring(0, 9)
}

function formatarTelefone() {
  telefone.value = telefone.value.replace(/\D/g, '')
  telefone.value =
    telefone.value.length <= 10
      ? telefone.value.replace(/(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3')
      : telefone.value.replace(/(\d{2})(\d{5})(\d{0,4})/, '($1) $2-$3')
}

async function buscarEndereco() {
  const cepLimpo = cep.value.replace(/\D/g, '')
  if (cepLimpo.length !== 8) return
  try {
    const r = await buscarEnderecoPorCep(cepLimpo)
    if (r.erro) throw new Error()
    rua.value = r.logradouro
    bairro.value = r.bairro
    cidade.value = r.localidade
    estado.value = r.uf
  } catch {
    toast.error('Erro ao buscar endereço.')
  }
}

const loading = ref(false)

async function atualizar() {
  if (!roleId.value) {
    toast.error('Selecione o tipo de perfil.')
    return
  }

  loading.value = true
  try {
    await cadastrarUsuario({
      nome: nome.value,
      email: email.value,
      telefone: telefone.value.replace(/\D/g, ''),
      cpf: cpfCnpj.value.replace(/\D/g, ''),
      empresaId: props.usuario.empresaId,
      roleId: roleId.value,
      ativo: props.usuario.ativo ?? true,
      endereco: {
        cep: cep.value.replace(/\D/g, ''),
        rua: rua.value,
        numero: numero.value,
        bairro: bairro.value,
        cidade: cidade.value,
        estado: estado.value
      },
      atualizar: true
    })

    toast.success('Cliente atualizado com sucesso!')
    emit('atualizar')
    emit('fechar')
  } catch {
    toast.error('Erro ao atualizar cliente.')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* Loading overlay */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 5000;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
}
/* Botão de fechar do modal */
.modal-content .btn-fechar {
  font-size: 1.8rem; /* aumenta o tamanho do X */
  font-weight: bold;  /* opcional, deixa mais destacado */
  background: #dddddd;
  border: none;
  cursor: pointer;
  color: #000;        /* cor do X */
  position: absolute;  /* se quiser posicionar no canto */
  top: 1rem;
  right: 1rem;
}

.spinner {
  border: 6px solid #f3f3f3;
  border-top: 6px solid #f2d16b;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.modal-container {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 3000;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  box-sizing: border-box;
  overflow-y: auto;
}

.modal-content {
  position: relative;
  width: 100%;
  max-width: 500px;
  background-color: #ffffff;
  border: 1px solid #e6e6e6;
  border-radius: 12px;
  padding: 1.3rem 1.6rem;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  max-height: 75vh; /* altura menor */
  overflow-y: auto;
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

.modal-content h2 {
  margin-top: 0;
  font-size: 1.6rem;
  color: #333;
  border-bottom: 1px solid #88712b;
  padding-bottom: 0.5rem;
}

.btn-fechar {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  background-color: #cebf94;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.2rem;
  color: #000000;
  display: flex;
  align-items: center;
  justify-content: center;
  transition:
    background-color 0.2s ease,
    transform 0.15s ease;
}
.btn-fechar:hover {
  background-color: #dddddd;
  transform: rotate(90deg);
}

fieldset {
  border: 1px solid #8a8100!important;
  border-radius: 8px;
  padding: 0.9rem 1.1rem;
  margin-bottom: 1rem;
  background-color: #fff;
}
legend {
  font-weight: bold;
  padding: 0 0.5rem;
  color: #000000;
}

label {
  display: block;
  margin-bottom: 0.4rem;
  font-size: 0.95rem;
  color: #555;
}

input,
select {
  width: 100%;
  padding: 0.55rem;
  margin-bottom: 1rem;
  border-radius: 6px;
  border: 1px solid #ccc;
  font-size: 0.95rem;
  background-color: #fff;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}
input:focus,
select:focus {
  outline: none;
  border-color: #61501e;
  box-shadow: 0 0 4px rgba(242, 209, 107, 0.6);
}

.cep-row {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0px;
  align-items: center;
}

.cep-row input {
  margin-bottom: 0px;
}
.nao-sei {
  background: #f8f1d2;
  color: #5c4a00;
  padding: 0.55rem 0.8rem;
  border-radius: 6px;
  font-size: 0.85rem;
  text-decoration: none;
  white-space: nowrap;
  transition: background-color 0.2s ease;
}
.nao-sei:hover {
  background-color: #f2e5a7;
}

.btn-group {
  display: flex;
  gap: 0.8rem;
  justify-content: flex-end;
}

.btn-group button {
  padding: 0.6rem 1.2rem;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-size: 0.95rem;
  transition:
    background-color 0.2s ease,
    transform 0.15s ease;
}

.btn-primary {
  background-color: #aec437;
  color: #000000;
  font-weight: bold;
}
.btn-primary:hover {
  background-color: #d0d888;
  transform: translateY(-1px);
}
.btn-secondary {
  background-color: #ebebeb;
  color: #000000;
  font-weight: bold;
}
.btn-secondary:hover {
  background-color: #dfdfdf;
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .modal-content {
    max-width: 90%;
    padding: 1rem 1.2rem;
  }
  .btn-group {
    flex-direction: column;
    gap: 0.5rem;
  }
  .btn-group button {
    width: 100%;
    font-size: 0.9rem;
  }
}
</style>
