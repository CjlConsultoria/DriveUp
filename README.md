# 📦 Controle de Estoque

Sistema web para **gestão de estoque**, com backend em **Spring Boot** e frontend em **Vue 3**. Permite o cadastro de produtos, movimentações (entradas e saídas), consulta de extrato por produto, e muito mais.

---

## ⚙️ Tecnologias Utilizadas

### Backend
- Java 17+
- Spring Boot
- Spring Security (autenticação básica)
- Spring Data JPA
- Banco de Dados H2 (em memória)
- Lombok

### Frontend
- Vue 3
- TypeScript
- Pug (template engine)
- Pinia (store)
- Fetch API

---

## 🔐 Segurança

- Todas as rotas da API estão protegidas com autenticação HTTP Basic.
- Usuário e senha padrão:
  - **Usuário**: `admin`
  - **Senha**: `admin`
- Necessário enviar `Authorization: Basic <base64(admin:admin)>` nos headers para consumir as APIs.
- Não há interface de login — a autenticação é feita via headers no frontend.

---

## 🧩 Funcionalidades

### Produtos
- Cadastro de produtos com tipo e valor
- Edição e exclusão
- Listagem com filtros
- Ações rápidas via dropdown
- Visualização de detalhes

### Movimentações de Estoque
- Registro de entradas e saídas
- Filtro por tipo e período
- Paginação dos resultados
- Visualização de movimentações por produto

### Extrato por Produto
- Modal com extrato completo do produto
- Totais de entrada e saída
- Lista detalhada de movimentações

---

## 🌐 Frontend

### Funcionalidades da Interface
- Interface limpa e responsiva
- Listagem de produtos com filtros
- Ações via modais:
  - Editar produto
  - Confirmar exclusão
  - Visualizar extrato
- Paginação integrada
- Destaques visuais para campos obrigatórios como e-mail e endereço


---

## 🔧 Configurações da Aplicação

### Banco de Dados (H2)
- URL: `jdbc:h2:mem:estoque-db`
- Usuário: `sa` | Senha: *(vazio)*
- Console: `http://localhost:8080/h2-console`

### Servidor
- Porta: `8080`

### JPA
- `ddl-auto=update` (cria/atualiza as tabelas automaticamente)
- SQL visível no console para debug

---

## ▶️ Como Executar

### Backend
```bash
./mvnw spring-boot:run
```
Acesse: `http://localhost:8080`

### Frontend
```bash
cd frontend/
npm install
npm run dev
```
Acesse: `http://localhost:5173`

---

## 📌 Requisitos

- Java 17+
- Node.js 18+
- Maven

---

## 📫 Contato

**Cleyton Jesus**  
📧 [ticleyton@gmail.com]  

---

> Projeto simples e funcional, ideal para controle interno ou demonstração de estrutura com Spring + Vue.