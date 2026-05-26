package com.cef.ProjetoCaixaversoServicoFinanceiro.resources;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class SimulacaoResourceValidationIntegrationTest {

    @Test
    void deveRetornarBadRequestQuandoValorInicialForNuloViaHttp() {
        String payload = """
                {
                  "valorInicial": null,
                  "taxaJurosMensal": 1.5,
                  "prazoMeses": 12
                }
                """;

        validarBadRequest(payload);
    }

    @Test
    void deveRetornarBadRequestQuandoValorInicialForZeroViaHttp() {
        String payload = """
                {
                  "valorInicial": 0.00,
                  "taxaJurosMensal": 1.5,
                  "prazoMeses": 12
                }
                """;

        validarBadRequest(payload);
    }

    @Test
    void deveRetornarBadRequestQuandoValorInicialForNegativoViaHttp() {
        String payload = """
                {
                  "valorInicial": -1000.00,
                  "taxaJurosMensal": 1.5,
                  "prazoMeses": 12
                }
                """;

        validarBadRequest(payload);
    }

    @Test
    void deveRetornarBadRequestQuandoTaxaJurosMensalForNulaViaHttp() {
        String payload = """
                {
                  "valorInicial": 1000.00,
                  "taxaJurosMensal": null,
                  "prazoMeses": 12
                }
                """;

        validarBadRequest(payload);
    }

    @Test
    void deveRetornarBadRequestQuandoTaxaJurosMensalForNegativaViaHttp() {
        String payload = """
                {
                  "valorInicial": 1000.00,
                  "taxaJurosMensal": -1.5,
                  "prazoMeses": 12
                }
                """;

        validarBadRequest(payload);
    }

    @Test
    void deveRetornarBadRequestQuandoPrazoMesesForNuloViaHttp() {
        String payload = """
                {
                  "valorInicial": 1000.00,
                  "taxaJurosMensal": 1.5,
                  "prazoMeses": null
                }
                """;

        validarBadRequest(payload);
    }

    @Test
    void deveRetornarBadRequestQuandoPrazoMesesForZeroViaHttp() {
        String payload = """
                {
                  "valorInicial": 1000.00,
                  "taxaJurosMensal": 1.5,
                  "prazoMeses": 0
                }
                """;

        validarBadRequest(payload);
    }

    @Test
    void deveRetornarBadRequestQuandoPrazoMesesForNegativoViaHttp() {
        String payload = """
                {
                  "valorInicial": 1000.00,
                  "taxaJurosMensal": 1.5,
                  "prazoMeses": -1
                }
                """;

        validarBadRequest(payload);
    }

    @Test
    void devePermitirTaxaJurosMensalZeroViaHttp() {
        String payload = """
                {
                  "valorInicial": 1000.00,
                  "taxaJurosMensal": 0.00,
                  "prazoMeses": 3
                }
                """;

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        assertNotNull(json.getLong("id"));
        assertBigDecimalEquals("1000.00", json.get("valorInicial"));
        assertBigDecimalEquals("0.000000", json.get("taxaJurosMensal"));
        assertEquals(3, json.getInt("prazoMeses"));
        assertBigDecimalEquals("1000.00", json.get("valorTotalFinal"));
        assertBigDecimalEquals("0.00", json.get("valorTotalJuros"));

        assertNotNull(json.getList("memoriaCalculo"));
        assertEquals(3, json.getList("memoriaCalculo").size());

        assertEquals(1, json.getInt("memoriaCalculo[0].mes"));
        assertBigDecimalEquals("1000.00", json.get("memoriaCalculo[0].saldoInicial"));
        assertBigDecimalEquals("0.00", json.get("memoriaCalculo[0].juro"));
        assertBigDecimalEquals("1000.00", json.get("memoriaCalculo[0].saldoFinal"));
    }

    @Test
    void deveRetornarBadRequestQuandoIdForZeroViaHttp() {
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get("/simulacoes/0")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        assertEquals(400, json.getInt("status"));
        assertNotNull(json.getString("erro"));
        assertFalse(json.getString("erro").isBlank());
        assertNotNull(json.getString("mensagem"));
        assertFalse(json.getString("mensagem").isBlank());
        assertEquals("/simulacoes/0", json.getString("caminho"));
    }

    private void validarBadRequest(String payload) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        assertEquals(400, json.getInt("status"));

        String erro = json.getString("erro");
        assertNotNull(erro);
        assertFalse(erro.isBlank());

        String mensagem = json.getString("mensagem");
        assertNotNull(mensagem);
        assertFalse(mensagem.isBlank());

        assertEquals("/simulacoes", json.getString("caminho"));

        String timestamp = json.getString("timestamp");
        assertNotNull(timestamp);
        assertFalse(timestamp.isBlank());

        assertNotNull(json.getList("detalhes"));
    }

    private void assertBigDecimalEquals(String esperado, Object valorRecebido) {
        assertNotNull(valorRecebido);

        BigDecimal valorEsperado = new BigDecimal(esperado);
        BigDecimal valorAtual = new BigDecimal(valorRecebido.toString());

        assertEquals(
                0,
                valorEsperado.compareTo(valorAtual),
                () -> "Esperado: " + valorEsperado + ", recebido: " + valorAtual
        );
    }
}