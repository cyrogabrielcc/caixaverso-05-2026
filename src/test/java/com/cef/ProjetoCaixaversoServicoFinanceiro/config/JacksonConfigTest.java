package com.cef.ProjetoCaixaversoServicoFinanceiro.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class JacksonConfigTest {

    @Test
    void deveFalharQuandoCampoIntegerVierComoString() {
        ObjectMapper objectMapper = criarObjectMapperConfigurado();

        String json = """
                {
                    "prazoMeses": "12",
                    "taxaJurosMensal": 1.5,
                    "valorInicial": 1000.00
                }
                """;

        assertThrows(JsonMappingException.class, () ->
                objectMapper.readValue(json, EntradaTeste.class)
        );
    }

    @Test
    void deveFalharQuandoCampoIntegerVierComoStringVazia() {
        ObjectMapper objectMapper = criarObjectMapperConfigurado();

        String json = """
                {
                    "prazoMeses": "",
                    "taxaJurosMensal": 1.5,
                    "valorInicial": 1000.00
                }
                """;

        assertThrows(JsonMappingException.class, () ->
                objectMapper.readValue(json, EntradaTeste.class)
        );
    }

    @Test
    void deveFalharQuandoCampoFloatVierComoString() {
        ObjectMapper objectMapper = criarObjectMapperConfigurado();

        String json = """
                {
                    "prazoMeses": 12,
                    "taxaJurosMensal": "1.5",
                    "valorInicial": 1000.00
                }
                """;

        assertThrows(JsonMappingException.class, () ->
                objectMapper.readValue(json, EntradaTeste.class)
        );
    }

    @Test
    void deveFalharQuandoCampoFloatVierComoStringVazia() {
        ObjectMapper objectMapper = criarObjectMapperConfigurado();

        String json = """
                {
                    "prazoMeses": 12,
                    "taxaJurosMensal": "",
                    "valorInicial": 1000.00
                }
                """;

        assertThrows(JsonMappingException.class, () ->
                objectMapper.readValue(json, EntradaTeste.class)
        );
    }

    @Test
    void deveDesserializarQuandoCamposNumericosForemValidos() throws Exception {
        ObjectMapper objectMapper = criarObjectMapperConfigurado();

        String json = """
                {
                    "prazoMeses": 12,
                    "taxaJurosMensal": 1.5,
                    "valorInicial": 1000.00
                }
                """;

        EntradaTeste entrada = objectMapper.readValue(json, EntradaTeste.class);

        assertEquals(12, entrada.getPrazoMeses());
        assertEquals(0, new BigDecimal("1.5").compareTo(entrada.getTaxaJurosMensal()));
        assertEquals(0, new BigDecimal("1000.00").compareTo(entrada.getValorInicial()));
    }

    private ObjectMapper criarObjectMapperConfigurado() {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonConfig jacksonConfig = new JacksonConfig();

        jacksonConfig.customize(objectMapper);

        return objectMapper;
    }

    static class EntradaTeste {
        private Integer prazoMeses;
        private BigDecimal taxaJurosMensal;
        private BigDecimal valorInicial;

        public Integer getPrazoMeses() {
            return prazoMeses;
        }

        public void setPrazoMeses(Integer prazoMeses) {
            this.prazoMeses = prazoMeses;
        }

        public BigDecimal getTaxaJurosMensal() {
            return taxaJurosMensal;
        }

        public void setTaxaJurosMensal(BigDecimal taxaJurosMensal) {
            this.taxaJurosMensal = taxaJurosMensal;
        }

        public BigDecimal getValorInicial() {
            return valorInicial;
        }

        public void setValorInicial(BigDecimal valorInicial) {
            this.valorInicial = valorInicial;
        }
    }
}