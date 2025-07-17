<template lang="pug">
  .home-container
    section.banner
      h1 Seja Bem-vindo ao Sistema de Estoque
      p Gerencie seus produtos, categorias e movimentações com facilidade e eficiência.

    section.features
      h2 Funcionalidades
      ul.features-list
        li Controle de estoque com movimentações
        li Cadastro de produtos e categorias
        li Histórico detalhado de entradas e saídas
        li Interface simples e responsiva
</template>

<script setup lang="ts">
// Sem lógica específica por enquanto
</script>

<style scoped>
.home-container {
  max-width: 900px;
  margin: 3rem auto;
  padding: 0 1rem;
  text-align: center;
  color: #333;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.banner h1 {
  font-size: 2.8rem;
  margin-bottom: 1rem;
  font-weight: 700;
}

.banner p {
  font-size: 1.25rem;
  margin-bottom: 3rem;
  color: #555;
}

.features h2 {
  font-size: 1.8rem;
  margin-bottom: 1.25rem;
  color: #222;
  border-bottom: 2px solid #4caf50;
  display: inline-block;
  padding-bottom: 0.3rem;
}

.features-list {
  list-style: none;
  padding: 0;
  margin: 0 auto;
  max-width: 600px;
  text-align: left;
}

.features-list li {
  font-size: 1.1rem;
  margin-bottom: 0.8rem;
  position: relative;
  padding-left: 1.6rem;
}

.features-list li::before {
  content: '✓';
  color: #4caf50;
  position: absolute;
  left: 0;
  top: 0;
  font-weight: bold;
}
</style>
