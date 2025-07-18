<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue' // ✅ Import necessário

import contaImg from '@/assets/estoque.png'
import img1 from '@/assets/slide1.webp'
import img2 from '@/assets/slide2.webp'
import img3 from '@/assets/slide3.avif'
import bgImage from '@/assets/mexendodopc.jpg'

const imagens = [img1, img2, img3]
const indexAtual = ref(0)
let intervalo: any = null

const irPara = (i: number) => {
  indexAtual.value = i
}

const proximo = () => {
  indexAtual.value = (indexAtual.value + 1) % imagens.length
}

onMounted(() => {
  intervalo = setInterval(proximo, 4000)
})

onUnmounted(() => {
  clearInterval(intervalo)
})
const cards = ref([
  'Controle de estoque em tempo real',
  'Cadastro completo de clientes e serviços prestados',
  'Emissão e acompanhamento de ordens de serviço',
  'Organização de tarefas e cronograma de produção',
  'Gestão de custos e controle financeiro da oficina',
  'Acompanhamento da produtividade da equipe',
  'Relatórios de desempenho e indicadores operacionais',
  'Redução de retrabalho com processos bem definidos'
])

const icones = ref([
  'https://img.icons8.com/ios-filled/50/DBDBDB/box.png',
  'https://img.icons8.com/ios-filled/50/DBDBDB/name.png',
  'https://img.icons8.com/ios-filled/50/DBDBDB/task.png',
  'https://img.icons8.com/ios-filled/50/DBDBDB/calendar.png',
  'https://img.icons8.com/ios-filled/50/DBDBDB/accounting.png',
  'https://img.icons8.com/ios-filled/50/DBDBDB/conference-call.png',
  'https://img.icons8.com/ios-filled/50/DBDBDB/combo-chart.png',
  'https://img.icons8.com/ios-filled/50/DBDBDB/settings.png'
])




const sliderRef = ref<HTMLElement | null>(null)
let interval: number

// Função de scroll automático
onMounted(() => {
  interval = setInterval(() => {
    const el = sliderRef.value
    if (!el) return
    const maxScroll = el.scrollWidth - el.clientWidth
    if (el.scrollLeft >= maxScroll) {
      el.scrollTo({ left: 0, behavior: 'smooth' })
    } else {
      el.scrollBy({ left: 300, behavior: 'smooth' })
    }
  }, 3000)
})

onUnmounted(() => {
  clearInterval(interval)
})
const estiloFundo = { backgroundImage: `url(${bgImage})` }

</script>


<template lang="pug">
section.banner-container
  img.banner-image(:src="contaImg" alt="Banner Conta")
  section.banner-with-text
    .shape-left
      h1.title
        | Seja Bem-vindo ao
        br
        | Sistema de Oficinas
      p.description
        | Nosso sistema de controle de estoque oferece uma solução prática e intuitiva para gerenciar produtos, categorias, fornecedores e movimentações de entrada e saída. Com interface simples e responsiva, permite que empresas acompanhem com precisão seu estoque, evitando perdas, faltas ou excessos.
section.slider-container
  .conteudo-lateral
    .slide-box
      img.slide-image(:src="imagens[indexAtual]" alt="Imagem do slide")

    .texto-box
      h2.titulo
        | Nosso software 
        br
        | atende aos mais altos padrões do mercado

      p.descricao
        | Trabalhe com um sistema desenhado em conformidade com a ISO 9001 e com todas as normas alinhadas a ela, para conquistar ou manter suas certificações.

      button.botao Conhecer software gratuitamente

  //- Agora as bolinhas ficam abaixo de tudo
  .paginadores
    span.ponto(
      v-for="(img, i) in imagens"
      :key="i"
      :class="{ ativo: i === indexAtual }"
      @click="irPara(i)"
    )

section.bg-slider-container(:style="estiloFundo")
  .bg-slider-overlay
    h1.bg-slider-title Quer elevar sua Gestão da Qualidade a um novo patamar?
    button.bg-slider-button Testar funcionalidades
    .bg-slider-track(ref="sliderRef")
      .bg-card(v-for="(item, index) in cards" :key="index")
        .bg-card-content
          .bg-card-inner
            img.bg-card-icon(:src="icones[index]" alt="Ícone")
          .bg-card-text {{ item }}

section.slider-container
  .conteudo-lateral
    .texto-box
      h2.titulo1 Nosso software 
        br
        | atende aos mais altos padrões do mercado

      p.descricao1 Trabalhe com um sistema desenhado em conformidade com a ISO 9001 e com todas as normas alinhadas a ela, para conquistar ou manter suas certificações.

      button.botao1 Conhecer software gratuitamente

    .slide-box
      img.slide-image(:src="imagens[indexAtual]" alt="Imagem do slide")



section.funcionalidades(id="funcionalidades")
  h2.func-title Funcionalidades do Sistema
  ul.func-list
    li
      span.check ✓
      |  Controle de estoque com movimentações
    li
      span.check ✓
      |  Cadastro de produtos e categorias
    li
      span.check ✓
      |  Histórico detalhado de entradas e saídas
    li
      span.check ✓
      |  Interface simples e responsiva
    button.func-btn Testar sistema



</template>

<style scoped>
.botao1{
  background-color: #dce1d5;
  border: none;
  padding: 0.8rem 1.6rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.3s ease;
  border-radius: 5px;
}

.botao:hover {
  background-color: #c8cebe;
}
.slider-container1{
  margin-top: 80px;
  margin-bottom: 80px;
}
.titulo1 {
  font-size: 2.5rem;
  font-weight: bold;
  color: #272727;
  margin-bottom: 1rem;
  line-height: 1.3;
}

.descricao1 {
  font-size: 1rem;
  color: #6b6b6b;
  margin-bottom: 2rem;
  max-width: 500px;
}

.bg-card-inner {
  background-color: rgba(219, 219, 219, 0.15); /* cinza translúcido */
  border-radius: 12px;
  padding: 6px 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: -30px;
  margin-bottom: 30px;
  width: 60px;
}

.bg-card-icon {
  width: 36px;
  height: 36px;
}

.bg-card-text {
  font-size: 14px;
  color: #fff; /* ou outro tom, dependendo do fundo */
  text-align: center;
}
html, body {
  overflow: hidden;
}

.bg-slider-container {
  width: 100vw;
  height: 100vh;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bg-slider-overlay {
  background-color: rgba(0, 0, 0, 0.6); /* fundo preto com 60% opacidade */
  padding: 50px 30px;
  border-radius: 0; /* removido para ocupar 100% */
  width: 100%;
  height: 100%;
  color: white;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.bg-slider-title {
  width: 40%;
  text-align: left;
  white-space: normal;
  margin: 0 0 20px 0;
  color: #ff9900;
  font-size: 3.1rem;
  font-weight: 700; /* Deixa a fonte bem grossa */
}

.bg-slider-button {
  width: 40%;           /* botão também alinhado na mesma largura */
  align-self: flex-start; /* alinha botão à esquerda se estiver usando flex */
}

.bg-slider-title,
.bg-slider-button {
  align-self: flex-start;  /* força alinhamento à esquerda */
}

.bg-slider-button {
  background-color: white;
  color: rgb(39, 65, 37);
  font-weight: bold;
  border: none;
  padding: 13px 25px;   /* aumenta o tamanho do botão */
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 40px;
  transition: 0.3s;
  width: auto;
  align-self: flex-start;
  font-size: 1rem;   /* aumenta o tamanho da fonte */
}

.bg-slider-button:hover {
  background-color: #f0f0f0;
}
.bg-slider-track {
  display: flex;
  overflow-x: auto;
  scroll-behavior: smooth;
  gap: 16px;
  box-sizing: border-box;
  width: 100%;
  margin-left: -20px;
  padding: 0px 0px;
}

.bg-slider-track::-webkit-scrollbar {
  display: none;
}

.bg-card {
  width: 250px;             /* define a largura diretamente */
  height: 240px;            /* define uma altura maior */
  background-color: rgb(39, 65, 37);
  color: white;
  padding: 20px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  box-sizing: border-box;   /* garante que o padding não expanda o card */
  overflow: hidden;         /* esconde conteúdo que ultrapasse */
}

.bg-card-icon {
  font-size: 24px;
  margin-bottom: 10px;
}

.bg-card-text {
  font-size: 1rem;
  font-weight: 500;
}


.unique-slider-container {
  background-size: cover;
  background-position: center;
  padding: 60px 20px;
  position: relative;
  color: #fff;
}

.unique-slider-overlay {
  background-color: rgba(0, 0, 0, 0.5);
  padding: 40px;
  border-radius: 12px;
  max-width: 1200px;
  margin: 0 auto;
}

.unique-slider-title {
  font-size: 2.2rem;
  font-weight: bold;
  margin-bottom: 20px;
  color: #d8ff3a;
}

.unique-slider-button {
  background-color: white;
  color: black;
  font-weight: bold;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 30px;
}

.unique-slider-track {
  display: flex;
  overflow-x: auto;
  scroll-behavior: smooth;
  gap: 16px;
  padding-bottom: 10px;
}

.unique-slider-track::-webkit-scrollbar {
  display: none;
}

.unique-card {
  min-width: 280px;
  background-color: #222d14;
  color: white;
  padding: 20px;
  border-radius: 10px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.unique-card-icon {
  font-size: 24px;
  margin-bottom: 10px;
}

.unique-card-text {
  font-size: 1rem;
  font-weight: 500;
}
.slider-container {
  width: 100%;
  background-color: #fff; /* fundo branco */
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 3rem 2rem;
  box-sizing: border-box;
}

.conteudo-lateral {
  display: flex;
  justify-content: space-between;
  width: 100%;
  max-width: 1200px;
  flex-wrap: wrap;
  box-sizing: border-box;
}

.slide-box {
  flex: 1 1 50%;
  max-width: 50%;
  padding: 1rem;
  display: flex;
  justify-content: center;
  align-items: center;
}

.slide-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.4);
}

.texto-box {
  flex: 1 1 50%;
  max-width: 50%;
  padding: 1rem;
}

.paginadores {
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 2rem;
}

.ponto {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #ccc;
  cursor: pointer;
  transition: background 0.3s ease;
}

.ponto.ativo {
  background: #2d2e22;
}

.titulo {
  font-size: 2.5rem;
  font-weight: bold;
  color: #2d2e22;
  margin-bottom: 1rem;
  line-height: 1.3;
}

.descricao {
  font-size: 1rem;
  color: #6b6b6b;
  margin-bottom: 2rem;
  max-width: 500px;
}

.botao {
  background-color: #dce1d5;
  border: none;
  padding: 0.8rem 1.6rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.3s ease;
  border-radius: 5px;
}

.botao:hover {
  background-color: #c8cebe;
}

/* ✅ Responsivo */
@media (max-width: 900px) {
  .conteudo-lateral {
    flex-direction: column;
    text-align: center;
  }

  .slide-box,
  .texto-box {
    max-width: 100%;
    flex: 1 1 100%;
  }

  .titulo {
    font-size: 1.8rem;
  }

  .descricao {
    font-size: 0.95rem;
  }

  .paginadores {
    margin-top: 1.5rem;
  }
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: Arial, Helvetica, sans-serif;
}

html, body {
  width: 100vw;
  height: 100vh;
  background-color: #ffffff;
  overflow: hidden; /* remove rolagem vertical e horizontal */
  font-family: Arial, sans-serif;
}
.banner-image {
  width: 100vw;
  height: 60vh; /* antes era 50vh */
  object-fit: cover;
  display: block;
  margin: 0;
  margin-bottom: -70px; /* aumente proporcionalmente se quiser manter a sobreposição */
}


.banner-container {
  position: relative;
  width: 100vw;
  margin: 0;
  padding: 0;
  overflow: hidden;
  margin-top: -90px;
}

.banner-with-text {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  color: white;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 20px;
  box-sizing: border-box;
}

.shape-left {
  background-color: #a76400;
  border-top-right-radius: 60% 50%;
  border-bottom-right-radius: 60% 50%;
  padding: 10rem 4rem 3rem 3rem;
  width: 50%;
  height: 70vh;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  gap: 1.5rem;
  pointer-events: auto;
  margin-left: -50px;
  margin-top: -20px;
  max-height: 550px;
}

.title {
  font-size: 2.5rem;
  line-height: 1.2;
  margin-bottom: -10px;
  margin-left: 40px;
  color: #ffffff;
  font-family: inherit;
}

.description {
  font-size: 1rem;
  color: #c5c5c5;
  line-height: 1.5;
  max-width: 400px;
  margin-bottom: 3rem;
  text-align: left;
  margin-left: 40px;
  font-family: inherit;
}

.arrow {
  position: relative;
  top: -50px;
  font-size: 1rem;
  color: rgba(255, 255, 255, 1);
  cursor: pointer;
  margin-left: 40px;
  background-color: transparent;
  border: 1px solid rgba(214, 214, 214, 1);
  border-radius: 50%;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  user-select: none;
  padding: 0;
  box-sizing: border-box;
}

.arrow:hover {
  color: #000000;
  transform: translateY(5px);
  background-color: aliceblue;
}

/* NOVA SEÇÃO DE FUNCIONALIDADES */
.funcionalidades {
  background-color: #ffffff;
  padding: 1rem 2rem; /* Ajustado para menos espaço vertical */
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  /* Removi margin-bottom -50px para evitar scroll */
  margin-bottom: 0;
  overflow: hidden; /* Para evitar scroll interno */
}
.func-wrapper {
  display: flex;
  flex-direction: column;  /* empilha verticalmente */
  align-items: flex-start; /* alinha à esquerda */
  gap: 1.5rem; /* espaço entre lista e botão */
}
.func-title {
  font-size: 30px;
  margin-bottom: 1.5rem;
  color: #333333;
  font-weight: bold;
  margin-top: 40px; /* Remove margin negativa */
}
.func-list {
  list-style: none;
  padding: 0;
  margin: 0 auto 2rem; /* centraliza e dá espaçamento abaixo */
  display: flex;
  flex-direction: column;
  gap: 1rem;
  font-size: 1.05rem;
  color: #444;
  width: 100%;
  max-width: 400px; /* limita largura para centralizar melhor */
  align-items: center; /* centraliza os itens */
}
.func-list li {
  background-color: #fff;
  padding: 0.8rem 1.2rem;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  width: 100%;
  max-width: 400px;
  display: flex;
  align-items: center;
  position: relative;
  font-weight: 400;
  font-size: 15px;
  justify-content: center; /* centraliza o conteúdo */
}

/* Estilo do ícone */
.func-list li .check {
  position: absolute;
  left: 1rem; /* fixa o ícone na esquerda do card */
}
.check {
  color: #2e7d32; /* Verde discreto */
  font-size: 1rem;
  font-weight: normal;
}
.func-btn {
  padding: 0.8rem 3rem;
  background-color: #16bd00ff;
  border: none;
  border-radius: 30px;
  color: white;
  font-weight: bold;
  cursor: pointer;
  font-size: 1.1rem;
  height: 47px;
  transition: background-color 0.3s ease;
  align-self: center;
}

.func-btn:hover {
  background-color: #47ff0fff;
}
</style>