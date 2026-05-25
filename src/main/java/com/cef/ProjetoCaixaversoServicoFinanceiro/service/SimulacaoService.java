package com.cef.ProjetoCaixaversoServicoFinanceiro.service;

import com.cef.ProjetoCaixaversoServicoFinanceiro.dto.MemoriaCalculoDTO;
import com.cef.ProjetoCaixaversoServicoFinanceiro.dto.SimulacaoRequestDTO;
import com.cef.ProjetoCaixaversoServicoFinanceiro.dto.SimulacaoResponseDTO;
import com.cef.ProjetoCaixaversoServicoFinanceiro.exception.RegraDeNegocioException;
import com.cef.ProjetoCaixaversoServicoFinanceiro.model.MemoriaCalculo;
import com.cef.ProjetoCaixaversoServicoFinanceiro.model.Simulacao;
import com.cef.ProjetoCaixaversoServicoFinanceiro.repository.SimulacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class SimulacaoService {

    private final SimulacaoRepository simulacaoRepository;

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final MathContext MC = new MathContext(20, ROUNDING_MODE);

    private static final int SCALE_MONETARIO = 2;
    private static final int SCALE_TAXA_PERCENTUAL = 6;
    private static final int SCALE_TAXA_DECIMAL = 10;

    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final BigDecimal ZERO_MONETARIO = BigDecimal.ZERO.setScale(SCALE_MONETARIO, ROUNDING_MODE);

    @Inject
    public SimulacaoService(SimulacaoRepository simulacaoRepository) {
        this.simulacaoRepository = simulacaoRepository;
    }

    @Transactional
    public SimulacaoResponseDTO simular(SimulacaoRequestDTO requestDTO) {
        validarRequest(requestDTO);

        BigDecimal valorInicial = normalizarValorMonetario(requestDTO.getValorInicial());
        BigDecimal taxaJurosMensal = normalizarTaxaPercentual(requestDTO.getTaxaJurosMensal());
        Integer prazoMeses = requestDTO.getPrazoMeses();

        List<MemoriaCalculo> memoriaCalculo = gerarMemoriaCalculo(
                valorInicial,
                taxaJurosMensal,
                prazoMeses
        );

        BigDecimal valorTotalFinal = calcularValorTotalFinal(memoriaCalculo);

        BigDecimal valorTotalJuros = normalizarValorMonetario(
                valorTotalFinal.subtract(valorInicial, MC)
        );

        Simulacao simulacao = new Simulacao();
        simulacao.setValorInicial(valorInicial);
        simulacao.setTaxaJurosMensal(taxaJurosMensal);
        simulacao.setPrazoMeses(prazoMeses);
        simulacao.setValorTotalFinal(valorTotalFinal);
        simulacao.setValorTotalJuros(valorTotalJuros);
        simulacao.setMemoriaCalculo(memoriaCalculo);

        simulacaoRepository.persistAndFlush(simulacao);

        return toResponseDTO(simulacao);
    }

    @Transactional
    public SimulacaoResponseDTO buscarPorId(Long id) {
        Simulacao simulacao = findSimulacaoById(id);
        return toResponseDTO(simulacao);
    }

    private Simulacao findSimulacaoById(Long id) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException("O ID da simulação deve ser maior que zero.");
        }

        return simulacaoRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Simulação não encontrada com o ID: " + id));
    }

    private void validarRequest(SimulacaoRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new RegraDeNegocioException("Os dados da simulação são obrigatórios.");
        }

        validarValorInicial(requestDTO.getValorInicial());
        validarTaxaJurosMensal(requestDTO.getTaxaJurosMensal());
        validarPrazoMeses(requestDTO.getPrazoMeses());
    }

    private void validarValorInicial(BigDecimal valorInicial) {
        if (valorInicial == null) {
            throw new RegraDeNegocioException("O valor inicial é obrigatório e deve ser numérico.");
        }

        if (valorInicial.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("O valor inicial deve ser um número maior que zero.");
        }
    }

    private void validarTaxaJurosMensal(BigDecimal taxaJurosMensal) {
        if (taxaJurosMensal == null) {
            throw new RegraDeNegocioException("A taxa de juros mensal é obrigatória e deve ser numérica.");
        }

        if (taxaJurosMensal.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraDeNegocioException("A taxa de juros mensal deve ser um número maior ou igual a zero.");
        }
    }

    private void validarPrazoMeses(Integer prazoMeses) {
        if (prazoMeses == null) {
            throw new RegraDeNegocioException("O prazo em meses é obrigatório e deve ser numérico.");
        }

        if (prazoMeses <= 0) {
            throw new RegraDeNegocioException("O prazo em meses deve ser um número inteiro maior que zero.");
        }
    }

    private List<MemoriaCalculo> gerarMemoriaCalculo(
            BigDecimal valorInicial,
            BigDecimal taxaJurosMensal,
            Integer prazoMeses
    ) {
        List<MemoriaCalculo> memoriaCalculo = new ArrayList<>();

        BigDecimal taxaDecimal = converterTaxaPercentualParaDecimal(taxaJurosMensal);
        BigDecimal saldoAtual = valorInicial;

        for (int mes = 1; mes <= prazoMeses; mes++) {
            BigDecimal saldoInicial = normalizarValorMonetario(saldoAtual);

            BigDecimal juro = calcularJuroDoMes(
                    saldoInicial,
                    taxaDecimal
            );

            BigDecimal saldoFinal = normalizarValorMonetario(
                    saldoInicial.add(juro, MC)
            );

            MemoriaCalculo memoria = new MemoriaCalculo();
            memoria.setMes(mes);
            memoria.setSaldoInicial(saldoInicial);
            memoria.setJuro(juro);
            memoria.setSaldoFinal(saldoFinal);

            memoriaCalculo.add(memoria);

            saldoAtual = saldoFinal;
        }

        return memoriaCalculo;
    }

    private BigDecimal calcularJuroDoMes(
            BigDecimal saldoInicial,
            BigDecimal taxaDecimal
    ) {
        return normalizarValorMonetario(
                saldoInicial.multiply(taxaDecimal, MC)
        );
    }

    private BigDecimal converterTaxaPercentualParaDecimal(BigDecimal taxaPercentual) {
        return taxaPercentual
                .divide(CEM, MC)
                .setScale(SCALE_TAXA_DECIMAL, ROUNDING_MODE);
    }

    private BigDecimal calcularValorTotalFinal(List<MemoriaCalculo> memoriaCalculo) {
        if (memoriaCalculo == null || memoriaCalculo.isEmpty()) {
            return ZERO_MONETARIO;
        }

        return normalizarValorMonetario(
                memoriaCalculo.get(memoriaCalculo.size() - 1).getSaldoFinal()
        );
    }

    private BigDecimal normalizarValorMonetario(BigDecimal valor) {
        return valor.setScale(SCALE_MONETARIO, ROUNDING_MODE);
    }

    private BigDecimal normalizarTaxaPercentual(BigDecimal taxaPercentual) {
        return taxaPercentual.setScale(SCALE_TAXA_PERCENTUAL, ROUNDING_MODE);
    }

    private SimulacaoResponseDTO toResponseDTO(Simulacao simulacao) {
        return SimulacaoResponseDTO.builder()
                .id(simulacao.getId())
                .valorInicial(normalizarValorMonetario(simulacao.getValorInicial()))
                .taxaJurosMensal(normalizarTaxaPercentual(simulacao.getTaxaJurosMensal()))
                .prazoMeses(simulacao.getPrazoMeses())
                .valorTotalFinal(normalizarValorMonetario(simulacao.getValorTotalFinal()))
                .valorTotalJuros(normalizarValorMonetario(simulacao.getValorTotalJuros()))
                .memoriaCalculo(toMemoriaCalculoDTOList(simulacao.getMemoriaCalculo()))
                .build();
    }

    private List<MemoriaCalculoDTO> toMemoriaCalculoDTOList(List<MemoriaCalculo> memoriaCalculo) {
        if (memoriaCalculo == null) {
            return new ArrayList<>();
        }

        return memoriaCalculo
                .stream()
                .sorted(Comparator.comparingInt(MemoriaCalculo::getMes))
                .map(this::toMemoriaCalculoDTO)
                .toList();
    }

    private MemoriaCalculoDTO toMemoriaCalculoDTO(MemoriaCalculo memoriaCalculo) {
        return MemoriaCalculoDTO.builder()
                .mes(memoriaCalculo.getMes())
                .saldoInicial(normalizarValorMonetario(memoriaCalculo.getSaldoInicial()))
                .juro(normalizarValorMonetario(memoriaCalculo.getJuro()))
                .saldoFinal(normalizarValorMonetario(memoriaCalculo.getSaldoFinal()))
                .build();
    }
}