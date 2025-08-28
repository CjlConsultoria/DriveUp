<template lang="pug">
section.cliente-list-container
  .header
    h2 Lista de Clientes
    button.btn-add(@click="showModal = true") + Adicionar Cliente

  // Filtros simples
  .filtros
    input(type="text" v-model="filtroNome" placeholder="Filtrar por nome")
    input(type="text" v-model="filtroCpfCnpj" placeholder="Filtrar por CPF/CNPJ")
    input(type="text" v-model="filtroEmail" placeholder="Filtrar por email")
    
  table.cliente-table
    thead
      tr
        th ID
        th Nome
        th CPF/CNPJ
        th Email
        th Telefone
        th Ações
    tbody
      tr(v-for="cliente in clientes" :key="cliente.id" @click="abrirModal(cliente)" class="clickable-row")
        td {{ cliente.id }}
        td {{ cliente.nome }}
        td {{ cliente.cpfCnpj }}
        td {{ cliente.email }}
        td {{ cliente.telefone || '-' }}
        td
          button.btn-editar(@click.stop="removerCliente(cliente.id)") Editar
          button.btn-delete(@click.stop="removerCliente(cliente.id)") Excluir

  .pagination
    button(@click="paginaAnterior" :disabled="currentPage === 0") ◀
    span Página {{ currentPage + 1 }} de {{ totalPages }}
    button(@click="proximaPagina" :disabled="currentPage + 1 === totalPages") ▶

  // Modal de consulta (sem edição)
  div.modal-mask(v-if="modalVisible")
    div.modal-wrapper
      div.modal-container
        header.modal-header
          h3 Dados do Cliente
          button.btn-close(@click="fecharModal") ✖
        
        section.modal-section
          h4 Dados Pessoais
          p
            b Nome: 
            | {{ clienteSelecionado.nome }}
          p
            b Email: 
            | {{ clienteSelecionado.email || '-' }}
          p
            b CPF/CNPJ: 
            | {{ clienteSelecionado.cpfCnpj || '-' }}
          p
            b Telefone: 
            | {{ clienteSelecionado.telefone || '-' }}
        
        section.modal-section
          h4 Endereço
          p(v-if="clienteSelecionado.endereco")
            b CEP: 
            | {{ clienteSelecionado.endereco.cep || '-' }}
          p(v-if="clienteSelecionado.endereco")
            b Rua: 
            | {{ clienteSelecionado.endereco.logradouro || clienteSelecionado.endereco.rua || '-' }}
          p(v-if="clienteSelecionado.endereco")
            b Número: 
            | {{ clienteSelecionado.endereco.numero || '-' }}
          p(v-if="clienteSelecionado.endereco")
            b Complemento: 
            | {{ clienteSelecionado.endereco.complemento || '-' }}
          p(v-if="clienteSelecionado.endereco")
            b Bairro: 
            | {{ clienteSelecionado.endereco.bairro || '-' }}
          p(v-if="clienteSelecionado.endereco")
            b Cidade: 
            | {{ clienteSelecionado.endereco.cidade || '-' }}
          p(v-if="clienteSelecionado.endereco")
            b Estado: 
            | {{ clienteSelecionado.endereco.estado || '-' }}

        footer.modal-footer
          button.btn-close(@click="fecharModal") Fechar


  ClienteModal(v-if="showModal" @salvar="onClienteSalvo" @fechar="showModal = false")

</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import ClienteModal from '@/views/Cadastro/ClienteCadastroModal.vue'
import { listarClientes, excluirCliente } from '@/services/clienteService'
import { useToast } from 'vue-toastification'

const toast = useToast()
type Cliente = {
  id: number
  nome: string
  email: string
  telefone?: string
  cpfCnpj?: string
  endereco?: {
    cep?: string
    logradouro?: string
    rua?: string
    numero?: string
    complemento?: string
    bairro?: string
    cidade?: string
    estado?: string
  }
}

const clientes = ref<Cliente[]>([])
const showModal = ref(false)

const currentPage = ref(0)
const itemsPerPage = 5
const totalPages = ref(1)
const totalElements = ref(0)

const filtroNome = ref('')
const filtroEmail = ref('')
const filtroCpfCnpj = ref('')
const sort = ref('nome,asc')

// Modal de consulta
const modalVisible = ref(false)
const clienteSelecionado = ref<Cliente | null>(null)

async function buscarClientes() {
  try {
    const data = await listarClientes({
      nome: filtroNome.value || undefined,
      email: filtroEmail.value || undefined,
      cpfCnpj: filtroCpfCnpj.value || undefined
    })

    // Normaliza campos opcionais para não quebrar tipagem
    clientes.value = data.content.map((c) => ({
      ...c,
      email: c.email || '',
      telefone: c.telefone || '',
      cpfCnpj: c.cpfCnpj || ''
    }))

    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
  } catch (error) {
    console.error('Erro ao buscar clientes:', error)
  }
}

watch([currentPage, filtroNome, filtroEmail, filtroCpfCnpj], () => {
  buscarClientes()
})

onMounted(() => {
  buscarClientes()
})

function proximaPagina() {
  if (currentPage.value + 1 < totalPages.value) currentPage.value++
}

function paginaAnterior() {
  if (currentPage.value > 0) currentPage.value--
}

function limparFiltros() {
  filtroNome.value = ''
  filtroEmail.value = ''
  filtroCpfCnpj.value = ''
  currentPage.value = 0
}

async function onClienteSalvo() {
  showModal.value = false // fecha modal
  limparFiltros() // opcional, limpa filtros
  await buscarClientes() // busca clientes sem filtro
}

async function removerCliente(id: number) {
  try {
    // converter para string, já que a service espera string
    await excluirCliente(String(id))
    await buscarClientes()
    toast.success('Cliente excluído com sucesso.')
  } catch (error) {
    console.error('Erro ao excluir cliente:', error)
    toast.error('Erro ao excluir cliente.')
  }
}

// Abrir modal de detalhes
function abrirModal(cliente: Cliente) {
  clienteSelecionado.value = cliente
  modalVisible.value = true
}

function fecharModal() {
  modalVisible.value = false
  clienteSelecionado.value = null
}
</script>

<style scoped>
.cliente-list-container {
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
table.cliente-table {
  width: 100%;
  border-collapse: collapse;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  background-color: #fff;
}

.cliente-table th,
.cliente-table td {
  padding: 0.9rem 1.2rem;
  text-align: left;
  border-bottom: 1px solid #eaeaea;
  color: #1a1a1a;
  font-size: 0.95rem;
}

.cliente-table th {
  background-color: #f9fafc;
  font-weight: 600;
  color: #333;
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

.clickable-row {
  cursor: pointer;
}

.clickable-row:hover {
  background-color: #f0f8ff;
}

/* Modal */

.modal-mask {
  position: fixed;
  z-index: 9999;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-wrapper {
  max-width: 600px;
  width: 90%;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  padding: 1.5rem 2rem;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.5rem;
}

.btn-close {
  background: transparent;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0;
  color: #666;
}

.modal-section {
  margin-bottom: 1.5rem;
}

.modal-section h4 {
  margin-bottom: 0.5rem;
  border-bottom: 1px solid #ddd;
  padding-bottom: 0.3rem;
  font-weight: 600;
  color: #333;
}

.modal-section p {
  margin: 0.25rem 0;
  font-size: 1rem;
  color: #444;
}

.modal-footer {
  text-align: right;
}

.modal-footer .btn-close {
  background-color: #2c7be5;
  color: white;
  border-radius: 6px;
  padding: 0.5rem 1rem;
  font-weight: 600;
}

.modal-footer .btn-close:hover {
  background-color: #1a5fd0;
}

.filtros {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.filtros input[type='text'] {
  flex: 1 1 200px; /* permite crescer, mas não menor que 200px */
  padding: 0.5rem 0.75rem;
  border: 1.5px solid #ccc;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
  outline-offset: 2px;
  outline-color: transparent;
}

.filtros input[type='text']:focus {
  border-color: #2c7be5;
  outline-color: #2c7be5;
  box-shadow: 0 0 6px rgba(44, 123, 229, 0.5);
}

/* Ajuste do botão adicionar para ficar alinhado com filtros */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.btn-add {
  background-color: #2c7be5;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 8px;
  font-weight: 600;
  font-size: 0.95rem;
  cursor: pointer;
  transition:
    background-color 0.3s ease,
    transform 0.2s ease;
}

.btn-add:hover {
  background-color: #1a5fd0;
  transform: translateY(-1px);
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
