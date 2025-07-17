<template lang="pug">
  .modal-backdrop(@click.self="$emit('close')")
    .modal-content
      header
        h3 Extrato do Produto - {{ extrato.descricao }}
        button.btn-fechar(@click="$emit('close')") Fechar

      section.extrato-info
        h4 Informações Gerais
        p
          strong Nome:
          |  {{ extrato.descricao }}
        p
          strong Total Entrada:
          |  {{ extrato.quantidadeTotalEntrada }}
        p
          strong Total Saída:
          |  {{ extrato.quantidadeTotalSaida }}
        p
          strong Saldo Atual:
          |  {{ extrato.saldoAtual }}
        p
          strong Lucro Total:
          |  R$ {{ extrato.lucroTotal.toFixed(2) }}

      section.extrato-movimentos
        h4 Movimentações
        table
          thead
            tr
              th Data
              th Tipo
              th Quantidade
              th Valor Venda
              th Lucro
          tbody
            tr(v-for="mov in extrato.movimentos" :key="mov.dataVenda + mov.tipo")
              td {{ new Date(mov.dataVenda).toLocaleDateString('pt-BR') }}
              td(:class="mov.tipo.toLowerCase() === 'entrada' ? 'tipo-entrada' : mov.tipo.toLowerCase() === 'saida' ? 'tipo-saida' : ''") {{ mov.tipo }}
              td {{ mov.quantidade }}
              td {{ mov.valorVenda !== null ? 'R$ ' + mov.valorVenda.toFixed(2) : '-' }}
              td {{ 'R$ ' + mov.lucro.toFixed(2) }}
  </template>

<script setup lang="ts">
defineProps<{ extrato: any }>()
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.modal-content {
  background: #fff;
  padding: 2rem 3rem;
  width: 720px; /* aumentado */
  max-width: 95vw;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  max-height: 85vh;
  overflow-y: auto;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #2c3e50;
  border: 1px solid #e2e8f0;
  font-size: 1rem;
  user-select: text;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #cbd5e1; /* cinza claro */
  padding-bottom: 0.75rem;
  margin-bottom: 1.5rem;
}

header h3 {
  font-weight: 700;
  font-size: 1.4rem;
  color: #1e293b; /* azul escuro suave */
  letter-spacing: 0.04em;
}

.btn-fechar {
  background: transparent;
  border: 1.8px solid #94a3b8; /* azul cinza suave */
  color: #64748b;
  font-weight: 600;
  font-size: 1rem;
  padding: 0.35rem 0.85rem;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.25s ease;
  user-select: none;
}
.btn-fechar:hover {
  background: #61d36b;
  border-color: #4faf4f;
  color: #fff;
}

.extrato-info h4,
.extrato-movimentos h4 {
  font-weight: 600;
  font-size: 1.1rem;
  color: #334155;
  margin-bottom: 1rem;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 0.4rem;
}

.extrato-info p {
  margin: 0.35rem 0;
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #f1f5f9;
  padding-bottom: 0.4rem;
  color: #476959;
  font-weight: 600;
}

.extrato-info strong {
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-weight: 700;
  flex-basis: 45%;
  color: #334155;
}

.extrato-info p > span {
  flex-basis: 55%;
  text-align: right;
  font-weight: 600;
  color: #1e3b28;
}

.extrato-movimentos table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 8px;
  font-size: 0.95rem;
  color: #486947;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: inset 0 0 5px #f0f4f8;
}

.extrato-movimentos thead {
  background-color: #f8fafc;
  color: #335535;
  user-select: none;
}

.extrato-movimentos thead th {
  padding: 0.7rem 1rem;
  text-align: left;
  letter-spacing: 0.06em;
  font-weight: 700;
  border-bottom: 1.5px solid #cbd5e1;
}

.extrato-movimentos tbody tr:hover {
  background-color: #e0f2fe;
  cursor: default;
  color: #03a12b;
  font-weight: 700;
  transition: background-color 0.3s ease;
}

.extrato-movimentos tbody tr {
  background: #f9fafb;
  transition: background-color 0.25s ease;
  border-radius: 8px;
}

.extrato-movimentos tbody td {
  padding: 0.7rem 1rem;
  vertical-align: middle;
  font-weight: 500;
  color: #335544;
  border-radius: 6px;
  transition: color 0.25s ease;
}

/* Alinhamento e formatação numérica */
.extrato-movimentos td:nth-child(3),
.extrato-movimentos td:nth-child(4),
.extrato-movimentos td:nth-child(5) {
  text-align: right;
  font-variant-numeric: tabular-nums;
}

/* Tipo */
.extrato-movimentos td:nth-child(2) {
  font-weight: 700;
  text-transform: uppercase;
}

/* Cores suaves por tipo */
.extrato-movimentos td.tipo-entrada {
  color: #16a34a; /* verde médio */
}

.extrato-movimentos td.tipo-saida {
  color: #dc2626; /* vermelho médio */
}

/* Responsive */
@media (max-width: 760px) {
  .modal-content {
    width: 95vw;
    padding: 1.5rem 2rem;
  }
  .extrato-info p {
    flex-direction: column;
    align-items: flex-start;
  }
  .extrato-info strong,
  .extrato-info p > span {
    flex-basis: auto;
    text-align: left;
  }
  .extrato-movimentos th,
  .extrato-movimentos td {
    font-size: 0.85rem;
    padding: 0.5rem 0.7rem;
  }
}
</style>
