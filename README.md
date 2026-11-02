# 🏦 WalletCore API - Motor de Transações Financeiras

Este projeto é um microsserviço de processamento de transações financeiras projetado para suportar cenários de alta concorrência. Desenvolvido para validar conceitos avançados de engenharia de software, o sistema garante a consistência de dados, prevenção de falhas sistêmicas e processamento assíncrono.

## 🎯 Arquitetura e Padrões
O projeto foi estruturado seguindo os princípios da **Arquitetura Hexagonal (Clean Architecture)**, separando o domínio central das camadas de infraestrutura e entrega. 

- **Idempotência:** Implementação de mecanismos antifraude/duplicação de requisições utilizando **Redis** como camada de controle hiper-rápida.
- **Processamento Assíncrono:** Utilização de **RabbitMQ** para o enfileiramento de processamento de saldos, garantindo resiliência e evitando sobrecarga no banco de dados principal.
- **Painel de Back-Office:** Interface integrada construída em **React** para monitoramento operacional em tempo real.

## 🛠️ Tecnologias Utilizadas
- **Backend:** Java 21 | Quarkus Framework
- **Banco de Dados (Persistência):** PostgreSQL
- **Cache & Controle (In-Memory):** Redis
- **Mensageria (Event-Driven):** RabbitMQ
- **Frontend (Back-Office):** React.js
- **DevOps & Qualidade:** Docker | Docker Compose | GitHub Actions (CI) | JUnit5 (Testcontainers)

## ⚙️ Como Executar o Projeto Localmente

**1. Pré-requisitos**
Certifique-se de ter o Docker e o Docker Compose instalados na sua máquina.

**2. Subindo a Infraestrutura (Banco, Cache e Mensageria)**
```bash
docker-compose up -d
