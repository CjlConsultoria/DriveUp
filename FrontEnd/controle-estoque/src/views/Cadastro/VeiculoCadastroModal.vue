<template lang="pug">
section.tela-inteira
  .cadastro-wrapper
    h2 Cadastro de Veículo
    button.btn-fechar(@click="$emit('fechar')") ×
    form(@submit.prevent="cadastrar")
      fieldset
        legend Dados do Veículo

        label Cliente *
        input(
          type="text",
          v-model="filtroCliente",
          placeholder="Digite para buscar cliente...",
          @focus="mostrarDropdown = true",
          @blur="handleBlur",
          autocomplete="off"
        )
        ul.dropdown(v-if="mostrarDropdown && clientesFiltrados.length")
          li(
            v-for="cliente in clientesFiltrados",
            :key="cliente.id",
            @mousedown.prevent="selecionarCliente(cliente)"
          ) {{ cliente.nome }}

        div.info-cliente(v-if="clienteSelecionado")
          p CPF: {{ clienteSelecionado.cpfCnpj }}
          p E-mail: {{ clienteSelecionado.email }}
          p Telefone: {{ clienteSelecionado.telefone }}

        label(for="placa") Placa *
        input#placa(type="text" v-model="placa" placeholder="ABC-1234" maxlength="8" required)

        label(for="marca") Marca *
        input#marca(type="text" v-model="marca" placeholder="Marca" required)

        label(for="modelo") Modelo *
        input#modelo(type="text" v-model="modelo" placeholder="Modelo" required)

        label(for="ano") Ano *
        input#ano(type="number" v-model.number="ano" placeholder="Ano" min="1900" max="2100" required)

        label(for="cor") Cor *
        input#cor(type="text" v-model="cor" placeholder="Cor" required)

        label(for="quilometragem") Quilometragem *
        input#quilometragem(type="number" v-model.number="quilometragem" placeholder="Quilometragem" min="0" required)

      button(type="submit") Cadastrar
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import { listarClientes } from '@/services/clienteService'
import { cadastrarVeiculo } from '@/services/veiculoService'
const emit = defineEmits(['salvar', 'fechar'])
import type { Cliente } from '@/services/clienteService'

const clienteSelecionado = ref<Cliente | null>(null)

const toast = useToast()

const clientes = ref<Array<any>>([])
const filtroCliente = ref('')
const mostrarDropdown = ref(false)

const placa = ref('')
const marca = ref('')
const modelo = ref('')
const ano = ref<number | null>(null)
const cor = ref('')
const quilometragem = ref<number | null>(null)

onMounted(async () => {
  try {
    const response = await listarClientes({}) // ⚡ necessário passar um objeto
    // Mapeia os clientes para garantir que o id seja string e traga os dados necessários
    clientes.value = response.content.map((c: any) => ({
      id: c.id.toString(), // se precisar converter para string
      nome: c.nome,
      cpfCnpj: c.cpfCnpj,
      email: c.email
    }))
  } catch (error) {
    console.error('Erro ao carregar lista de clientes.')
  }
})

const clientesFiltrados = computed(() => {
  if (!filtroCliente.value) return clientes.value
  return clientes.value.filter((c) =>
    c.nome.toLowerCase().includes(filtroCliente.value.toLowerCase())
  )
})

function selecionarCliente(cliente: any) {
  clienteSelecionado.value = cliente
  filtroCliente.value = cliente.nome
  mostrarDropdown.value = false
}

function handleBlur() {
  // Delay para dar tempo do clique registrar no dropdown
  setTimeout(() => {
    mostrarDropdown.value = false
    if (clienteSelecionado.value?.nome !== filtroCliente.value) {
      clienteSelecionado.value = null
    }
  }, 200)
}

async function cadastrar() {
  if (
    !clienteSelecionado.value ||
    !placa.value ||
    !marca.value ||
    !modelo.value ||
    !ano.value ||
    !cor.value ||
    quilometragem.value === null
  ) {
    toast.error('Preencha todos os campos obrigatórios.')
    return
  }
  try {
    await cadastrarVeiculo({
      clienteId: clienteSelecionado.value.id.toString(), // ✅ converte number para string
      placa: placa.value,
      marca: marca.value,
      modelo: modelo.value,
      ano: ano.value,
      cor: cor.value,
      quilometragem: quilometragem.value
    })
    toast.success('Veículo cadastrado com sucesso!')

    emit('salvar')
  } catch {
    toast.error('Erro ao cadastrar veículo.')
  }
}
</script>

<style scoped>
.tela-inteira {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 999;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.cadastro-wrapper {
  position: relative;
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

input {
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

input:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 6px #42b983a0;
}

.dropdown {
  border: 1px solid #ccc;
  max-height: 150px;
  overflow-y: auto;
  margin: 0;
  padding: 0;
  list-style: none;
  background: white;
  position: absolute;
  width: 100%;
  z-index: 10;
  border-radius: 4px;
}

.dropdown li {
  padding: 8px 12px;
  cursor: pointer;
}

.dropdown li:hover {
  background-color: #42b983;
  color: white;
}

.info-cliente {
  margin-top: 0.5rem;
  padding: 0.75rem 1rem;
  background-color: #f0f9f4;
  border: 1px solid #42b983;
  border-radius: 8px;
  font-size: 0.95rem;
  color: #2c3e50;
  user-select: none;
}

button[type='submit'] {
  margin-top: 1.8rem;
  padding: 0.9rem;
  width: 100%;
  border: none;
  border-radius: 10px;
  background-color: #42b983;
  color: white;
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
  transition: background-color 0.25s ease;
}

button[type='submit']:hover {
  background-color: #369b70;
}

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
</style>
