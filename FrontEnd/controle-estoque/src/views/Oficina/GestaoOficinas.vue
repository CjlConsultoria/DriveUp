<template lang="pug">
div.container-geral
  div.container.mx-auto.p-6
    h1.text-2xl.font-bold.mb-4.text-center Gestão de Oficinas


    button.btn-primary.mb-4(@click="abrirModalOficina()") Nova Oficina

    input(
      type="text"
      v-model="filtro"
      placeholder="Pesquisar oficina..."
      class="border rounded px-3 py-2 mb-4 w-full max-w-md"
      @input="buscarOficinas"
    )

    table.min-w-full.border.border-gray-200.rounded
      thead.bg-gray-100
        tr
          th.px-4.py-2 Nome
          th.px-4.py-2 CNPJ
          th.px-4.py-2 Telefone
          th.px-4.py-2 Licença
          th.px-4.py-2 Usuários
          th.px-4.py-2 Data Inicio / Fim
          th.px-4.py-2 Ações
      tbody
        tr(v-for="oficina in oficinas" :key="oficina.id" class="border-b")
          td.px-4.py-2 {{ oficina.nome }}
          td.px-4.py-2 {{ oficina.cnpj }}
          td.px-4.py-2 {{ oficina.telefone }}
          td.px-4.py-2
            | {{ oficina.licenca.tipo }} - {{ oficina.licenca.maxUsuarios }} usuários
          td.px-4.py-2 {{ oficina.usuarios.length }}
          td.px-4.py-2 {{ oficina.dataInicio }} até {{ oficina.dataFim }}
          td.px-4.py-2
            button.btn-secondary.mr-2(@click="abrirModalOficina(oficina)") Editar
            button.btn-secondary(@click="abrirModalUsuario(oficina)") Usuários

    OficinaModal(:visible="modalOficina" :oficina="oficinaSelecionada" @fechar="fecharModalOficina" @salvar="salvarOficina")
    UsuarioModal(:visible="modalUsuario" :oficina="oficinaSelecionada" @fechar="fecharModalUsuario" @salvar="salvarUsuarios")
</template>


<script setup lang="ts">
import { ref, onMounted } from 'vue'
import OficinaModal from '@/views/Oficina/components/OficinaModal.vue'
import UsuarioModal from '@/views/Oficina/components/UsuarioModal.vue'
import { listarLicencas, salvarLicenca } from '@/services/licencaService'
import type { LicencaDetalhadaDTO, LicencaDTO } from '@/services/licencaService'

interface Usuario {
  id: string
  nome: string
  email: string
  tipo: 'Admin' | 'Usuário'
}

interface Licenca {
  id?: number
  tipo: string
  maxUsuarios: number
  dataExpiracao: string
}

interface Oficina {
  id: string
  nome: string
  cnpj: string
  telefone: string
  licenca: Licenca
  usuarios: Usuario[]
}

const oficinas = ref<Oficina[]>([])
const filtro = ref('')
const oficinaSelecionada = ref<Oficina | null>(null)
const modalOficina = ref(false)
const modalUsuario = ref(false)
function formatarDataParaDDMMYYYY(data?: string): string {
  if (!data) return '-'
  const d = new Date(data)
  const dia = String(d.getDate()).padStart(2, '0')
  const mes = String(d.getMonth() + 1).padStart(2, '0') // meses começam do 0
  const ano = d.getFullYear()
  return `${dia}/${mes}/${ano}`
}

async function buscarOficinas() {
  try {
    const response = await listarLicencas({ empresaNome: filtro.value })
    oficinas.value = response.content.map((lic: any) => ({
      id: lic.empresaId.toString(),
      nome: lic.empresaNome,
      cnpj: lic.empresaCnpj,
      telefone: lic.empresaTelefone || '-',
      dataInicio: formatarDataParaDDMMYYYY(lic.dataInicio),
      dataFim: formatarDataParaDDMMYYYY(lic.dataFim),
      licenca: {
        id: lic.id,
        tipo: lic.tipoLicencaNome,
        maxUsuarios: lic.limiteUsuarios,
        dataExpiracao: lic.dataFim
      },
      usuarios: Array.from({ length: lic.qtdUsuarios || 0 }, (_, i) => ({
        id: `user-${i}`,
        nome: '-',
        email: '-',
        tipo: 'Usuário' as const
      }))
    }))
  } catch (err) {
    console.error('Erro ao buscar oficinas', err)
    oficinas.value = []
  }
}

function fecharModalOficina() {
  modalOficina.value = false
  atualizarOficinas()
}

function fecharModalUsuario() {
  modalUsuario.value = false
  atualizarOficinas()
}

async function atualizarOficinas() {
  await buscarOficinas()
}

onMounted(() => {
  buscarOficinas()
})

function abrirModalOficina(oficina?: Oficina) {
  oficinaSelecionada.value = oficina ? { ...oficina, licenca: { ...oficina.licenca } } : null
  modalOficina.value = true
}

function abrirModalUsuario(oficina: Oficina) {
  oficinaSelecionada.value = { ...oficina }
  modalUsuario.value = true
}

async function salvarOficina(payload: LicencaDTO) {
  try {
    // Salva a licença usando o payload recebido do modal
    const licencaSalva = await salvarLicenca(payload)

    // Atualiza a oficina na lista
    const index = oficinas.value.findIndex((o) => o.id === payload.empresaId?.toString())

    const oficinaAtualizada = {
      id: payload.empresaId?.toString() || Date.now().toString(),
      nome: payload.empresaNome,
      cnpj: payload.empresaCnpj,
      telefone: payload.telefone || '-',
      licenca: {
        id: licencaSalva.id,
        tipo: licencaSalva.tipo,
        maxUsuarios: licencaSalva.limiteUsuarios,
        dataExpiracao: licencaSalva.dataFim
      },
      usuarios: index !== -1 ? oficinas.value[index].usuarios : [] // mantém usuários existentes
    }

    if (index !== -1) {
      oficinas.value[index] = oficinaAtualizada
    } else {
      oficinas.value.push(oficinaAtualizada)
    }

    modalOficina.value = false
  } catch (err) {
    console.error('Erro ao salvar oficina', err)
  }
}

function salvarUsuarios(usuarios: Usuario[]) {
  if (oficinaSelecionada.value) {
    const index = oficinas.value.findIndex((o) => o.id === oficinaSelecionada.value!.id)
    if (index !== -1) oficinas.value[index].usuarios = usuarios
  }
  modalUsuario.value = false
}
</script>

<style scoped>
h1.text-2xl {
  text-align: center;
}

/* Container geral do site */
.container-geral {
  min-height: 100vh; /* ocupa a tela toda */
  background-color: #f1f1f1; /* mesma cor ou outra */
  padding: 20px;
}

.container {
  max-width: 1100px;
  margin: 15px auto 0 auto;
  padding: 1.5rem;
  background-color: #ffffff; 
  color: #000000;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  border-radius: 20px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.10);
  min-height: 47rem; /* altura mínima do container */
}



h1 {
  font-size: 2rem;
  margin-bottom: 1rem;
}

input[type='text'] {
  width: 100%;
  max-width: 300px;
  padding: 0.5rem;
  margin-bottom: 1rem;
  border-radius: 6px;
  border: 1px solid #333;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1rem;
}

th,
td {
  border-bottom: 1px solid #ddd;
  border-right: 1px solid #ddd;
  padding: 0.4rem 0.6rem; /* reduz altura */
  text-align: left !important;
  vertical-align: middle;
  white-space: nowrap; /* evita quebra de linha */
  font-size: 0.85rem;  /* diminui tamanho do texto */
}

thead th {
  background-color: #222222;
  color: #fff;
  user-select: none;
  padding: 0.4rem 0.4rem; /* reduz altura do cabeçalho */
  font-size: 0.9rem;      /* diminui texto do cabeçalho */
}


button {
  padding: 0.3rem 0.3rem;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  margin-right: 0.5rem;
}

.btn-primary {
  background-color:#aec437;
  color: #000000;
}

.btn-primary:hover {
  background-color: #d0d888;
}

.btn-secondary {
  background-color: #61b961;
  color: #000;
}

.btn-secondary:hover {
  background-color: #91d691;
}

.modal-content {
  background-color: #fff;
  color: #000;
  padding: 1.5rem;
  border-radius: 12px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
}

.modal-header {
  font-size: 1.25rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  margin-top: 1rem;
}

.flex {
  display: flex;
}

.gap-2 {
  gap: 0.5rem;
}

.mb-2 {
  margin-bottom: 0.5rem;
}
</style>
