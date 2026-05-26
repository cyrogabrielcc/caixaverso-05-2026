package com.cef.ProjetoCaixaversoServicoFinanceiro.service;

import com.cef.ProjetoCaixaversoServicoFinanceiro.dto.MemoriaCalculoDTO;
import com.cef.ProjetoCaixaversoServicoFinanceiro.dto.SimulacaoRequestDTO;
import com.cef.ProjetoCaixaversoServicoFinanceiro.dto.SimulacaoResponseDTO;
import com.cef.ProjetoCaixaversoServicoFinanceiro.exception.RegraDeNegocioException;
import com.cef.ProjetoCaixaversoServicoFinanceiro.model.MemoriaCalculo;
import com.cef.ProjetoCaixaversoServicoFinanceiro.model.Simulacao;
import com.cef.ProjetoCaixaversoServicoFinanceiro.repository.SimulacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimulacaoServiceTest {

    private SimulacaoRepository simulacaoRepository;
    private SimulacaoService simulacaoService;

    @BeforeEach
    void configurar() {
        simulacaoRepository = mock(SimulacaoRepository.class);
        simulacaoService = new SimulacaoService(simulacaoRepository);
    }

    @Test
    void deveSimularJurosCompostosComSucesso() {
        SimulacaoRequestDTO requestDTO = criarRequest(
                new BigDecimal("1000.00"),
                new BigDecimal("1.50"),
                3
        );

        SimulacaoResponseDTO responseDTO = simulacaoService.simular(requestDTO);

        assertNotNull(responseDTO);
        assertNull(responseDTO.getId());

        assertEquals(new BigDecimal("1000.00"), responseDTO.getValorInicial());
        assertEquals(new BigDecimal("1.500000"), responseDTO.getTaxaJurosMensal());
        assertEquals(3, responseDTO.getPrazoMeses());
        assertEquals(new BigDecimal("1045.68"), responseDTO.getValorTotalFinal());
        assertEquals(new BigDecimal("45.68"), responseDTO.getValorTotalJuros());

        assertNotNull(responseDTO.getMemoriaCalculo());
        assertEquals(3, responseDTO.getMemoriaCalculo().size());

        MemoriaCalculoDTO mes1 = responseDTO.getMemoriaCalculo().get(0);
        assertEquals(1, mes1.getMes());
        assertEquals(new BigDecimal("1000.00"), mes1.getSaldoInicial());
        assertEquals(new BigDecimal("15.00"), mes1.getJuro());
        assertEquals(new BigDecimal("1015.00"), mes1.getSaldoFinal());

        MemoriaCalculoDTO mes2 = responseDTO.getMemoriaCalculo().get(1);
        assertEquals(2, mes2.getMes());
        assertEquals(new BigDecimal("1015.00"), mes2.getSaldoInicial());
        assertEquals(new BigDecimal("15.23"), mes2.getJuro());
        assertEquals(new BigDecimal("1030.23"), mes2.getSaldoFinal());

        MemoriaCalculoDTO mes3 = responseDTO.getMemoriaCalculo().get(2);
        assertEquals(3, mes3.getMes());
        assertEquals(new BigDecimal("1030.23"), mes3.getSaldoInicial());
        assertEquals(new BigDecimal("15.45"), mes3.getJuro());
        assertEquals(new BigDecimal("1045.68"), mes3.getSaldoFinal());

        ArgumentCaptor<Simulacao> captor = ArgumentCaptor.forClass(Simulacao.class);
        verify(simulacaoRepository, times(1)).persistAndFlush(captor.capture());
        verifyNoMoreInteractions(simulacaoRepository);

        Simulacao simulacaoPersistida = captor.getValue();

        assertEquals(new BigDecimal("1000.00"), simulacaoPersistida.getValorInicial());
        assertEquals(new BigDecimal("1.500000"), simulacaoPersistida.getTaxaJurosMensal());
        assertEquals(3, simulacaoPersistida.getPrazoMeses());
        assertEquals(new BigDecimal("1045.68"), simulacaoPersistida.getValorTotalFinal());
        assertEquals(new BigDecimal("45.68"), simulacaoPersistida.getValorTotalJuros());
        assertNotNull(simulacaoPersistida.getMemoriaCalculo());
        assertEquals(3, simulacaoPersistida.getMemoriaCalculo().size());
    }

    @Test
    void deveSimularComTaxaZeroSemGerarJuros() {
        SimulacaoRequestDTO requestDTO = criarRequest(
                new BigDecimal("1000.00"),
                new BigDecimal("0.00"),
                2
        );

        SimulacaoResponseDTO responseDTO = simulacaoService.simular(requestDTO);

        assertNotNull(responseDTO);
        assertEquals(new BigDecimal("1000.00"), responseDTO.getValorInicial());
        assertEquals(new BigDecimal("0.000000"), responseDTO.getTaxaJurosMensal());
        assertEquals(2, responseDTO.getPrazoMeses());
        assertEquals(new BigDecimal("1000.00"), responseDTO.getValorTotalFinal());
        assertEquals(new BigDecimal("0.00"), responseDTO.getValorTotalJuros());

        assertEquals(2, responseDTO.getMemoriaCalculo().size());

        MemoriaCalculoDTO mes1 = responseDTO.getMemoriaCalculo().get(0);
        assertEquals(1, mes1.getMes());
        assertEquals(new BigDecimal("1000.00"), mes1.getSaldoInicial());
        assertEquals(new BigDecimal("0.00"), mes1.getJuro());
        assertEquals(new BigDecimal("1000.00"), mes1.getSaldoFinal());

        MemoriaCalculoDTO mes2 = responseDTO.getMemoriaCalculo().get(1);
        assertEquals(2, mes2.getMes());
        assertEquals(new BigDecimal("1000.00"), mes2.getSaldoInicial());
        assertEquals(new BigDecimal("0.00"), mes2.getJuro());
        assertEquals(new BigDecimal("1000.00"), mes2.getSaldoFinal());

        verify(simulacaoRepository, times(1)).persistAndFlush(any(Simulacao.class));
        verifyNoMoreInteractions(simulacaoRepository);
    }

    @Test
    void deveArredondarValorInicialETaxaAoSimular() {
        SimulacaoRequestDTO requestDTO = criarRequest(
                new BigDecimal("1000.999"),
                new BigDecimal("1.1234567"),
                1
        );

        SimulacaoResponseDTO responseDTO = simulacaoService.simular(requestDTO);

        assertEquals(new BigDecimal("1001.00"), responseDTO.getValorInicial());
        assertEquals(new BigDecimal("1.123457"), responseDTO.getTaxaJurosMensal());

        assertEquals(1, responseDTO.getMemoriaCalculo().size());
        assertEquals(new BigDecimal("1001.00"), responseDTO.getMemoriaCalculo().get(0).getSaldoInicial());

        verify(simulacaoRepository, times(1)).persistAndFlush(any(Simulacao.class));
        verifyNoMoreInteractions(simulacaoRepository);
    }

    @Test
    void deveLancarExceptionQuandoRequestForNulo() {
        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> simulacaoService.simular(null)
        );

        assertEquals("Os dados da simulação são obrigatórios.", exception.getMessage());

        verifyNoInteractions(simulacaoRepository);
    }

    @Test
    void deveLancarExceptionQuandoValorInicialForNulo() {
        SimulacaoRequestDTO requestDTO = criarRequest(
                null,
                new BigDecimal("1.50"),
                12
        );

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> simulacaoService.simular(requestDTO)
        );

        assertEquals("O valor inicial é obrigatório e deve ser numérico.", exception.getMessage());

        verifyNoInteractions(simulacaoRepository);
    }

    @Test
    void deveLancarExceptionQuandoValorInicialForZero() {
        SimulacaoRequestDTO requestDTO = criarRequest(
                new BigDecimal("0.00"),
                new BigDecimal("1.50"),
                12
        );

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> simulacaoService.simular(requestDTO)
        );

        assertEquals("O valor inicial deve ser um número maior que zero.", exception.getMessage());

        verifyNoInteractions(simulacaoRepository);
    }

    @Test
    void deveLancarExceptionQuandoValorInicialForNegativo() {
        SimulacaoRequestDTO requestDTO = criarRequest(
                new BigDecimal("-1000.00"),
                new BigDecimal("1.50"),
                12
        );

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> simulacaoService.simular(requestDTO)
        );

        assertEquals("O valor inicial deve ser um número maior que zero.", exception.getMessage());

        verifyNoInteractions(simulacaoRepository);
    }

    @Test
    void deveLancarExceptionQuandoTaxaJurosMensalForNula() {
        SimulacaoRequestDTO requestDTO = criarRequest(
                new BigDecimal("1000.00"),
                null,
                12
        );

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> simulacaoService.simular(requestDTO)
        );

        assertEquals("A taxa de juros mensal é obrigatória e deve ser numérica.", exception.getMessage());

        verifyNoInteractions(simulacaoRepository);
    }

    @Test
    void deveLancarExceptionQuandoTaxaJurosMensalForNegativa() {
        SimulacaoRequestDTO requestDTO = criarRequest(
                new BigDecimal("1000.00"),
                new BigDecimal("-1.00"),
                12
        );

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> simulacaoService.simular(requestDTO)
        );

        assertEquals("A taxa de juros mensal deve ser um número maior ou igual a zero.", exception.getMessage());

        verifyNoInteractions(simulacaoRepository);
    }

    @Test
    void deveLancarExceptionQuandoPrazoMesesForNulo() {
        SimulacaoRequestDTO requestDTO = criarRequest(
                new BigDecimal("1000.00"),
                new BigDecimal("1.50"),
                null
        );

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> simulacaoService.simular(requestDTO)
        );

        assertEquals("O prazo em meses é obrigatório e deve ser numérico.", exception.getMessage());

        verifyNoInteractions(simulacaoRepository);
    }

    @Test
    void deveLancarExceptionQuandoPrazoMesesForZero() {
        SimulacaoRequestDTO requestDTO = criarRequest(
                new BigDecimal("1000.00"),
                new BigDecimal("1.50"),
                0
        );

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> simulacaoService.simular(requestDTO)
        );

        assertEquals("O prazo em meses deve ser um número inteiro maior que zero.", exception.getMessage());

        verifyNoInteractions(simulacaoRepository);
    }

    @Test
    void deveLancarExceptionQuandoPrazoMesesForNegativo() {
        SimulacaoRequestDTO requestDTO = criarRequest(
                new BigDecimal("1000.00"),
                new BigDecimal("1.50"),
                -1
        );

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> simulacaoService.simular(requestDTO)
        );

        assertEquals("O prazo em meses deve ser um número inteiro maior que zero.", exception.getMessage());

        verifyNoInteractions(simulacaoRepository);
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        Long id = 1L;
        Simulacao simulacao = criarSimulacaoPersistida(id);

        when(simulacaoRepository.findByIdOptional(id)).thenReturn(Optional.of(simulacao));

        SimulacaoResponseDTO responseDTO = simulacaoService.buscarPorId(id);

        assertNotNull(responseDTO);
        assertEquals(id, responseDTO.getId());
        assertEquals(new BigDecimal("1000.00"), responseDTO.getValorInicial());
        assertEquals(new BigDecimal("1.500000"), responseDTO.getTaxaJurosMensal());
        assertEquals(2, responseDTO.getPrazoMeses());
        assertEquals(new BigDecimal("1030.23"), responseDTO.getValorTotalFinal());
        assertEquals(new BigDecimal("30.23"), responseDTO.getValorTotalJuros());

        assertEquals(2, responseDTO.getMemoriaCalculo().size());
        assertEquals(1, responseDTO.getMemoriaCalculo().get(0).getMes());
        assertEquals(2, responseDTO.getMemoriaCalculo().get(1).getMes());

        verify(simulacaoRepository, times(1)).findByIdOptional(id);
        verifyNoMoreInteractions(simulacaoRepository);
    }

    @Test
    void deveOrdenarMemoriaCalculoAoBuscarPorId() {
        Long id = 1L;
        Simulacao simulacao = criarSimulacaoPersistida(id);

        when(simulacaoRepository.findByIdOptional(id)).thenReturn(Optional.of(simulacao));

        SimulacaoResponseDTO responseDTO = simulacaoService.buscarPorId(id);

        assertEquals(2, responseDTO.getMemoriaCalculo().size());
        assertEquals(1, responseDTO.getMemoriaCalculo().get(0).getMes());
        assertEquals(2, responseDTO.getMemoriaCalculo().get(1).getMes());

        verify(simulacaoRepository, times(1)).findByIdOptional(id);
        verifyNoMoreInteractions(simulacaoRepository);
    }

    @Test
    void deveRetornarListaVaziaQuandoMemoriaCalculoForNulaAoBuscarPorId() {
        Long id = 1L;

        Simulacao simulacao = new Simulacao();
        definirIdPorReflexao(simulacao, id);
        simulacao.setValorInicial(new BigDecimal("1000.00"));
        simulacao.setTaxaJurosMensal(new BigDecimal("1.50"));
        simulacao.setPrazoMeses(2);
        simulacao.setValorTotalFinal(new BigDecimal("1030.23"));
        simulacao.setValorTotalJuros(new BigDecimal("30.23"));
        simulacao.setMemoriaCalculo(null);

        when(simulacaoRepository.findByIdOptional(id)).thenReturn(Optional.of(simulacao));

        SimulacaoResponseDTO responseDTO = simulacaoService.buscarPorId(id);

        assertNotNull(responseDTO.getMemoriaCalculo());
        assertTrue(responseDTO.getMemoriaCalculo().isEmpty());

        verify(simulacaoRepository, times(1)).findByIdOptional(id);
        verifyNoMoreInteractions(simulacaoRepository);
    }

    @Test
    void deveLancarExceptionQuandoIdForNuloAoBuscarPorId() {
        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> simulacaoService.buscarPorId(null)
        );

        assertEquals("O ID da simulação deve ser maior que zero.", exception.getMessage());

        verifyNoInteractions(simulacaoRepository);
    }

    @Test
    void deveLancarExceptionQuandoIdForZeroAoBuscarPorId() {
        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> simulacaoService.buscarPorId(0L)
        );

        assertEquals("O ID da simulação deve ser maior que zero.", exception.getMessage());

        verifyNoInteractions(simulacaoRepository);
    }

    @Test
    void deveLancarExceptionQuandoIdForNegativoAoBuscarPorId() {
        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class,
                () -> simulacaoService.buscarPorId(-1L)
        );

        assertEquals("O ID da simulação deve ser maior que zero.", exception.getMessage());

        verifyNoInteractions(simulacaoRepository);
    }

    @Test
    void deveRetornarZeroQuandoCalcularValorTotalFinalReceberMemoriaNula() throws Exception {
        Method metodo = SimulacaoService.class.getDeclaredMethod(
                "calcularValorTotalFinal",
                List.class
        );
        metodo.setAccessible(true);

        BigDecimal resultado = (BigDecimal) metodo.invoke(
                simulacaoService,
                new Object[]{null}
        );

        assertEquals(new BigDecimal("0.00"), resultado);
    }

    @Test
    void deveRetornarZeroQuandoCalcularValorTotalFinalReceberMemoriaVazia() throws Exception {
        Method metodo = SimulacaoService.class.getDeclaredMethod(
                "calcularValorTotalFinal",
                List.class
        );
        metodo.setAccessible(true);

        BigDecimal resultado = (BigDecimal) metodo.invoke(
                simulacaoService,
                List.of()
        );

        assertEquals(new BigDecimal("0.00"), resultado);
    }

    @Test
    void deveLancarEntityNotFoundExceptionQuandoSimulacaoNaoForEncontrada() {
        Long id = 99L;

        when(simulacaoRepository.findByIdOptional(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> simulacaoService.buscarPorId(id)
        );

        assertEquals("Simulação não encontrada com o ID: 99", exception.getMessage());

        verify(simulacaoRepository, times(1)).findByIdOptional(id);
        verifyNoMoreInteractions(simulacaoRepository);
    }

    private SimulacaoRequestDTO criarRequest(
            BigDecimal valorInicial,
            BigDecimal taxaJurosMensal,
            Integer prazoMeses
    ) {
        SimulacaoRequestDTO requestDTO = new SimulacaoRequestDTO();
        requestDTO.setValorInicial(valorInicial);
        requestDTO.setTaxaJurosMensal(taxaJurosMensal);
        requestDTO.setPrazoMeses(prazoMeses);
        return requestDTO;
    }

    private Simulacao criarSimulacaoPersistida(Long id) {
        MemoriaCalculo mes2 = new MemoriaCalculo();
        mes2.setMes(Integer.valueOf(2));
        mes2.setSaldoInicial(new BigDecimal("1015.00"));
        mes2.setJuro(new BigDecimal("15.23"));
        mes2.setSaldoFinal(new BigDecimal("1030.23"));

        MemoriaCalculo mes1 = new MemoriaCalculo();
        mes1.setMes(Integer.valueOf(1));
        mes1.setSaldoInicial(new BigDecimal("1000.00"));
        mes1.setJuro(new BigDecimal("15.00"));
        mes1.setSaldoFinal(new BigDecimal("1015.00"));

        Simulacao simulacao = new Simulacao();
        definirIdPorReflexao(simulacao, id);
        simulacao.setValorInicial(new BigDecimal("1000.00"));
        simulacao.setTaxaJurosMensal(new BigDecimal("1.50"));
        simulacao.setPrazoMeses(2);
        simulacao.setValorTotalFinal(new BigDecimal("1030.23"));
        simulacao.setValorTotalJuros(new BigDecimal("30.23"));
        simulacao.setMemoriaCalculo(List.of(mes2, mes1));

        return simulacao;
    }

    private void definirIdPorReflexao(Simulacao simulacao, Long id) {
        try {
            Field campoId = Simulacao.class.getDeclaredField("id");
            campoId.setAccessible(true);
            campoId.set(simulacao, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Não foi possível definir o ID da simulação no teste.", e);
        }
    }
}