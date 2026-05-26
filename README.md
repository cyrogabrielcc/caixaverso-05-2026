# Simulador de Financiamentos - Java

API backend desenvolvida em **Java 25**, **Quarkus** e **H2 Database** para simular financiamentos com juros compostos, persistir a simulação e permitir consulta posterior pelo ID.

## Stack utilizada

- Java 25
- Quarkus
- H2 Database embutido
- Hibernate ORM / Panache
- Jakarta Validation
- OpenAPI / Swagger
- JUnit 5
- RestAssured
- JaCoCo
- Maven

> O projeto não utiliza Docker nem Docker Compose.

## Requisitos da aplicação

A API permite:

- criar uma simulação de financiamento;
- calcular juros compostos mês a mês;
- gerar memória de cálculo com saldo inicial, juro e saldo final;
- persistir o resultado no banco H2;
- consultar uma simulação existente pelo ID;
- expor documentação via Swagger.

## Como executar o projeto

Na raiz do projeto, execute:

```bash
mvn quarkus:dev
```

Ou, usando Maven Wrapper no Windows:

```bash
mvnw.cmd quarkus:dev
```

A aplicação ficará disponível em:

```text
http://localhost:8080
```

## Swagger

Com a aplicação em execução, acesse:

```text
http://localhost:8080/q/swagger-ui
```

## Endpoints

### Criar simulação

```http
POST /simulacoes
```

Exemplo de requisição:

```json
{
  "valorInicial": 1000.00,
  "taxaJurosMensal": 1.5,
  "prazoMeses": 3
}
```

Exemplo de resposta:

```json
{
  "id": 1,
  "valorInicial": 1000.00,
  "taxaJurosMensal": 1.500000,
  "prazoMeses": 3,
  "valorTotalFinal": 1045.68,
  "valorTotalJuros": 45.68,
  "memoriaCalculo": [
    {
      "mes": 1,
      "saldoInicial": 1000.00,
      "juro": 15.00,
      "saldoFinal": 1015.00
    },
    {
      "mes": 2,
      "saldoInicial": 1015.00,
      "juro": 15.23,
      "saldoFinal": 1030.23
    },
    {
      "mes": 3,
      "saldoInicial": 1030.23,
      "juro": 15.45,
      "saldoFinal": 1045.68
    }
  ]
}
```

### Consultar simulação por ID

```http
GET /simulacoes/{id}
```

Exemplo:

```http
GET /simulacoes/1
```

## Como compilar

```bash
mvn clean compile
```

Ou, usando Maven Wrapper no Windows:

```bash
mvnw.cmd clean compile
```

## Como executar os testes e validar cobertura

Para executar a suíte de testes e validar a cobertura mínima configurada pelo JaCoCo, execute:

```bash
mvn clean verify
```

Ou, usando Maven Wrapper no Windows:

```bash
mvnw.cmd clean verify
```

O relatório de cobertura será gerado em:

```text
target/site/jacoco/index.html
```

## Observações técnicas

- Os cálculos financeiros utilizam `BigDecimal`.
- A taxa de juros mensal é recebida em formato percentual.
- A memória de cálculo é persistida junto com a simulação.
- A API retorna respostas HTTP adequadas para sucesso, validação e recursos não encontrados.
- As tabelas do H2 são criadas automaticamente pela aplicação.
