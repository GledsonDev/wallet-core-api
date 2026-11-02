# Wallet Core

Um sistema de carteira digital e painel administrativo desenvolvido para demonstrar padrões arquiteturais avançados aplicados a transações financeiras. O projeto foca em alta concorrência, resiliência e processamento assíncrono.

## 🚀 Arquitetura e Tecnologias

O projeto adota uma estrutura em **Monorepo**, dividindo as responsabilidades entre uma API de alta performance e uma interface reativa.

### Back-end (Java 21 LTS & Quarkus 3)
* **Garantia de Idempotência (Redis):** Interceção de transações duplicadas (ex: cliques múltiplos na interface) em milissegundos utilizando chaves únicas de transação, protegendo a integridade do saldo.
* **Processamento Assíncrono (RabbitMQ):** Desacoplamento da consolidação financeira. A API REST regista a intenção, e os *Workers* em background executam o cálculo transacional e atualizam o banco de dados, permitindo altíssima escalabilidade.
* **Persistência (PostgreSQL & Hibernate Panache):** Armazenamento relacional seguro para entidades de `Account` e `Transaction`.
* **Design Patterns:** Separação clara de domínio e infraestrutura, facilitando testes e manutenção.

### Front-end (React & TypeScript)
* **Core:** React com tipagem estrita para segurança de dados no cliente.
* **Build & Roteamento:** Vite com configuração de Proxy reverso em ambiente de desenvolvimento, eliminando problemas de CORS nativamente.
* **Estilização:** Tailwind CSS para um design responsivo, limpo e condicional (indicadores de débito/crédito).

## ⚙️ Como Executar o Projeto

Certifique-se de ter o **Docker Desktop**, **Java 21**, **Maven** e **Node.js** instalados.

### 1. Subir a Infraestrutura
```bash
cd backend
docker-compose up -d
```

### 2. Iniciar a API Quarkus (Porta 8080)
```bash
cd backend
./mvnw compile quarkus:dev
```
A API expõe o Swagger UI em http://localhost:8080/q/swagger-ui/ para exploração dos endpoints REST.

### 3. Iniciar o Back-Office React (Porta 5173)
```bash
cd frontend
npm install
npm run dev
```