<template lang="pug">
div(v-if="visible" class="modal-overlay")
  div.modal-content
    h2.text-xl.font-bold.mb-4
      span(:v-text="localOficina.id ? 'Editar Oficina' : 'Nova Oficina'")
    
    form(@submit.prevent="salvarOficina")
      label.block.mb-2 Nome
      input(type="text" v-model="localOficina.nome" required)

      label.block.mb-2 CNPJ
      input(
        type="text"
        v-model="cnpjFormatado"
        @input="onCNPJInput"
        required
      )

      label.block.mb-2 Telefone 
      input(type="text" v-model="localOficina.telefone" required)

      label.block.mb-2 Licença
      select(v-model.number="selectedLicencaId" @change="onChangeLicenca")
        option(value="" disabled) Selecione uma licença
        option(v-for="lic in tiposLicenca" :key="lic.id" :value="lic.id") {{ lic.nome }}

      // Campos de data
      label.block.mb-2.mt-3 Data de Início
      input(type="date" v-model="localOficina.dataInicio" required)

      label.block.mb-2 Data de Fim
      input(type="date" v-model="localOficina.dataFim" required)

      div(v-if="localOficina.licenca && localOficina.licenca.id" class="licenca-detalhes mt-2 p-3 border border-gray-300 rounded bg-gray-50")
        p.mb-1
          strong Limite de usuários: 
          span {{ localOficina.licenca.maxUsuarios }}
        p.mb-1
          strong Código: 
          span {{ localOficina.licenca.codigo }}
        p.mb-0
          strong Descrição: 
          span {{ localOficina.licenca.descricao }}

      // Botões separados
      div.flex.justify-between.items-center.mt-4.w-full
        button.btn-secondary(type="button" @click="$emit('fechar')") Cancelar
        button.btn-primary(type="submit") Salvar


</template>
<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { listarTiposLicenca } from '@/services/licencaService'
import type { TipoLicenca, LicencaDTO } from '@/services/licencaService'

interface Licenca {
  id?: number
  tipo: string
  maxUsuarios?: number
  codigo?: string
  descricao?: string
  dataExpiracao?: string
}

interface Oficina {
  id?: string
  nome: string
  cnpj: string
  telefone: string
  dataInicio?: string
  dataFim?: string
  licenca?: Licenca
  usuarios?: any[]
}

const props = defineProps<{ visible: boolean; oficina: Oficina | null }>()
const emit = defineEmits(['fechar', 'salvar'])

const tiposLicenca = ref<TipoLicenca[]>([])
const selectedLicencaId = ref<number | null>(null)

const localOficina = ref<Oficina>({
  nome: '',
  cnpj: '',
  telefone: '',
  dataInicio: '',
  dataFim: '',
  licenca: { tipo: '' }
})

const cnpjFormatado = ref('') // exibição formatada

function formatarCNPJ(cnpj: string): string {
  if (!cnpj) return ''
  const apenasNumeros = cnpj.replace(/\D/g, '')
  return apenasNumeros.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5')
}

function onCNPJInput(e: Event) {
  const target = e.target as HTMLInputElement
  const numeros = target.value.replace(/\D/g, '')
  localOficina.value.cnpj = numeros // valor puro
  cnpjFormatado.value = formatarCNPJ(numeros) // valor formatado
}

function formatarDataInput(data?: string): string {
  if (!data) return ''
  return data.split('T')[0] // mantém só a parte da data
}
watch(
  () => props.oficina,
  (novaOficina) => {
    if (novaOficina) {
      localOficina.value = {
        ...novaOficina,
        dataInicio: formatarDataInput(novaOficina.dataInicio),
        dataFim: formatarDataInput(novaOficina.dataFim),
        licenca: novaOficina.licenca || { tipo: '' }
      }
      cnpjFormatado.value = formatarCNPJ(localOficina.value.cnpj)

      if (novaOficina.licenca?.id) {
        const licencaSelecionada = tiposLicenca.value.find((l) => l.id === novaOficina.licenca?.id)
        if (licencaSelecionada) {
          selectedLicencaId.value = licencaSelecionada.id
          localOficina.value.licenca = {
            id: licencaSelecionada.id,
            tipo: licencaSelecionada.nome,
            maxUsuarios: licencaSelecionada.limiteUsuarios,
            codigo: licencaSelecionada.codigo,
            descricao: licencaSelecionada.descricao
          }
        }
      }
    } else {
      localOficina.value = {
        nome: '',
        cnpj: '',
        telefone: '',
        dataInicio: '',
        dataFim: '',
        licenca: { tipo: '' }
      }
      cnpjFormatado.value = ''
      selectedLicencaId.value = null
    }
  },
  { immediate: true }
)

onMounted(async () => {
  try {
    tiposLicenca.value = await listarTiposLicenca()
  } catch (err) {
    console.error('Erro ao buscar tipos de licença', err)
  }
})

function onChangeLicenca() {
  const licencaSelecionada = tiposLicenca.value.find((l) => l.id === selectedLicencaId.value)
  if (licencaSelecionada) {
    localOficina.value.licenca = {
      id: licencaSelecionada.id,
      tipo: licencaSelecionada.nome,
      maxUsuarios: licencaSelecionada.limiteUsuarios,
      codigo: licencaSelecionada.codigo,
      descricao: licencaSelecionada.descricao
    }
  } else {
    localOficina.value.licenca = { tipo: '' }
  }
}

function salvarOficina() {
  const payload: LicencaDTO = {
    id: localOficina.value.licenca?.id,
    empresaId: localOficina.value.id ? Number(localOficina.value.id) : undefined,
    empresaNome: localOficina.value.nome || '',
    empresaCnpj: localOficina.value.cnpj, // valor puro
    telefone: localOficina.value.telefone || '',
    dataInicio: localOficina.value.dataInicio || '',
    dataFim: localOficina.value.dataFim || '',
    tipo: localOficina.value.licenca?.tipo || '',
    limiteUsuarios: localOficina.value.licenca?.maxUsuarios || 0,
    ativa: true
  }

  emit('salvar', payload)
}
</script>

<style scoped>
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
  background-color: #ffffff;
  color: #000000;
  padding: 1.5rem;
  border-radius: 12px;
  width: 100%;
  max-width: 550px;
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

input[type='text'],
select {
  width: 100%;
  padding: 0.5rem;
  margin-bottom: 1rem;
  border-radius: 6px;
  border: 1px solid #333;
  color: #000;
  background-color: #fff;
  font-size: 0.95rem;
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

.licenca-detalhes p {
  margin: 0.2rem 0;
}

.licenca-detalhes strong {
  font-weight: 600;
}

.licenca-detalhes {
  background-color: #f9fafb;
  border-left: 4px solid #f2d16b;
}

button {
  padding: 0.5rem 1rem;
  border-radius: 6px;
  border: none;
  cursor: pointer;
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

input[type='text'],
input[type='date'],
select {
  width: 100%;
  padding: 0.5rem;
  margin-bottom: 1rem;
  border-radius: 6px;
  border: 1px solid #333;
  color: #000;
  background-color: #fff;
  font-size: 0.95rem;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

input[type='text']:focus,
input[type='date']:focus,
select:focus {
  outline: none;
  border-color: #f2d16b;
  box-shadow: 0 0 4px rgba(242, 209, 107, 0.6);
}
/* Força o ícone do calendário a ficar visível */
input[type='date']::-webkit-calendar-picker-indicator {
  cursor: pointer;
  filter: invert(0.5);
}
</style>
