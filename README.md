# Gerenciador Financeiro Pessoal com IA (Full-Stack)

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/technologies/downloads/#java21)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-blue.svg)](https://react.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![AWS](https://img.shields.io/badge/AWS-EC2%20%26%20RDS-orange.svg)](https://aws.amazon.com/)
[![Netlify](https://img.shields.io/badge/Netlify-Deploy-cyan.svg)](https://www.netlify.com/)

[![Java CI with Maven](https://github.com/NoClick11/Gerenciador-Financeiro/actions/workflows/ci.yml/badge.svg)](https://github.com/NoClick11/Gerenciador-Financeiro/actions/workflows/ci.yml)

---

**🚀 Acesse a Aplicação Online:** **[Live Demo](https://gerenciadorfinanceiro34123.netlify.app/)**
*(Sinta-se à vontade para criar uma conta para testar!)*

---

## 📖 Descrição

API RESTful e aplicação Front-end (SPA) desenvolvidas em Java com Spring Boot e React para um gerenciador financeiro pessoal. O sistema permite o controle de transações, automação de lançamentos recorrentes e fornece insights financeiros gerados por Inteligência Artificial através da API do Google Gemini.

O projeto foi totalmente implantado na nuvem, com o backend rodando na **AWS (EC2 + RDS)** protegido por **HTTPS (via Nginx + Let's Encrypt)** e o frontend hospedado na **Netlify** para entrega rápida via CDN global. O processo de build e teste do backend é automatizado com **GitHub Actions**.

Este projeto foi construído como uma peça de portfólio para demonstrar habilidades em desenvolvimento full-stack, arquitetura de software, segurança de APIs, integração com serviços externos e deploy em nuvem, visando uma oportunidade de estágio na área de desenvolvimento de software.

---

## ✨ Features

### Backend (API RESTful)
- ✅ **Autenticação e Autorização:** Sistema seguro com Spring Security e JWT (`jjwt` v0.13.0) para registro e login.
- ✅ **Dados por Usuário:** Acesso segregado aos dados financeiros por usuário.
- ✅ **CRUD de Transações:** Endpoints para gerenciar transações (entradas e saídas).
- ✅ **CRUD de Itens Recorrentes:** Gerenciamento de modelos de transações recorrentes.
- ✅ **Geração Automática de Transações:** Lógica para criar lançamentos recorrentes ao visualizar um novo mês.
- ✅ **Exclusão Inteligente:** Opção para excluir apenas uma instância recorrente ou cancelar a recorrência.
- ✅ **Integração com IA:** Endpoint para obter sugestões financeiras da API do Google Gemini.
- ✅ **Persistência de Sugestões:** Sugestões da IA salvas no banco de dados.
- ✅ **Documentação de API:** Documentação interativa com Swagger (SpringDoc).
- ✅ **Segurança:** Configuração de HTTPS com Nginx e Let's Encrypt.
- ✅ **Testes:** Cobertura de testes unitários (JUnit/Mockito) e de integração (Spring Test/MockMvc).
- ✅ **CI/CD:** Integração Contínua com GitHub Actions para build e teste automatizados.

### Frontend (React SPA)
- ✅ **Interface Reativa:** Construído com React 19 e Vite.
- ✅ **Roteamento:** Navegação entre páginas com React Router v7.
- ✅ **Rotas Protegidas:** Acesso ao dashboard restrito a usuários logados.
- ✅ **Gerenciamento de Estado:** Lógica de estado centralizada e compartilhada via Context API.
- ✅ **Dashboard Financeiro:** Balanço (entradas, saídas, saldo) e gráfico de rosca (`Chart.js`) dinâmicos.
- ✅ **Navegação de Histórico:** Interface para visualizar transações de meses anteriores/posteriores.
- ✅ **Gerenciamento de Recorrentes:** Interface para CRUD de modelos recorrentes.
- ✅ **Consumo de IA:** Funcionalidade para solicitar e exibir sugestões da IA.
- ✅ **UX Melhorada:** Notificações de sucesso/erro integradas à página (sem `alert()`).

---

## 🚀 Tecnologias Utilizadas

### **Backend**
- Java 21
- Spring Boot 3.5.6
- Spring Security (Autenticação com JWT)
- Spring Data JPA / Hibernate
- PostgreSQL 15 (AWS RDS)
- Nginx (Reverse Proxy + HTTPS)
- Let's Encrypt (Certificados SSL/TLS via Certbot)
- Maven
- JJWT 0.13.0
- SpringDoc OpenAPI (Swagger)
- JUnit 5 / Mockito

### **Frontend**
- React 19
- Vite
- React Router DOM v7
- Chart.js / react-chartjs-2
- React Icons
- CSS Modules / CSS Padrão

### **Cloud & DevOps**
- AWS EC2 (Ubuntu)
- AWS RDS (PostgreSQL)
- Netlify (Hospedagem Frontend + CDN)
- GitHub Actions (CI para o Backend)
- Docker & Docker Compose (Para banco de dados local opcional)

### **Serviços Externos**
- Google Gemini API

---

## 🏁 Como Rodar o Projeto Localmente

**Pré-requisitos:** JDK 21+, Docker (opcional, para DB local), Node.js 18+ e Git.

### **1. Backend**

```bash
# Clone o repositório
git clone [https://github.com/NoClick11/Gerenciador-Financeiro.git](https://github.com/NoClick11/Gerenciador-Financeiro.git)
cd Gerenciador-Financeiro

# Configure as variáveis de ambiente (necessárias para rodar)
# Você pode configurar no seu sistema ou nas configurações da sua IDE (ex: IntelliJ Run Configuration)
export JWT_SECRET_KEY="SUA_CHAVE_SECRETA_JWT_AQUI"
export GEMINI_API_KEY="SUA_CHAVE_DA_API_DO_GEMINI_AQUI"

# Opção A: Rodar com banco de dados Docker local (Recomendado para dev isolado)
# (Certifique-se que o Docker Desktop está rodando)
docker-compose up -d
# Rode a aplicação (usará application.properties por padrão, que aponta para localhost:5433)
./mvnw spring-boot:run

# Opção B: Rodar conectado ao banco de dados da AWS (Requer configuração extra)
# 1. Crie um application-dev.properties em src/main/resources
# 2. Copie o conteúdo de application-prod.properties para ele
# 3. Exporte as variáveis JDBC_* (URL, USERNAME, PASSWORD) do RDS no seu terminal local
# 4. Rode a aplicação ativando o perfil 'dev':
# ./mvnw spring-boot:run -Dspring.profiles.active=dev

# O backend estará rodando em http://localhost:8080.

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
A documentação completa e interativa dos endpoints do backend está disponível na instância online:

➡️ Swagger UI https://manuelneto.shop/swagger-ui.html

LinkedIn: https://www.linkedin.com/in/manuel-bahamonde-372969374

GitHub: https://github.com/NoClick11

