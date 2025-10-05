# Gerenciador Financeiro com IA (Full-Stack)

![Java](https://img.shields.io/badge/Java-21-blue.svg) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg) ![React](https://img.shields.io/badge/React-18-blue.svg) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg) ![Docker](https://img.shields.io/badge/Docker-blue.svg)

API RESTful e aplicação Front-end (SPA) desenvolvidas em Java com Spring Boot e React para um gerenciador financeiro pessoal. O sistema permite o controle de transações, automação de lançamentos recorrentes e fornece insights financeiros gerados por Inteligência Artificial através da API do Google Gemini.

Este projeto foi construído como uma peça de portfólio para demonstrar habilidades em desenvolvimento full-stack, arquitetura de software, segurança de APIs e integração com serviços externos, visando uma oportunidade de estágio na área de desenvolvimento de software.

---

## ✨ Features

### Backend (API RESTful)
- ✅ **Autenticação e Autorização:** Sistema seguro com Spring Security e JWT para registro e login de múltiplos usuários.
- ✅ **Dados por Usuário:** Cada usuário tem acesso apenas às suas próprias transações e configurações.
- ✅ **CRUD de Transações:** Endpoints para criar, ler, atualizar e deletar transações (entradas e saídas).
- ✅ **CRUD de Itens Recorrentes:** Endpoints para gerenciar "modelos" de transações recorrentes (ex: Salário, Aluguel).
- ✅ **Geração Automática de Transações:** Lógica de serviço que, ao visualizar um novo mês, cria automaticamente as transações recorrentes que ainda não foram lançadas.
- ✅ **Exclusão Inteligente:** Lógica para permitir a exclusão de apenas uma instância de transação recorrente ou o cancelamento da recorrência para sempre.
- ✅ **Integração com IA:** Endpoint que envia o histórico de transações para a API do Google Gemini e retorna sugestões para melhoria dos gastos.
- ✅ **Persistência de Sugestões:** As sugestões da IA são salvas no banco de dados, atreladas ao usuário.
- ✅ **Documentação de API:** Geração automática de documentação interativa com **Swagger (SpringDoc)**.

### Frontend (React SPA)
- ✅ **Interface Reativa:** Frontend construído com React e Vite.
- ✅ **Roteamento:** Navegação entre páginas (Login, Registro, Dashboard) sem recarregar, usando **React Router**.
- ✅ **Rotas Protegidas:** O usuário só consegue acessar o dashboard principal se estiver logado.
- ✅ **Gerenciamento de Estado:** Lógica de estado centralizada no componente `App` com "State Lifting" e `Context` para uma UI consistente.
- ✅ **Dashboard Financeiro:** Exibição do balanço (entradas, saídas, saldo) e um gráfico de rosca (`Chart.js`) que se atualizam em tempo real.
- ✅ **Navegação de Histórico:** Botões para navegar entre os meses e visualizar o histórico de transações.
- ✅ **Gerenciamento de Recorrentes:** Modal para o usuário cadastrar e deletar seus modelos de transações recorrentes.
- ✅ **Consumo de IA:** Botão para solicitar e exibir as sugestões geradas pela IA.

---

## 🚀 Tecnologias Utilizadas

### **Backend**
- Java 21
- Spring Boot 3.5.6
- Spring Security (Autenticação com JWT)
- Spring Data JPA
- PostgreSQL 15
- Docker & Docker Compose
- Maven
- JJWT (Java JWT)
- SpringDoc OpenAPI (Swagger)

### **Frontend**
- React 18
- Vite
- React Router DOM
- Chart.js
- React Icons

### **Serviços Externos**
- Google Gemini API

---

## 🏁 Como Rodar o Projeto

**Pré-requisitos:** JDK 21+, Docker e Node.js 18+.

### **1. Backend**
```bash
# Clone este repositório
$git clone https://[URL-DO-SEU-REPOSITORIO-NO-GITHUB].git$ cd nome-do-projeto

# Crie e configure sua chave da API do Gemini como uma variável de ambiente na sua IDE:
# GEMINI_API_KEY=SUA_CHAVE_AQUI

# Suba o container do PostgreSQL com Docker
$ docker-compose up -d

# Rode a aplicação Spring Boot
$ ./mvnw spring-boot:run
O backend estará rodando em http://localhost:8080.
```

2. Frontend

```
# Navegue até a pasta do frontend
$ cd frontend

# Na raiz da pasta 'frontend', crie um arquivo .env e adicione a URL da API:
# VITE_API_BASE_URL=http://localhost:8080

# Instale as dependências
$ npm install

# Rode o servidor de desenvolvimento
$ npm run dev
O frontend estará disponível em http://localhost:5173.
```


📚 Documentação da API
A documentação completa e interativa dos endpoints do backend, gerada pelo Swagger, está disponível em:

➡️ http://localhost:8080/swagger-ui.html


LinkedIn: https://www.linkedin.com/in/seu-usuario/

GitHub: https://github.com/NoClick11

