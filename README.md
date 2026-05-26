# Projeto Caixaverso - Serviço Financeiro

API backend em Java com Quarkus para simulação de financiamentos com juros compostos, geração de memória de cálculo mensal e persistência em banco H2 embutido.

A aplicação executa de forma 100% nativa, sem Docker ou Docker Compose.

---

## Requisitos

- Java 25
- Maven

Verifique as versões instaladas:

## Comandos principais

```bash
# Compila o projeto e verifica se o código-fonte está sem erros de compilação
mvn clean compile

# Executa os testes automatizados do projeto
mvn clean test

# Executa os testes e gera o relatório de cobertura do JaCoCo
mvn clean test jacoco:report

# Executa a validação completa da entrega: compila, testa, gera relatório e valida a cobertura mínima configurada
mvn clean verify

# Inicia a aplicação em modo desenvolvimento
mvn quarkus:dev
```

O comando principal para avaliação da entrega é:

```bash
mvn clean verify
```

Esse comando compila o projeto, executa a suíte de testes, gera o relatório do JaCoCo e valida a cobertura mínima configurada no projeto.

Se a cobertura mínima não for atingida, o build será finalizado com erro.

---

## Relatório de cobertura JaCoCo

O relatório HTML será gerado em:

```text
target/site/jacoco/index.html
```

Abra esse arquivo no navegador para visualizar a cobertura por pacote, classe e método.

---

## Execução da aplicação

Após executar:

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

As tabelas são criadas automaticamente pela aplicação. Não é necessário instalar banco externo nem executar scripts SQL manuais.

---

## Endpoints principais

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

## Formato da resposta de simulação

```json
{
  "id": 1,
  "valorInicial": 1000.00,
  "taxaJurosMensal": 1.500000,
  "prazoMeses": 12,
  "valorTotalFinal": 1195.62,
  "valorTotalJuros": 195.62,
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
```
####  Obs: em juros compostos o saldo inicial de cada mês, deve ser o saldo final do mês anterior
```
---

## Validações principais

A API valida:

- `valorInicial`: obrigatório, numérico e maior que zero;
- `taxaJurosMensal`: obrigatória, numérica e maior ou igual a zero;
- `prazoMeses`: obrigatório, numérico, inteiro e maior que zero;
- `id`: obrigatório e maior que zero nas consultas.

Falhas de validação e regras de negócio retornam HTTP 400.

Simulações inexistentes retornam HTTP 404.

---

## Precisão financeira

Os cálculos financeiros utilizam `BigDecimal`, evitando perdas de precisão de `double` ou `float`.

A memória de cálculo registra a evolução mensal do saldo:

- mês;
- saldo inicial;
- juro do período;
- saldo final.

---

## Empacotar a aplicação

```bash
mvn clean package
```

Executar o pacote gerado:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```