package com.cef.ProjetoCaixaversoServicoFinanceiro.dto;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SimulacaoRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void configurarValidador() {
        validator = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory()
                .getValidator();
    }

    @Test
    void deveCriarRequestDTOComGettersESetters() {
        SimulacaoRequestDTO dto = new SimulacaoRequestDTO();

        BigDecimal valorInicial = new BigDecimal("1000.00");
        BigDecimal taxaJurosMensal = new BigDecimal("1.50");
        Integer prazoMeses = (Integer) 12;

        dto.setValorInicial(valorInicial);
        dto.setTaxaJurosMensal(taxaJurosMensal);
        dto.setPrazoMeses(prazoMeses);

        assertEquals(valorInicial, dto.getValorInicial());
        assertEquals(taxaJurosMensal, dto.getTaxaJurosMensal());
        assertEquals(prazoMeses, dto.getPrazoMeses());
    }

    @Test
    void naoDeveGerarViolacoesQuandoRequestForValido() {
        SimulacaoRequestDTO dto = criarRequest(
                new BigDecimal("1000.00"),
                new BigDecimal("1.50"),
                Integer.valueOf(12)
        );

        Set<ConstraintViolation<SimulacaoRequestDTO>> violacoes = validator.validate(dto);

        assertTrue(violacoes.isEmpty());
    }

    @Test
    void deveGerarViolacaoQuandoValorInicialForNulo() {
        SimulacaoRequestDTO dto = criarRequest(
                null,
                new BigDecimal("1.50"),
                Integer.valueOf(12)
        );

        Set<ConstraintViolation<SimulacaoRequestDTO>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(contemViolacao(
                violacoes,
                "valorInicial",
                "O valor inicial é obrigatório."
        ));
    }

    @Test
    void deveGerarViolacaoQuandoValorInicialForZero() {
        SimulacaoRequestDTO dto = criarRequest(
                new BigDecimal("0.00"),
                new BigDecimal("1.50"),
                Integer.valueOf(12)
        );

        Set<ConstraintViolation<SimulacaoRequestDTO>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(contemViolacao(
                violacoes,
                "valorInicial",
                "O valor inicial deve ser maior que zero."
        ));
    }

    @Test
    void deveGerarViolacaoQuandoValorInicialForNegativo() {
        SimulacaoRequestDTO dto = criarRequest(
                new BigDecimal("-1000.00"),
                new BigDecimal("1.50"),
                Integer.valueOf(12)
        );

        Set<ConstraintViolation<SimulacaoRequestDTO>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(contemViolacao(
                violacoes,
                "valorInicial",
                "O valor inicial deve ser maior que zero."
        ));
    }

    @Test
    void deveGerarViolacaoQuandoTaxaJurosMensalForNula() {
        SimulacaoRequestDTO dto = criarRequest(
                new BigDecimal("1000.00"),
                null,
                Integer.valueOf(12)
        );

        Set<ConstraintViolation<SimulacaoRequestDTO>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(contemViolacao(
                violacoes,
                "taxaJurosMensal",
                "A taxa de juros mensal é obrigatória."
        ));
    }

    @Test
    void devePermitirTaxaJurosMensalZero() {
        SimulacaoRequestDTO dto = criarRequest(
                new BigDecimal("1000.00"),
                new BigDecimal("0.00"),
                Integer.valueOf(12)
        );

        Set<ConstraintViolation<SimulacaoRequestDTO>> violacoes = validator.validate(dto);

        assertTrue(violacoes.isEmpty());
    }

    @Test
    void deveGerarViolacaoQuandoTaxaJurosMensalForNegativa() {
        SimulacaoRequestDTO dto = criarRequest(
                new BigDecimal("1000.00"),
                new BigDecimal("-1.00"),
                Integer.valueOf(12)
        );

        Set<ConstraintViolation<SimulacaoRequestDTO>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(contemViolacao(
                violacoes,
                "taxaJurosMensal",
                "A taxa de juros mensal não pode ser negativa."
        ));
    }

    @Test
    void deveGerarViolacaoQuandoPrazoMesesForNulo() {
        SimulacaoRequestDTO dto = criarRequest(
                new BigDecimal("1000.00"),
                new BigDecimal("1.50"),
                null
        );

        Set<ConstraintViolation<SimulacaoRequestDTO>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(contemViolacao(
                violacoes,
                "prazoMeses",
                "O prazo em meses é obrigatório."
        ));
    }

    @Test
    void deveGerarViolacaoQuandoPrazoMesesForZero() {
        SimulacaoRequestDTO dto = criarRequest(
                new BigDecimal("1000.00"),
                new BigDecimal("1.50"),
                Integer.valueOf(0)
        );

        Set<ConstraintViolation<SimulacaoRequestDTO>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(contemViolacao(
                violacoes,
                "prazoMeses",
                "O prazo em meses deve ser maior que zero."
        ));
    }

    @Test
    void deveGerarViolacaoQuandoPrazoMesesForNegativo() {
        SimulacaoRequestDTO dto = criarRequest(
                new BigDecimal("1000.00"),
                new BigDecimal("1.50"),
                Integer.valueOf(-1)
        );

        Set<ConstraintViolation<SimulacaoRequestDTO>> violacoes = validator.validate(dto);

        assertFalse(violacoes.isEmpty());
        assertTrue(contemViolacao(
                violacoes,
                "prazoMeses",
                "O prazo em meses deve ser maior que zero."
        ));
    }

    private SimulacaoRequestDTO criarRequest(
            BigDecimal valorInicial,
            BigDecimal taxaJurosMensal,
            Integer prazoMeses
    ) {
        SimulacaoRequestDTO dto = new SimulacaoRequestDTO();
        dto.setValorInicial(valorInicial);
        dto.setTaxaJurosMensal(taxaJurosMensal);
        dto.setPrazoMeses(prazoMeses);
        return dto;
    }

    private boolean contemViolacao(
            Set<ConstraintViolation<SimulacaoRequestDTO>> violacoes,
            String campo,
            String mensagem
    ) {
        return violacoes.stream()
                .anyMatch(violacao ->
                        campo.equals(violacao.getPropertyPath().toString())
                                && mensagem.equals(violacao.getMessage())
                );
    }
}