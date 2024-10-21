# Microserviço Order Processor

## Visão Geral

Este projeto é um **microserviço para processamento de pedidos de clientes**. Ele escuta eventos de uma fila RabbitMQ, processa os pedidos, armazena-os em um banco de dados MongoDB e fornece endpoints para a recuperação e resumo dos pedidos de clientes. O microserviço foi projetado para lidar com todo o ciclo de vida de um pedido: desde o consumo de eventos até a geração de relatórios.

## Funcionalidades

- **Integração com RabbitMQ**: Consome mensagens da fila `order_created_queue`.
- **Armazenamento no MongoDB**: Os pedidos são armazenados na coleção `tb_orders` no MongoDB.
- **API de Pedidos**: Exposição de endpoints RESTful para listar pedidos de clientes e calcular resumos de pedidos.
- **Comunicação baseada em DTOs**: DTOs bem definidos para uma comunicação robusta entre os componentes.

## Tecnologias

- **Java 17**
- **Spring Boot**
- **MongoDB** (como banco de dados)
- **RabbitMQ** (para mensageria)
- **JUnit 5 & Mockito** (para testes)
- **Maven** (ferramenta de build)

## Iniciando o Projeto

### Pré-requisitos

Para rodar este projeto, você precisará de:

- **Java 17**
- **MongoDB**
- **RabbitMQ**
- **Maven**

### Rodando Localmente

1. Clone o repositório:

   ```bash
   git clone https://github.com/seuusuario/order-processor.git
   ```

2. Navegue até o diretório do projeto:

   ```bash
   cd order-processor
   ```

3. Execute a aplicação:

   ```bash
   mvn spring-boot:run
   ```

### Configurações

Você pode configurar os detalhes de conexão do RabbitMQ e MongoDB no arquivo `application.yml`, localizado em `src/main/resources/`.

### Endpoints da API

- **GET /orders?customerId={id}&page={page}&pageSize={size}**  
  Retorna uma lista paginada de pedidos para um cliente específico.

  **Parâmetros:**
  - `customerId` (obrigatório): ID do cliente
  - `page`: Página da paginação (padrão é 0)
  - `pageSize`: Número de pedidos por página (padrão é 10)

  **Resposta**:
  - Retorna uma lista paginada de pedidos e um resumo do valor total dos pedidos.

### Exemplo de Resposta

```json
{
  "data": [
    {
      "orderId": 1,
      "customerId": 2,
      "totalPrice": 20.50
    }
  ],
  "pagination": {
    "totalElements": 1,
    "totalPages": 1,
    "page": 0,
    "pageSize": 10
  },
  "summary": {
    "totalOnOrders": 20.50
  }
}
```

## Testes

O projeto inclui testes unitários abrangentes para garantir a funcionalidade do `OrderController`. Os testes cobrem:

- Tratamento de respostas vazias
- Verificação de parâmetros corretos passados para os serviços
- Manuseio correto de diferentes cenários de pedidos de clientes

Execute os testes com:

```bash
mvn test
```

## Melhorias Futuras

- Adicionar suporte para mensageria com Kafka.
- Estender a API para suportar atualizações e cancelamentos de pedidos.
- Implementar funcionalidades mais detalhadas de relatórios.
