# Checkout Service

Microsserviço escrito em Java Spring responsável pelo gerenciamento de pedidos e integração com gateway de pagamentos escrito em Node com Express. O serviço utiliza uma arquitetura orientada a eventos com RabbitMQ para processamento assíncrono de pagamentos.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Spring AMQP (RabbitMQ)
- MongoDB
- Redis
- H2 Database
- Maven
- Docker (opcional)

## Pré-requisitos

- JDK 17 ou superior
- Maven 3.8 ou superior
- Docker & Docker Compose (opcional)
- MongoDB
- Redis
- RabbitMQ

## Configuração do Ambiente

1. Clone o repositório:
```bash
git clone https://github.com/your-username/checkout-service
cd checkout-service
```

2. Configure as variáveis de ambiente no `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:checkoutdb
    username: sa
    password: password
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
  data:
    mongodb:
      uri: mongodb://localhost:27017/transactions
    redis:
      host: localhost
      port: 6379
```

## Executando com Docker

1. Inicie os serviços necessários:
```bash
docker-compose up -d
```

2. Build e execução do serviço:
```bash
# Build do projeto
mvn clean package

# Execução do container
docker run -d --name checkout-service \
  --network host \
  -p 8080:8080 \
  checkout-service:latest
```

## Executando Localmente

1. Certifique-se que MongoDB, Redis e RabbitMQ estão rodando

2. Execute o projeto:
```bash
# Build e execução
mvn spring-boot:run

# Ou em duas etapas
mvn clean package
java -jar target/checkout-service-1.0.0.jar
```

## Executando os Testes

```bash
# Executar todos os testes
mvn test

# Executar apenas testes unitários
mvn test -Dtest=*Test

# Executar apenas testes de integração
mvn test -Dtest=*IntegrationTest

# Executar com relatório de cobertura
mvn verify
```

## Endpoints da API

### Criar Pedido
```http
POST /api/orders
Content-Type: application/json

{
  "customerEmail": "customer@example.com",
  "amount": 100.00
}
```

### Consultar Pedido
```http
GET /api/orders/{orderId}
```

### Consultar Status do Pedido
```http
GET /api/orders/{orderId}/status
```

## Integração com RabbitMQ

O serviço utiliza as seguintes exchanges e filas:

- Exchange: `payment_exchange`
- Filas:
    - `payment_request_queue`: Envia requisições de pagamento
    - `payment_result_queue`: Recebe resultados de processamento

### Formato das Mensagens

Requisição de Pagamento:
```json
{
  "id": 123,
  "customerEmail": "customer@example.com",
  "amount": 100.00,
  "status": "PENDING"
}
```

Resultado do Pagamento:
```json
{
  "orderId": 123,
  "success": true,
  "transactionId": "tx_abc123",
  "errorMessage": null
}
```

## Persistência de Dados

### H2 Database
- Armazena os pedidos e seus status
- Console disponível em: http://localhost:8080/h2-console

### MongoDB
- Armazena o histórico completo de transações
- Rastreamento de mudanças de status
- Registro de tentativas de pagamento

### Redis
- Cache de status de pedidos
- Melhoria de performance para consultas frequentes

## Monitoramento

O serviço expõe métricas através do Spring Actuator:
- Health check: http://localhost:8080/actuator/health
- Métricas: http://localhost:8080/actuator/metrics

## Troubleshooting

### Aplicação não inicia
1. Verifique se as portas não estão em uso
2. Confirme que todos os serviços externos estão rodando
3. Verifique os logs da aplicação:
```bash
tail -f logs/application.log
```

### Problemas com RabbitMQ
1. Verifique a conexão:
```bash
rabbitmqctl status
```

2. Verifique as filas:
```bash
rabbitmqctl list_queues
```

### Testes falham
1. Limpe e recompile o projeto:
```bash
mvn clean install
```

2. Verifique se o TestContainers pode acessar o Docker:
```bash
docker ps
```

## Docker Compose

```yaml
version: '3.8'

services:
  mongodb:
    image: mongo:latest
    ports:
      - "27017:27017"

  redis:
    image: redis:latest
    ports:
      - "6379:6379"

  rabbitmq:
    image: rabbitmq:management
    ports:
      - "5672:5672"
      - "15672:15672"

  checkout-service:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mongodb
      - redis
      - rabbitmq
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/transactions
      - SPRING_REDIS_HOST=redis
      - SPRING_RABBITMQ_HOST=rabbitmq
```