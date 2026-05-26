package com.cef.ProjetoCaixaversoServicoFinanceiro.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulacaoTest {

    @Test
    void deveCriarSimulacaoComConstrutorVazio() {
        Simulacao simulacao = new Simulacao();

        assertNotNull(simulacao);
        assertNull(simulacao.getId());
        assertNull(simulacao.getValorInicial());
        assertNull(simulacao.getTaxaJurosMensal());
        assertNull(simulacao.getPrazoMeses());
        assertNull(simulacao.getValorTotalFinal());
        assertNull(simulacao.getValorTotalJuros());

        assertNotNull(simulacao.getMemoriaCalculo());
        assertTrue(simulacao.getMemoriaCalculo().isEmpty());
    }

    @Test
    void devePreencherEObterTodosOsCampos() {
        Simulacao simulacao = new Simulacao();

        BigDecimal valorInicial = new BigDecimal("1000.00");
        BigDecimal taxaJurosMensal = new BigDecimal("1.50");
        Integer prazoMeses = 12;
        BigDecimal valorTotalFinal = new BigDecimal("1195.62");
        BigDecimal valorTotalJuros = new BigDecimal("195.62");

        MemoriaCalculo memoria = new MemoriaCalculo();
        memoria.setMes(1);
        memoria.setSaldoInicial(new BigDecimal("1000.00"));
        memoria.setJuro(new BigDecimal("15.00"));
        memoria.setSaldoFinal(new BigDecimal("1015.00"));

        List<MemoriaCalculo> memoriaCalculo = new ArrayList<>();
        memoriaCalculo.add(memoria);

        simulacao.setValorInicial(valorInicial);
        simulacao.setTaxaJurosMensal(taxaJurosMensal);
        simulacao.setPrazoMeses(prazoMeses);
        simulacao.setValorTotalFinal(valorTotalFinal);
        simulacao.setValorTotalJuros(valorTotalJuros);
        simulacao.setMemoriaCalculo(memoriaCalculo);

        assertEquals(valorInicial, simulacao.getValorInicial());
        assertEquals(taxaJurosMensal, simulacao.getTaxaJurosMensal());
        assertEquals(prazoMeses, simulacao.getPrazoMeses());
        assertEquals(valorTotalFinal, simulacao.getValorTotalFinal());
        assertEquals(valorTotalJuros, simulacao.getValorTotalJuros());
        assertEquals(memoriaCalculo, simulacao.getMemoriaCalculo());
        assertEquals(1, simulacao.getMemoriaCalculo().size());
    }

    @Test
    void devePermitirDefinirIdPorReflexao() throws Exception {
        Simulacao simulacao = new Simulacao();

        Field campoId = Simulacao.class.getDeclaredField("id");
        campoId.setAccessible(true);
        campoId.set(simulacao, 10L);

        assertEquals(10L, simulacao.getId());
    }

    @Test
    void deveConverterMemoriaCalculoNulaParaListaVazia() {
        Simulacao simulacao = new Simulacao();

        simulacao.setMemoriaCalculo(null);

        assertNotNull(simulacao.getMemoriaCalculo());
        assertTrue(simulacao.getMemoriaCalculo().isEmpty());
    }

    @Test
    void devePermitirMemoriaCalculoVazia() {
        Simulacao simulacao = new Simulacao();

        simulacao.setMemoriaCalculo(List.of());

        assertNotNull(simulacao.getMemoriaCalculo());
        assertTrue(simulacao.getMemoriaCalculo().isEmpty());
    }

    @Test
    void devePermitirMemoriaCalculoComListaMutavel() {
        Simulacao simulacao = new Simulacao();

        List<MemoriaCalculo> memoriaCalculo = new ArrayList<>();

        MemoriaCalculo memoria = new MemoriaCalculo();
        memoria.setMes(1);
        memoria.setSaldoInicial(new BigDecimal("1000.00"));
        memoria.setJuro(new BigDecimal("10.00"));
        memoria.setSaldoFinal(new BigDecimal("1010.00"));

        memoriaCalculo.add(memoria);

        simulacao.setMemoriaCalculo(memoriaCalculo);

        assertNotNull(simulacao.getMemoriaCalculo());
        assertFalse(simulacao.getMemoriaCalculo().isEmpty());
        assertEquals(1, simulacao.getMemoriaCalculo().size());
        assertEquals(memoria, simulacao.getMemoriaCalculo().get(0));
    }

    @Test
    void deveExecutarEqualsHashCodeEToStringSemErro() {
        Simulacao simulacao = new Simulacao();

        simulacao.setValorInicial(new BigDecimal("1000.00"));
        simulacao.setTaxaJurosMensal(new BigDecimal("1.50"));
        simulacao.setPrazoMeses(12);
        simulacao.setValorTotalFinal(new BigDecimal("1195.62"));
        simulacao.setValorTotalJuros(new BigDecimal("195.62"));

        assertEquals(simulacao, simulacao);
        assertNotEquals(null, simulacao);
        assertNotEquals("texto", simulacao);
        assertNotNull(simulacao.hashCode());
        assertNotNull(simulacao.toString());
    }
}