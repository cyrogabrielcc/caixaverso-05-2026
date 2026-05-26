package com.cef.ProjetoCaixaversoServicoFinanceiro.resources;

import com.cef.ProjetoCaixaversoServicoFinanceiro.dto.MemoriaCalculoDTO;
import com.cef.ProjetoCaixaversoServicoFinanceiro.dto.SimulacaoRequestDTO;
import com.cef.ProjetoCaixaversoServicoFinanceiro.dto.SimulacaoResponseDTO;
import com.cef.ProjetoCaixaversoServicoFinanceiro.exception.RegraDeNegocioException;
import com.cef.ProjetoCaixaversoServicoFinanceiro.service.SimulacaoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimulacaoResourceTest {

    private SimulacaoService simulacaoService;
    private SimulacaoResource simulacaoResource;

    @BeforeEach
    void configurar() {
        simulacaoService = mock(SimulacaoService.class);
        simulacaoResource = new SimulacaoResource(simulacaoService);
    }

    @Test
    void deveSimularComSucessoRetornandoCreatedComLocationEBody() {
        SimulacaoRequestDTO requestDTO = criarRequestDTO();
        SimulacaoResponseDTO responseDTO = criarResponseDTO();

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getAbsolutePathBuilder())
                .thenReturn(UriBuilder.fromUri("http://localhost:8080/simulacoes"));

        when(simulacaoService.simular(requestDTO)).thenReturn(responseDTO);

        Response response = simulacaoResource.simular(requestDTO, uriInfo);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(responseDTO, response.getEntity());
        assertEquals(URI.create("http://localhost:8080/simulacoes/1"), response.getLocation());

        verify(simulacaoService, times(1)).simular(requestDTO);
        verifyNoMoreInteractions(simulacaoService);
    }

    @Test
    void deveBuscarPorIdComSucessoRetornandoOkComBody() {
        Long id = 1L;
        SimulacaoResponseDTO responseDTO = criarResponseDTO();

        when(simulacaoService.buscarPorId(id)).thenReturn(responseDTO);

        Response response = simulacaoResource.buscarPorId(id);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(responseDTO, response.getEntity());

        verify(simulacaoService, times(1)).buscarPorId(id);
        verifyNoMoreInteractions(simulacaoService);
    }

    @Test
    void devePropagarRegraDeNegocioExceptionAoSimular() {
        SimulacaoRequestDTO requestDTO = criarRequestDTO();

        UriInfo uriInfo = mock(UriInfo.class);

        when(simulacaoService.simular(requestDTO))
                .thenThrow(new RegraDeNegocioException("O valor inicial deve ser maior que zero."));

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> simulacaoResource.simular(requestDTO, uriInfo)
        );

        assertEquals("O valor inicial deve ser maior que zero.", exception.getMessage());

        verify(simulacaoService, times(1)).simular(requestDTO);
        verifyNoMoreInteractions(simulacaoService);
    }

    @Test
    void devePropagarEntityNotFoundExceptionAoBuscarPorId() {
        Long id = 99L;

        when(simulacaoService.buscarPorId(id))
                .thenThrow(new EntityNotFoundException("Simulação não encontrada com o ID: 99"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> simulacaoResource.buscarPorId(id)
        );

        assertEquals("Simulação não encontrada com o ID: 99", exception.getMessage());

        verify(simulacaoService, times(1)).buscarPorId(id);
        verifyNoMoreInteractions(simulacaoService);
    }

    @Test
    void deveConstruirLocationUsandoIdRetornadoPeloService() {
        SimulacaoRequestDTO requestDTO = criarRequestDTO();

        SimulacaoResponseDTO responseDTO = SimulacaoResponseDTO.builder()
                .id(25L)
                .valorInicial(new BigDecimal("1000.00"))
                .taxaJurosMensal(new BigDecimal("1.50"))
                .prazoMeses(12)
                .valorTotalFinal(new BigDecimal("1195.62"))
                .valorTotalJuros(new BigDecimal("195.62"))
                .memoriaCalculo(List.of())
                .build();

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getAbsolutePathBuilder())
                .thenReturn(UriBuilder.fromUri("http://localhost:8080/simulacoes"));

        when(simulacaoService.simular(requestDTO)).thenReturn(responseDTO);

        Response response = simulacaoResource.simular(requestDTO, uriInfo);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(URI.create("http://localhost:8080/simulacoes/25"), response.getLocation());
        assertEquals(responseDTO, response.getEntity());

        verify(simulacaoService, times(1)).simular(requestDTO);
        verifyNoMoreInteractions(simulacaoService);
    }

    private SimulacaoRequestDTO criarRequestDTO() {
        SimulacaoRequestDTO requestDTO = new SimulacaoRequestDTO();
        requestDTO.setValorInicial(new BigDecimal("1000.00"));
        requestDTO.setTaxaJurosMensal(new BigDecimal("1.50"));
        requestDTO.setPrazoMeses(12);
        return requestDTO;
    }

    private SimulacaoResponseDTO criarResponseDTO() {
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

        return SimulacaoResponseDTO.builder()
                .id(1L)
                .valorInicial(new BigDecimal("1000.00"))
                .taxaJurosMensal(new BigDecimal("1.50"))
                .prazoMeses(12)
                .valorTotalFinal(new BigDecimal("1195.62"))
                .valorTotalJuros(new BigDecimal("195.62"))
                .memoriaCalculo(List.of(memoriaMes1, memoriaMes2))
                .build();
    }
}