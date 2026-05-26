package com.cef.ProjetoCaixaversoServicoFinanceiro.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MemoriaCalculoTest {

    @Test
    void deveCriarMemoriaCalculoComConstrutorVazio() {
        MemoriaCalculo memoriaCalculo = new MemoriaCalculo();

        assertNotNull(memoriaCalculo);
        assertNull(memoriaCalculo.getId());
        assertNull(memoriaCalculo.getMes());
        assertNull(memoriaCalculo.getSaldoInicial());
        assertNull(memoriaCalculo.getJuro());
        assertNull(memoriaCalculo.getSaldoFinal());
    }

    @Test
    void devePreencherEObterTodosOsCampos() {
        MemoriaCalculo memoriaCalculo = new MemoriaCalculo();

        Integer mes = 1;
        BigDecimal saldoInicial = new BigDecimal("1000.00");
        BigDecimal juro = new BigDecimal("15.00");
        BigDecimal saldoFinal = new BigDecimal("1015.00");

        memoriaCalculo.setMes(mes);
        memoriaCalculo.setSaldoInicial(saldoInicial);
        memoriaCalculo.setJuro(juro);
        memoriaCalculo.setSaldoFinal(saldoFinal);

        assertEquals(mes, memoriaCalculo.getMes());
        assertEquals(saldoInicial, memoriaCalculo.getSaldoInicial());
        assertEquals(juro, memoriaCalculo.getJuro());
        assertEquals(saldoFinal, memoriaCalculo.getSaldoFinal());
    }

    @Test
    void devePermitirDefinirIdPorReflexao() throws Exception {
        MemoriaCalculo memoriaCalculo = new MemoriaCalculo();

        Field campoId = MemoriaCalculo.class.getDeclaredField("id");
        campoId.setAccessible(true);
        campoId.set(memoriaCalculo, 20L);

        assertEquals(20L, memoriaCalculo.getId());
    }

    @Test
    void devePermitirCamposNulos() {
        MemoriaCalculo memoriaCalculo = new MemoriaCalculo();

        memoriaCalculo.setMes(null);
        memoriaCalculo.setSaldoInicial(null);
        memoriaCalculo.setJuro(null);
        memoriaCalculo.setSaldoFinal(null);

        assertNull(memoriaCalculo.getMes());
        assertNull(memoriaCalculo.getSaldoInicial());
        assertNull(memoriaCalculo.getJuro());
        assertNull(memoriaCalculo.getSaldoFinal());
    }

    @Test
    void deveExecutarEqualsHashCodeEToStringSemErro() {
        MemoriaCalculo memoriaCalculo = new MemoriaCalculo();

        memoriaCalculo.setMes(1);
        memoriaCalculo.setSaldoInicial(new BigDecimal("1000.00"));
        memoriaCalculo.setJuro(new BigDecimal("15.00"));
        memoriaCalculo.setSaldoFinal(new BigDecimal("1015.00"));

        assertEquals(memoriaCalculo, memoriaCalculo);
        assertNotEquals(null, memoriaCalculo);
        assertNotEquals("texto", memoriaCalculo);
        assertNotNull(memoriaCalculo.hashCode());
        assertNotNull(memoriaCalculo.toString());
    }
}