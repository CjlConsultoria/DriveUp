<template lang="pug">
section.veiculo-list-container
  .header
    h2 Lista de Veículos
    button.btn-add(@click="showModal = true") + Adicionar Veículo

  // Filtros
  .filtros
    input(type="text" placeholder="Filtrar por modelo" v-model="filtroModelo")
    input(type="text" placeholder="Filtrar por placa" v-model="filtroPlaca")
    input(type="text" placeholder="Filtrar por CPF/CNPJ do dono" v-model="filtroCpfCnpj")

  table.veiculo-table
    thead
      tr
        th ID
        th Modelo
        th Placa
        th Dono
        th Ações
    tbody
      tr(
        v-for="veiculo in veiculos" 
        :key="veiculo.id"
        @click="abrirDetalhes(veiculo)"
        style="cursor: pointer;"
      )
        td {{ veiculo.id }}
        td {{ veiculo.modelo }}
        td {{ veiculo.placa }}
        td {{ veiculo.cliente.nome }}
        td
          button.btn-editar(@click.stop="excluirVeiculo(veiculo.id)") Editar
          button.btn-delete(@click.stop="excluirVeiculo(veiculo.id)") Excluir

  .pagination
    button(@click="paginaAnterior" :disabled="currentPage === 1") ◀
    span Página {{ currentPage }} de {{ totalPages }}
    button(@click="proximaPagina" :disabled="currentPage === totalPages") ▶

  VeiculoModal(
  v-if="showModal"
  @fechar="showModal = false"
  @salvar="aoSalvarVeiculo"
  )


  // Modal detalhes veículo e dono
  div.modal-detalhes(v-if="veiculoSelecionado")
    .modal-content
      button.btn-fechar(@click="fecharDetalhes") ×
      h3 Detalhes do Veículo

      section.dados-veiculo
        h4 Dados do Veículo
        p
          strong Modelo:
          |  {{ veiculoSelecionado.modelo }}
        p
          strong Placa:
          |  {{ veiculoSelecionado.placa }}
        p
          strong Marca:
          |  {{ veiculoSelecionado.marca }}
        p
          strong Ano:
          |  {{ veiculoSelecionado.ano }}
        p
          strong Cor:
          |  {{ veiculoSelecionado.cor }}
        p
          strong Quilometragem:
          |  {{ veiculoSelecionado.quilometragem }}      
      section.dados-dono(style="margin-top: 1rem;")
        h4 Dados do Dono
        p
          strong Nome:
          |  {{ veiculoSelecionado.cliente.nome }}
        p
          strong CPF/CNPJ:
          |  {{ veiculoSelecionado.cliente.cpfCnpj }}
        p
          strong E-mail:
          |  {{ veiculoSelecionado.cliente.email }}
        p
          strong Telefone:
          |  {{ veiculoSelecionado.cliente.telefone }}
</template>
<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import VeiculoModal from '@/views/Cadastro/VeiculoCadastroModal.vue'
import { listarVeiculos } from '@/services/veiculoService'

type Cliente = {
  id: number
  nome: string
  cpfCnpj: string
  email: string
  telefone: string
}

type Veiculo = {
  id: number
  modelo: string
  placa: string
  cliente: Cliente
}

const veiculos = ref<Veiculo[]>([])
const totalPages = ref(1)
const totalElements = ref(0)

const currentPage = ref(1)
const itemsPerPage = 5

// Filtros
const filtroModelo = ref('')
const filtroPlaca = ref('')
const filtroCpfCnpj = ref('')

const showModal = ref(false)
const veiculoSelecionado = ref<Veiculo | null>(null)

async function carregarVeiculos() {
  try {
    const response = await listarVeiculos({
      modelo: filtroModelo.value || undefined,
      placa: filtroPlaca.value || undefined,
      cpfCnpj: filtroCpfCnpj.value || undefined,
      page: currentPage.value - 1,
      size: itemsPerPage,
      sort: 'id,desc'
    })

    veiculos.value = response.content.map((v: any) => ({
      id: v.id,
      modelo: v.modelo,
      placa: v.placa,
      marca: v.marca,
      ano: v.ano,
      cor: v.cor,
      quilometragem: v.quilometragem,
      cliente: {
        id: v.clienteId,
        nome: v.clienteNome,
        cpfCnpj: v.clienteCpfCnpj,
        email: v.clienteEmail, // deixar vazio ou buscar depois
        telefone: v.clienteTelefone
      }
    }))

    totalPages.value = response.totalPages
    totalElements.value = response.totalElements
  } catch (error) {
    console.error('Erro ao carregar veículos:', error)
  }
}

// Recarregar ao mudar página ou filtros
watch([currentPage, filtroModelo, filtroPlaca, filtroCpfCnpj], () => {
  carregarVeiculos()
})

onMounted(() => {
  carregarVeiculos()
})
function aoSalvarVeiculo() {
  showModal.value = false
  carregarVeiculos()
}

function proximaPagina() {
  if (currentPage.value < totalPages.value) currentPage.value++
}
function paginaAnterior() {
  if (currentPage.value > 1) currentPage.value--
}

function excluirVeiculo(id: number) {
  veiculos.value = veiculos.value.filter((v) => v.id !== id)
  if (currentPage.value > totalPages.value) {
    currentPage.value = totalPages.value || 1
  }
}

function abrirDetalhes(veiculo: Veiculo) {
  veiculoSelecionado.value = veiculo
}
function fecharDetalhes() {
  veiculoSelecionado.value = null
}
</script>

<style scoped>
.veiculo-list-container {
  padding: 2rem;
  max-width: 100%;
  margin: auto;
  font-family: 'Segoe UI', sans-serif;
  color: #1a1a1a;
  background-color: #fff;
}

/* Header */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

h2 {
  color: #1a1a1a;
  font-size: 1.6rem;
}

/* Filtros */
.filtros {
  margin-bottom: 1rem;
  display: flex;
  gap: 1rem;
}

.filtros input {
  flex: 1;
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.filtros input:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 6px #42b983a0;
}

/* Botões */
button {
  transition:
    background-color 0.3s ease,
    transform 0.2s ease;
}

.btn-add {
  background-color: #2c7be5;
  color: #fff;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 8px;
  font-weight: 600;
  font-size: 0.95rem;
  cursor: pointer;
}

.btn-add:hover {
  background-color: #1a5fd0;
  transform: translateY(-1px);
}

.btn-delete {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 0.4rem 0.9rem;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
}

.btn-delete:hover {
  background-color: #c0392b;
  transform: translateY(-1px);
}

/* Tabela */
table.veiculo-table {
  width: 100%;
  border-collapse: collapse;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  background-color: #fff;
}

.veiculo-table th,
.veiculo-table td {
  padding: 0.9rem 1.2rem;
  text-align: left;
  border-bottom: 1px solid #eaeaea;
  color: #1a1a1a;
  font-size: 0.95rem;
}

.veiculo-table th {
  background-color: #f9fafc;
  font-weight: 600;
  color: #333;
}

.veiculo-table tr:hover {
  background-color: #f0f9f4;
}

/* Paginação */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 1.5rem;
  gap: 1rem;
}

.pagination button {
  background-color: #2c7be5;
  color: #fff;
  border: none;
  padding: 0.5rem 0.9rem;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  font-size: 0.9rem;
}

.pagination button:hover:not(:disabled) {
  background-color: #1a5fd0;
  transform: translateY(-1px);
}

.pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
  color: #666;
}

/* Modal detalhes */
.modal-detalhes {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 2rem 2.5rem;
  border-radius: 12px;
  max-width: 400px;
  width: 90%;
  position: relative;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
}

.modal-content section {
  margin-top: 1rem;
}

.modal-content h4 {
  border-bottom: 1px solid #ddd;
  padding-bottom: 0.4rem;
  margin-bottom: 0.7rem;
  color: #2c3e50;
  font-weight: 700;
}

.btn-fechar {
  position: absolute;
  top: 1rem;
  right: 1rem;
  font-size: 1.8rem;
  background: transparent;
  border: none;
  cursor: pointer;
  color: #666;
  user-select: none;
  transition: color 0.2s ease;
}

.btn-fechar:hover {
  color: #2c7be5;
}

.btn-editar {
  background-color: #4caf50; /* verde */
  color: white;
  border: none;
  padding: 0.4rem 0.9rem;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  margin-right: 0.5rem; /* espaço entre editar e excluir */
  transition:
    background-color 0.3s ease,
    transform 0.2s ease;
}

.btn-editar:hover {
  background-color: #3a9d40;
  transform: translateY(-1px);
}
</style>
