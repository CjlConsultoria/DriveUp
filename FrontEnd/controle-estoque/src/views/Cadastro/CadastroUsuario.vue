<template lang="pug">
section.tela-inteira
  .cadastro-wrapper
    h2 Cadastro de Usuário

    form(@submit.prevent="cadastrar" @input="limparMensagem")
      fieldset
        legend Dados Pessoais
        label(for="nome") Nome completo *
        input#nome(type="text" v-model="nome" placeholder="Nome completo" required)

        label(for="email") Email *
        input#email(type="email" v-model="email" placeholder="email@exemplo.com" required)

        label(for="telefone") Telefone *
        input#telefone(type="tel" v-model="telefone" placeholder="(99) 99999-9999" required)

        label(for="cpf") CPF *
        input#cpf(type="text" v-model="cpfFormatado" maxlength="14" placeholder="000.000.000-00" required)

      fieldset
        legend Endereço
        label(for="cep") CEP
        input#cep(type="text" v-model="cep" placeholder="00000-000" maxlength="8")

        label(for="rua") Rua
        input#rua(type="text" v-model="rua" placeholder="Rua" readonly)

        label(for="numero") Número
        input#numero(type="text" v-model="numero" placeholder="Número")

        label(for="complemento") Complemento
        input#complemento(type="text" v-model="complemento" placeholder="Complemento")

        label(for="bairro") Bairro
        input#bairro(type="text" v-model="bairro" placeholder="Bairro" readonly)

        label(for="cidade") Cidade
        input#cidade(type="text" v-model="cidade" placeholder="Cidade" readonly)

        label(for="estado") Estado
        input#estado(type="text" v-model="estado" placeholder="Estado" readonly)

      fieldset
        legend Acesso
        label(for="senha") Senha *
        input#senha(type="password" v-model="senha" placeholder="Senha" required)

        label(for="role") Permissão / Cargo
        select#role(v-model="role" required)
          option(disabled value="") Selecione a permissão
          option(v-for="p in Object.values(Permissao)" :key="p" :value="p") {{ p }}

      button(type="submit") Cadastrar
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useToast } from 'vue-toastification'
import { Permissao } from '@/enums/Permissao'
import { buscarEnderecoPorCep } from '@/services/viacepService'
import { cadastrarUsuario } from '@/services/authService'

const toast = useToast()

// Dados do formulário
const nome = ref('')
const email = ref('')
const senha = ref('')
const telefone = ref('')
const cpf = ref('')
const cep = ref('')
const rua = ref('')
const numero = ref('')
const complemento = ref('')
const bairro = ref('')
const cidade = ref('')
const estado = ref('')
const role = ref(Permissao.MECANICO)

const mensagem = ref('')

// Computed para exibir CPF formatado
function formatarCPF(valor: string) {
  const cpfNumeros = valor.replace(/\D/g, '')
  let formatado = cpfNumeros

  if (cpfNumeros.length > 3) {
    formatado = cpfNumeros.replace(/(\d{3})(\d)/, '$1.$2')
  }
  if (cpfNumeros.length > 6) {
    formatado = formatado.replace(/(\d{3})(\d)/, '$1.$2')
  }
  if (cpfNumeros.length > 9) {
    formatado = formatado.replace(/(\d{3})(\d{1,2})$/, '$1-$2')
  }

  return formatado
}

const cpfFormatado = computed({
  get() {
    return formatarCPF(cpf.value)
  },
  set(valor: string) {
    cpf.value = valor.replace(/\D/g, '')
  }
})

// CEP → preenche endereço automaticamente
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
        mensagem.value = 'CEP não encontrado.'
        toast.error('CEP não encontrado.')
      }
    } catch {
      mensagem.value = 'Erro ao buscar o CEP.'
      toast.error('Erro ao buscar o CEP.')
    }
  }
})

// Limpa a mensagem de erro/sucesso quando o formulário for modificado
function limparMensagem() {
  mensagem.value = ''
}

async function cadastrar() {
  if (!nome.value || !email.value || !senha.value || !cpf.value || !telefone.value) {
    toast.error('Preencha todos os campos obrigatórios.')
    return
  }

  try {
    await cadastrarUsuario({
      nome: nome.value,
      email: email.value,
      senha: senha.value,
      telefone: telefone.value,
      cpf: cpf.value,
      endereco: {
        cep: cep.value,
        rua: rua.value,
        numero: numero.value,
        complemento: complemento.value,
        bairro: bairro.value,
        cidade: cidade.value,
        estado: estado.value
      },
      role: role.value
    })

    toast.success('Usuário cadastrado com sucesso!')

    // Limpa o formulário
    nome.value = ''
    email.value = ''
    senha.value = ''
    telefone.value = ''
    cpf.value = ''
    cep.value = ''
    rua.value = ''
    numero.value = ''
    complemento.value = ''
    bairro.value = ''
    cidade.value = ''
    estado.value = ''
    role.value = Permissao.MECANICO
    mensagem.value = ''
  } catch (error) {
    toast.error('Erro ao cadastrar usuário.')
  }
}
</script>

<style scoped>
.tela-inteira {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f2f4f8;
  padding: 2rem;
}

.cadastro-wrapper {
  max-width: 900px;
  width: 100%;
  background-color: white;
  padding: 2.5rem 3rem;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  overflow-y: auto;
}

h2 {
  text-align: center;
  margin-bottom: 2rem;
  font-size: 2rem;
  color: #2c3e50;
}

fieldset {
  border: 1px solid #42b983;
  border-radius: 10px;
  margin-bottom: 2rem;
  padding: 1.5rem 1.8rem;
  background-color: #f9fcfb;
}

legend {
  font-size: 1.2rem;
  font-weight: bold;
  padding: 0 0.5rem;
  color: #2c3e50;
}

label {
  font-weight: 600;
  margin-top: 1.2rem;
  display: block;
  color: #2c3e50;
  font-size: 0.95rem;
}

input,
select {
  width: 100%;
  padding: 0.6rem 0.75rem;
  margin-top: 0.3rem;
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

button {
  margin-top: 2rem;
  padding: 0.9rem;
  width: 100%;
  border: none;
  border-radius: 10px;
  background-color: #42b983;
  color: white;
  font-weight: 700;
  font-size: 1.2rem;
  cursor: pointer;
  transition: background-color 0.25s ease;
}

button:hover {
  background-color: #369b70;
}

.mensagem {
  margin-top: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  text-align: center;
}
</style>
