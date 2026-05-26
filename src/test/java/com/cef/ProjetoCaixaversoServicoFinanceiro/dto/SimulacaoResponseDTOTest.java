package com.cef.ProjetoCaixaversoServicoFinanceiro.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulacaoResponseDTOTest {

    @Test
    void deveCriarSimulacaoResponseDTOComBuilder() {
        MemoriaCalculoDTO memoriaMes1 = MemoriaCalculoDTO.builder()
                .mes(1)
                .saldoInicial(new BigDecimal("1000.00"))
                .juro(new BigDecimal("15.00"))
                .saldoFinal(new BigDecimal("1015.00"))
                .build();

        MemoriaCalculoDTO memoriaMes2 = MemoriaCalculoDTO.builder()
                .mes(2)
                .saldoInicial(new BigDecimal("1015.00"))
                .juro(new BigDecimal("15.23"))
                .saldoFinal(new BigDecimal("1030.23"))
                .build();

        List<MemoriaCalculoDTO> memoriaCalculo = List.of(memoriaMes1, memoriaMes2);

        SimulacaoResponseDTO response = SimulacaoResponseDTO.builder()
                .id(Long.valueOf(1L))
                .valorInicial(new BigDecimal("1000.00"))
                .taxaJurosMensal(new BigDecimal("1.50"))
                .prazoMeses(Integer.valueOf(2))
                .valorTotalFinal(new BigDecimal("1030.23"))
                .valorTotalJuros(new BigDecimal("30.23"))
                .memoriaCalculo(memoriaCalculo)
                .build();

        assertEquals(1L, response.getId());
        assertEquals(new BigDecimal("1000.00"), response.getValorInicial());
        assertEquals(new BigDecimal("1.50"), response.getTaxaJurosMensal());
        assertEquals(2, response.getPrazoMeses());
        assertEquals(new BigDecimal("1030.23"), response.getValorTotalFinal());
        assertEquals(new BigDecimal("30.23"), response.getValorTotalJuros());

        assertNotNull(response.getMemoriaCalculo());
        assertEquals(2, response.getMemoriaCalculo().size());
        assertSame(memoriaCalculo, response.getMemoriaCalculo());

        assertEquals(1, response.getMemoriaCalculo().get(0).getMes());
        assertEquals(new BigDecimal("1000.00"), response.getMemoriaCalculo().get(0).getSaldoInicial());
        assertEquals(new BigDecimal("15.00"), response.getMemoriaCalculo().get(0).getJuro());
        assertEquals(new BigDecimal("1015.00"), response.getMemoriaCalculo().get(0).getSaldoFinal());

        assertEquals(2, response.getMemoriaCalculo().get(1).getMes());
        assertEquals(new BigDecimal("1015.00"), response.getMemoriaCalculo().get(1).getSaldoInicial());
        assertEquals(new BigDecimal("15.23"), response.getMemoriaCalculo().get(1).getJuro());
        assertEquals(new BigDecimal("1030.23"), response.getMemoriaCalculo().get(1).getSaldoFinal());
    }

    @Test
    void deveCriarSimulacaoResponseDTOComValoresNulosQuandoNaoInformadosNoBuilder() {
        SimulacaoResponseDTO response = SimulacaoResponseDTO.builder().build();

        assertNull(response.getId());
        assertNull(response.getValorInicial());
        assertNull(response.getTaxaJurosMensal());
        assertNull(response.getPrazoMeses());
        assertNull(response.getValorTotalFinal());
        assertNull(response.getValorTotalJuros());
        assertNull(response.getMemoriaCalculo());
    }

    @Test
    void devePermitirMemoriaCalculoVazia() {
        SimulacaoResponseDTO response = SimulacaoResponseDTO.builder()
                .id(Long.valueOf(1L))
                .valorInicial(new BigDecimal("1000.00"))
                .taxaJurosMensal(new BigDecimal("0.00"))
                .prazoMeses(Integer.valueOf(0))
                .valorTotalFinal(new BigDecimal("1000.00"))
                .valorTotalJuros(new BigDecimal("0.00"))
                .memoriaCalculo(List.of())
                .build();

        assertEquals(1L, response.getId());
        assertEquals(new BigDecimal("1000.00"), response.getValorInicial());
        assertEquals(new BigDecimal("0.00"), response.getTaxaJurosMensal());
        assertEquals(0, response.getPrazoMeses());
        assertEquals(new BigDecimal("1000.00"), response.getValorTotalFinal());
        assertEquals(new BigDecimal("0.00"), response.getValorTotalJuros());
        assertNotNull(response.getMemoriaCalculo());
        assertTrue(response.getMemoriaCalculo().isEmpty());
    }
}