<template lang="pug">
div.modal-container
  .modal-content
    h2 Cadastro de Cliente
    button.btn-fechar(@click="$emit('fechar')") ×
    form(@submit.prevent="cadastrar" @input="limparMensagem")
      fieldset
        legend Dados do Cliente

        label Nome *
        input(type="text" v-model="nome" required)

        label CPF/CNPJ *
        input(type="text" v-model="cpfCnpj" @input="formatarDocumento" required)

        label Telefone *
        input(type="text" v-model="telefone" @input="formatarTelefone" required)

        label E-mail *
        input(type="email" v-model="email" required)

      fieldset
        legend Endereço

        label CEP *
        input(type="text" v-model="cep" maxlength="9" @blur="buscarEndereco" @input="formatarCep" required)

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
        button(type="submit") Salvar
        button(type="button" @click="$emit('fechar')") Cancelar

</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useToast } from 'vue-toastification'
import { buscarEnderecoPorCep } from '@/services/viacepService'
import { cadastrarCliente } from '@/services/clienteService'

const emit = defineEmits(['salvar', 'fechar'])
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
const mensagem = ref('')

function limparMensagem() {
  mensagem.value = ''
}

function formatarCep() {
  cep.value = cep.value
    .replace(/\D/g, '')
    .replace(/^(\d{5})(\d{0,3})/, '$1-$2')
    .substring(0, 9)
}

function formatarTelefone() {
  telefone.value = telefone.value.replace(/\D/g, '')
  if (telefone.value.length <= 10) {
    telefone.value = telefone.value.replace(/(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3')
  } else {
    telefone.value = telefone.value.replace(/(\d{2})(\d{5})(\d{0,4})/, '($1) $2-$3')
  }
}

function formatarDocumento() {
  const doc = cpfCnpj.value.replace(/\D/g, '')
  if (doc.length <= 11) {
    cpfCnpj.value = doc.replace(/(\d{3})(\d{3})(\d{3})(\d{0,2})/, '$1.$2.$3-$4')
  } else {
    cpfCnpj.value = doc.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{0,2})/, '$1.$2.$3/$4-$5')
  }
}

async function buscarEndereco() {
  const cepLimpo = cep.value.replace(/\D/g, '')
  if (cepLimpo.length !== 8) return

  try {
    const resposta = await buscarCep(cepLimpo)
    if (resposta.erro) throw new Error('CEP não encontrado.')

    rua.value = resposta.logradouro
    bairro.value = resposta.bairro
    cidade.value = resposta.localidade
    estado.value = resposta.uf
  } catch {
    toast.error('Erro ao buscar endereço.')
  }
}

async function cadastrar() {
  if (
    !nome.value ||
    !cpfCnpj.value ||
    !telefone.value ||
    !email.value ||
    !cep.value ||
    !rua.value ||
    !numero.value ||
    !bairro.value ||
    !cidade.value ||
    !estado.value
  ) {
    toast.error('Preencha todos os campos obrigatórios.')
    return
  }

  try {
    await cadastrarCliente({
      nome: nome.value,
      cpfCnpj: cpfCnpj.value.replace(/\D/g, ''),
      email: email.value,
      telefone: telefone.value.replace(/\D/g, ''),
      endereco: {
        cep: cep.value.replace(/\D/g, ''),
        rua: rua.value,
        numero: numero.value,
        bairro: bairro.value,
        cidade: cidade.value,
        estado: estado.value
      }
    })
    emit('salvar')
  } catch {
    toast.error('Erro ao cadastrar cliente.')
  }
}
</script>

<style scoped>
.modal-container {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 999;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5); /* fundo escuro */
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

/* Janela do modal */
.modal-content {
  position: relative; /* para posicionar o X */
  width: 100%;
  max-width: 600px;
  background-color: #fff;
  border-radius: 16px;
  padding: 2rem 2.5rem;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.25);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  max-height: 90vh;
  overflow-y: auto;
  animation: fadeInScale 0.3s ease-out;
}

@keyframes fadeInScale {
  0% {
    opacity: 0;
    transform: scale(0.95);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  font-size: 1.8rem;
  color: #2c3e50;
}

fieldset {
  border: 1px solid #42b983;
  border-radius: 10px;
  margin-bottom: 1.5rem;
  padding: 1.2rem 1.5rem;
  background-color: #f9fcfb;
}

legend {
  font-size: 1.1rem;
  font-weight: bold;
  padding: 0 0.5rem;
  color: #2c3e50;
}

label {
  font-weight: 600;
  margin-top: 1rem;
  display: block;
  color: #2c3e50;
  font-size: 0.95rem;
}

input,
select {
  width: 100%;
  padding: 0.55rem 0.75rem;
  margin-top: 0.25rem;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 1rem;
  transition:
    border-color 0.3s ease,
    box-shadow 0.3s ease;
  background-color: white;
}

input:focus,
select:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 6px #42b983a0;
}

/* Botões */
.btn-group {
  display: flex;
  gap: 1rem;
  margin-top: 1.8rem;
}

.btn-group button {
  flex: 1;
  padding: 0.9rem;
  font-weight: 700;
  font-size: 1.1rem;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  transition: background-color 0.25s ease;
}

.btn-group button[type='submit'] {
  background-color: #42b983;
  color: white;
}

.btn-group button[type='submit']:hover {
  background-color: #369b70;
}

.btn-group button[type='button'] {
  background-color: #e74c3c;
  color: white;
}

.btn-group button[type='button']:hover {
  background-color: #c0392b;
}

/* Botão fechar */
.btn-fechar {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: transparent;
  border: none;
  font-size: 1.8rem;
  font-weight: bold;
  color: #666;
  cursor: pointer;
  line-height: 1;
  padding: 0;
  transition: color 0.2s ease;
  user-select: none;
}

.btn-fechar:hover {
  color: #42b983;
}

/* Scroll suave dentro do modal */
.modal-content::-webkit-scrollbar {
  width: 6px;
}

.modal-content::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 10px;
}
</style>
