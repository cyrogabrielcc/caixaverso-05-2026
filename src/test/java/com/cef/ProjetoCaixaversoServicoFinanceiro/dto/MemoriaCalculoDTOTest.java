package com.cef.ProjetoCaixaversoServicoFinanceiro.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MemoriaCalculoDTOTest {

    @Test
    void deveCriarMemoriaCalculoDTOComBuilder() {
        BigDecimal saldoInicial = new BigDecimal("1000.00");
        BigDecimal juro = new BigDecimal("15.00");
        BigDecimal saldoFinal = new BigDecimal("1015.00");

        MemoriaCalculoDTO dto = MemoriaCalculoDTO.builder()
                .mes(1)
                .saldoInicial(saldoInicial)
                .juro(juro)
                .saldoFinal(saldoFinal)
                .build();

        assertEquals(1, dto.getMes());
        assertEquals(saldoInicial, dto.getSaldoInicial());
        assertEquals(juro, dto.getJuro());
        assertEquals(saldoFinal, dto.getSaldoFinal());
    }

    @Test
    void deveCriarMemoriaCalculoDTOComValoresNulosQuandoNaoInformadosNoBuilder() {
        MemoriaCalculoDTO dto = MemoriaCalculoDTO.builder().build();

        assertEquals(0, dto.getMes());
        assertNull(dto.getSaldoInicial());
        assertNull(dto.getJuro());
        assertNull(dto.getSaldoFinal());
    }

    @Test
    void devePermitirMesZeroPoisNaoHaValidacaoNoDTO() {
        MemoriaCalculoDTO dto = MemoriaCalculoDTO.builder()
                .mes(0)
                .saldoInicial(new BigDecimal("1000.00"))
                .juro(new BigDecimal("0.00"))
                .saldoFinal(new BigDecimal("1000.00"))
                .build();

        assertEquals(0, dto.getMes());
        assertEquals(new BigDecimal("1000.00"), dto.getSaldoInicial());
        assertEquals(new BigDecimal("0.00"), dto.getJuro());
        assertEquals(new BigDecimal("1000.00"), dto.getSaldoFinal());
    }
}