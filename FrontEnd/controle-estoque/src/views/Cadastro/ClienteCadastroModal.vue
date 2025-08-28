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
        input(
          type="text"
          v-model="cpfCnpjFormatted"
          :readonly="isAtualizacao"
          maxlength="18"
          placeholder="000.000.000-00 ou 00.000.000/0000-00"
          @input="atualizarCpfCnpj"
        )


        label Telefone *
        input(type="text" v-model="telefone" @input="formatarTelefone" required)

        label E-mail *
        input(type="email" v-model="email" required)

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
        button.btn-primary(type="submit") Salvar
        button.btn-secondary(type="button" @click="$emit('fechar')") Cancelar
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useToast } from 'vue-toastification'
import { buscarEnderecoPorCep } from '@/services/viacepService'
import { cadastrarCliente } from '@/services/clienteService'

const emit = defineEmits(['salvar', 'fechar'])
const toast = useToast()

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

const cpfCnpjFormatted = ref('') // valor formatado

// Função de atualização do CPF/CNPJ
function atualizarCpfCnpj(e: Event) {
  const input = e.target as HTMLInputElement
  let valor = input.value.replace(/\D/g, '') // só números
  cpfCnpjRaw.value = valor
  cpfCnpj.value = valor // <-- isso garante que o v-model do cpfCnpj fique correto

  // formatação para exibição
  if (valor.length <= 11) {
    valor = valor
      .replace(/^(\d{3})(\d)/, '$1.$2')
      .replace(/^(\d{3}\.\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3}\.\d{3}\.\d{3})(\d{1,2})$/, '$1-$2')
  } else {
    valor = valor
      .replace(/^(\d{2})(\d)/, '$1.$2')
      .replace(/^(\d{2}\.\d{3})(\d)/, '$1.$2')
      .replace(/^(\d{2}\.\d{3}\.\d{3})(\d)/, '$1/$2')
      .replace(/(\d{2}\.\d{3}\.\d{3}\/\d{4})(\d{1,2})$/, '$1-$2')
  }

  cpfCnpjFormatted.value = valor
}

const nome = ref('')
const cpfCnpj = ref('') // valor principal do CPF/CNPJ
const telefone = ref('')
const email = ref('')
const cep = ref('')
const rua = ref('')
const numero = ref('')
const bairro = ref('')
const cidade = ref('')
const estado = ref('')
const mensagem = ref('')
const isAtualizacao = ref(false) // define se é edição

const cpfCnpjRaw = ref('') // valor só números

// Watch para atualizar valor numérico sempre que o formatado mudar
watch(cpfCnpj, (novo) => {
  cpfCnpjRaw.value = novo.replace(/\D/g, '')
})

// Função de formatação dinâmica
function formatarDocumento() {
  let valor = cpfCnpjRaw.value // sempre começar com números puros

  if (valor.length <= 11) {
    // CPF
    valor = valor
      .replace(/^(\d{3})(\d)/, '$1.$2')
      .replace(/^(\d{3}\.\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3}\.\d{3}\.\d{3})(\d{1,2})$/, '$1-$2')
  } else {
    // CNPJ
    valor = valor
      .replace(/^(\d{2})(\d)/, '$1.$2')
      .replace(/^(\d{2}\.\d{3})(\d)/, '$1.$2')
      .replace(/^(\d{2}\.\d{3}\.\d{3})(\d)/, '$1/$2')
      .replace(/(\d{2}\.\d{3}\.\d{3}\/\d{4})(\d{1,2})$/, '$1-$2')
  }

  cpfCnpj.value = valor
}

async function buscarEndereco() {
  const cepLimpo = cep.value.replace(/\D/g, '')
  if (cepLimpo.length !== 8) return

  try {
    const resposta = await buscarEnderecoPorCep(cepLimpo)
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
  border-bottom: 1px solid #f2d16b;
  padding-bottom: 0.5rem;
}

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

fieldset {
  border: 1px solid #f2d16b;
  border-radius: 8px;
  padding: 0.9rem 1.1rem;
  margin-bottom: 1rem;
  background-color: #fff;
}
legend {
  font-weight: bold;
  padding: 0 0.5rem;
  color: #7a5e00;
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
  border-color: #f2d16b;
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
  background-color: #f2d16b;
  color: #5c4a00;
  font-weight: bold;
}
.btn-primary:hover {
  background-color: #e8c44c;
  transform: translateY(-1px);
}
.btn-secondary {
  background-color: #f8f1d2;
  color: #5c4a00;
  font-weight: bold;
}
.btn-secondary:hover {
  background-color: #f2e5a7;
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
