package com.cef.ProjetoCaixaversoServicoFinanceiro.repository;

import com.cef.ProjetoCaixaversoServicoFinanceiro.model.MemoriaCalculo;
import com.cef.ProjetoCaixaversoServicoFinanceiro.model.Simulacao;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class SimulacaoRepositoryTest {

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Inject
    EntityManager entityManager;

    @Test
    @TestTransaction
    void devePersistirEBuscarSimulacaoPorIdNoH2() {
        Simulacao simulacao = criarSimulacao();

        simulacaoRepository.persistAndFlush(simulacao);

        Long idGerado = simulacao.getId();

        assertNotNull(idGerado);
        assertTrue(idGerado > 0);

        entityManager.clear();

        Optional<Simulacao> resultado = simulacaoRepository.findByIdOptional(idGerado);

        assertTrue(resultado.isPresent());

        Simulacao simulacaoEncontrada = resultado.get();

        assertEquals(idGerado, simulacaoEncontrada.getId());
        assertBigDecimalEquals("1000.00", simulacaoEncontrada.getValorInicial());
        assertBigDecimalEquals("1.500000", simulacaoEncontrada.getTaxaJurosMensal());
        assertEquals(3, simulacaoEncontrada.getPrazoMeses());
        assertBigDecimalEquals("1045.68", simulacaoEncontrada.getValorTotalFinal());
        assertBigDecimalEquals("45.68", simulacaoEncontrada.getValorTotalJuros());
    }

    @Test
    @TestTransaction
    void devePersistirMemoriaCalculoEmCascataNoH2() {
        Simulacao simulacao = criarSimulacao();

        MemoriaCalculo mes2 = criarMemoriaCalculo(
                2,
                "1015.00",
                "15.23",
                "1030.23"
        );

        MemoriaCalculo mes1 = criarMemoriaCalculo(
                1,
                "1000.00",
                "15.00",
                "1015.00"
        );

        simulacao.adicionarMemoriaCalculo(mes2);
        simulacao.adicionarMemoriaCalculo(mes1);

        simulacaoRepository.persistAndFlush(simulacao);

        Long idGerado = simulacao.getId();

        entityManager.clear();

        Simulacao simulacaoEncontrada = simulacaoRepository.findById(idGerado);

        assertNotNull(simulacaoEncontrada);
        assertEquals(idGerado, simulacaoEncontrada.getId());

        assertNotNull(simulacaoEncontrada.getMemoriaCalculo());
        assertEquals(2, simulacaoEncontrada.getMemoriaCalculo().size());

        MemoriaCalculo primeiraMemoria = simulacaoEncontrada.getMemoriaCalculo().get(0);
        MemoriaCalculo segundaMemoria = simulacaoEncontrada.getMemoriaCalculo().get(1);

        assertEquals(1, primeiraMemoria.getMes());
        assertBigDecimalEquals("1000.00", primeiraMemoria.getSaldoInicial());
        assertBigDecimalEquals("15.00", primeiraMemoria.getJuro());
        assertBigDecimalEquals("1015.00", primeiraMemoria.getSaldoFinal());

        assertEquals(2, segundaMemoria.getMes());
        assertBigDecimalEquals("1015.00", segundaMemoria.getSaldoInicial());
        assertBigDecimalEquals("15.23", segundaMemoria.getJuro());
        assertBigDecimalEquals("1030.23", segundaMemoria.getSaldoFinal());

        assertNotNull(primeiraMemoria.getSimulacao());
        assertNotNull(segundaMemoria.getSimulacao());

        assertEquals(idGerado, primeiraMemoria.getSimulacao().getId());
        assertEquals(idGerado, segundaMemoria.getSimulacao().getId());
    }

    @Test
    @TestTransaction
    void deveRemoverMemoriaCalculoPorOrphanRemovalNoH2() {
        Simulacao simulacao = criarSimulacao();

        MemoriaCalculo memoria = criarMemoriaCalculo(
                1,
                "1000.00",
                "15.00",
                "1015.00"
        );

        simulacao.adicionarMemoriaCalculo(memoria);

        simulacaoRepository.persistAndFlush(simulacao);

        Long idGerado = simulacao.getId();

        entityManager.clear();

        Simulacao simulacaoEncontrada = simulacaoRepository.findById(idGerado);

        assertEquals(1, simulacaoEncontrada.getMemoriaCalculo().size());

        MemoriaCalculo memoriaParaRemover = simulacaoEncontrada.getMemoriaCalculo().get(0);

        simulacaoEncontrada.removerMemoriaCalculo(memoriaParaRemover);
        simulacaoRepository.persistAndFlush(simulacaoEncontrada);

        entityManager.clear();

        Simulacao simulacaoAtualizada = simulacaoRepository.findById(idGerado);

        assertNotNull(simulacaoAtualizada);
        assertTrue(simulacaoAtualizada.getMemoriaCalculo().isEmpty());
    }

    @Test
    @TestTransaction
    void deveRetornarOptionalVazioQuandoSimulacaoNaoExistirNoH2() {
        Optional<Simulacao> resultado = simulacaoRepository.findByIdOptional(999999999L);

        assertTrue(resultado.isEmpty());
    }

    private Simulacao criarSimulacao() {
        Simulacao simulacao = new Simulacao();
        simulacao.setValorInicial(new BigDecimal("1000.00"));
        simulacao.setTaxaJurosMensal(new BigDecimal("1.500000"));
        simulacao.setPrazoMeses(3);
        simulacao.setValorTotalFinal(new BigDecimal("1045.68"));
        simulacao.setValorTotalJuros(new BigDecimal("45.68"));

        return simulacao;
    }

    private MemoriaCalculo criarMemoriaCalculo(
            Integer mes,
            String saldoInicial,
            String juro,
            String saldoFinal
    ) {
        MemoriaCalculo memoriaCalculo = new MemoriaCalculo();
        memoriaCalculo.setMes(mes);
        memoriaCalculo.setSaldoInicial(new BigDecimal(saldoInicial));
        memoriaCalculo.setJuro(new BigDecimal(juro));
        memoriaCalculo.setSaldoFinal(new BigDecimal(saldoFinal));

        return memoriaCalculo;
    }

    private void assertBigDecimalEquals(String esperado, BigDecimal valorAtual) {
        assertNotNull(valorAtual);

        BigDecimal valorEsperado = new BigDecimal(esperado);

        assertEquals(
                0,
                valorEsperado.compareTo(valorAtual),
                () -> "Esperado: " + valorEsperado + ", recebido: " + valorAtual
        );
    }
}
