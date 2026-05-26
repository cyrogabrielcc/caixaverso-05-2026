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
class SimulacaoResourceIntegrationTest {

    @Test
    void deveCriarSimulacaoEPersistirPermitindoConsultaPorIdViaHttp() {
        String payload = """
                {
                  "valorInicial": 1000.00,
                  "taxaJurosMensal": 1.5,
                  "prazoMeses": 3
                }
                """;

        Response postResponse = given()
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

        JsonPath postJson = postResponse.jsonPath();

        Long id = postJson.getLong("id");

        assertNotNull(id);
        assertTrue(id > 0);

        String location = postResponse.getHeader("Location");
        assertNotNull(location);
        assertTrue(
                location.endsWith("/simulacoes/" + id),
                () -> "Location retornado: " + location
        );

        validarRespostaSimulacao(postJson, id);

        Response getResponse = given()
                .accept(ContentType.JSON)
                .when()
                .get("/simulacoes/{id}", id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        JsonPath getJson = getResponse.jsonPath();

        validarRespostaSimulacao(getJson, id);
    }

    @Test
    void deveRetornarBadRequestQuandoCampoNumericoVierComoTextoViaHttp() {
        String payload = """
                {
                  "valorInicial": 1000.00,
                  "taxaJurosMensal": 1.5,
                  "prazoMeses": "12"
                }
                """;

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
        assertNotNull(json.getString("erro"));
        assertNotNull(json.getString("mensagem"));
        assertNotNull(json.getString("caminho"));
        assertNotNull(json.getList("detalhes"));
    }

    @Test
    void deveRetornarBadRequestQuandoJsonForMalformadoViaHttp() {
        String payloadMalformado = """
                {
                  "valorInicial": 1000.00,
                  "taxaJurosMensal": 1.5,
                  "prazoMeses": 12
                """;

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payloadMalformado)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        assertEquals(400, json.getInt("status"));
        assertNotNull(json.getString("erro"));
        assertNotNull(json.getString("mensagem"));
        assertNotNull(json.getString("caminho"));
        assertNotNull(json.getList("detalhes"));
    }

    @Test
    void deveRetornarNotFoundQuandoSimulacaoNaoExistirViaHttp() {
        Long idInexistente = 999999999L;

        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get("/simulacoes/{id}", idInexistente)
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        assertEquals(404, json.getInt("status"));
        assertNotNull(json.getString("erro"));
        assertNotNull(json.getString("mensagem"));
        assertEquals("/simulacoes/" + idInexistente, json.getString("caminho"));
    }

    private void validarRespostaSimulacao(JsonPath json, Long idEsperado) {
        assertEquals(idEsperado, json.getLong("id"));

        assertBigDecimalEquals("1000.00", json.get("valorInicial"));
        assertBigDecimalEquals("1.500000", json.get("taxaJurosMensal"));
        assertEquals(3, json.getInt("prazoMeses"));
        assertBigDecimalEquals("1045.68", json.get("valorTotalFinal"));
        assertBigDecimalEquals("45.68", json.get("valorTotalJuros"));

        assertNotNull(json.getList("memoriaCalculo"));
        assertEquals(3, json.getList("memoriaCalculo").size());

        assertEquals(1, json.getInt("memoriaCalculo[0].mes"));
        assertBigDecimalEquals("1000.00", json.get("memoriaCalculo[0].saldoInicial"));
        assertBigDecimalEquals("15.00", json.get("memoriaCalculo[0].juro"));
        assertBigDecimalEquals("1015.00", json.get("memoriaCalculo[0].saldoFinal"));

        assertEquals(2, json.getInt("memoriaCalculo[1].mes"));
        assertBigDecimalEquals("1015.00", json.get("memoriaCalculo[1].saldoInicial"));
        assertBigDecimalEquals("15.23", json.get("memoriaCalculo[1].juro"));
        assertBigDecimalEquals("1030.23", json.get("memoriaCalculo[1].saldoFinal"));

        assertEquals(3, json.getInt("memoriaCalculo[2].mes"));
        assertBigDecimalEquals("1030.23", json.get("memoriaCalculo[2].saldoInicial"));
        assertBigDecimalEquals("15.45", json.get("memoriaCalculo[2].juro"));
        assertBigDecimalEquals("1045.68", json.get("memoriaCalculo[2].saldoFinal"));
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