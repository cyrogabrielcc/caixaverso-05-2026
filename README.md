# Projeto Caixaverso - Serviço Financeiro

API backend desenvolvida em Java 25 com Quarkus para simulação de financiamentos com juros compostos, geração de memória de cálculo mensal e persistência em banco H2 embutido.

A aplicação executa de forma nativa, sem Docker ou Docker Compose.

---

## Requisitos

- Java 25
- Maven

---

## Tecnologias utilizadas

- Java 25
- Quarkus
- H2 Database
- Hibernate ORM com Panache
- Jakarta REST
- Jakarta Validation
- OpenAPI / Swagger
- JUnit 5
- RestAssured
- Mockito
- JaCoCo - Cobertura de Testes de 93%

---

## Como executar a validação da entrega

Execute o comando abaixo na raiz do projeto:

```bash
mvn clean verify
```

Esse comando compila o projeto, executa os testes automatizados, gera o relatório de cobertura do JaCoCo e valida a cobertura mínima configurada.

---

## Relatório de cobertura

Após a execução da validação, o relatório HTML do JaCoCo será gerado em:

```text
target/site/jacoco/index.html
```

Abra esse arquivo no navegador para consultar a cobertura por pacote, classe e método.

---

## Como executar a aplicação

Execute:

```bash
mvn quarkus:dev
```

A aplicação ficará disponível em:

```text
http://localhost:8080
```

---

## Swagger / OpenAPI

Com a aplicação em execução, acesse:

```text
http://localhost:8080/q/swagger-ui
```

A especificação OpenAPI fica disponível em:

```text
http://localhost:8080/q/openapi
```

---

## Banco de dados

O projeto utiliza H2 Database em modo embutido.

As tabelas são criadas automaticamente pela aplicação, sem necessidade de instalação de banco externo ou execução manual de scripts SQL.

---

## Endpoints

### Criar simulação

```http
POST /simulacoes
```

Payload de exemplo:

```json
{
  "valorInicial": 1000.00,
  "taxaJurosMensal": 1.5,
  "prazoMeses": 12
}
```

### Consultar simulação por ID

```http
GET /simulacoes/{id}
```

---

## Exemplo de resposta

```json
{
  "id": 1,
  "valorInicial": 1000.00,
  "taxaJurosMensal": 1.500000,
  "prazoMeses": 12,
  "valorTotalFinal": 1195.63,
  "valorTotalJuros": 195.63,
  "memoriaCalculo": [
    {
      "mes": 1,
      "saldoInicial": 1000.00,
      "juro": 15.00,
      "saldoFinal": 1015.00
    }
  ]
}
```

Na memória de cálculo, o saldo inicial de cada mês corresponde ao saldo final do mês anterior.

---

## Validações

A API valida os seguintes campos:

- `valorInicial`: obrigatório, numérico e maior que zero;
- `taxaJurosMensal`: obrigatória, numérica e maior ou igual a zero;
- `prazoMeses`: obrigatório, numérico, inteiro e maior que zero;
- `id`: obrigatório e maior que zero nas consultas.

Falhas de validação e regras de negócio retornam HTTP 400.

Simulações inexistentes retornam HTTP 404.

---

## Precisão financeira

Os cálculos financeiros utilizam `BigDecimal`, evitando perda de precisão por uso de `double` ou `float`.

A memória de cálculo registra, para cada mês:

- mês;
- saldo inicial;
- juro do período;
- saldo final.

---

## Empacotamento

Para gerar o pacote da aplicação, execute:

```bash
mvn clean package
```

O artefato executável será gerado na estrutura padrão do Quarkus.
