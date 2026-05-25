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
import java.util.Optional;

@ApplicationScoped
public class SimulacaoService {

    private final SimulacaoRepository simulacaoRepository;

    private static final MathContext MC = new MathContext(12, RoundingMode.HALF_UP);
    private static final int SCALE_MONETARIO = 2;
    private static final int SCALE_TAXA = 6;

    @Inject
    public SimulacaoService(SimulacaoRepository simulacaoRepository) {
        this.simulacaoRepository = simulacaoRepository;
    }

    @Transactional
    public SimulacaoResponseDTO simular(SimulacaoRequestDTO requestDTO) {
        validarRequest(requestDTO);

        BigDecimal valorInicial = requestDTO.getValorInicial().setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);
        BigDecimal taxaJurosMensal = requestDTO.getTaxaJurosMensal().setScale(SCALE_TAXA, RoundingMode.HALF_UP);
        Integer prazoMeses = requestDTO.getPrazoMeses();

        Simulacao simulacao = new Simulacao();
        simulacao.setValorInicial(valorInicial);
        simulacao.setTaxaJurosMensal(taxaJurosMensal);
        simulacao.setPrazoMeses(prazoMeses);

        List<MemoriaCalculo> memoriaCalculo = gerarMemoriaCalculo(
                valorInicial,
                taxaJurosMensal,
                prazoMeses
        );

        simulacao.setMemoriaCalculo(memoriaCalculo);

        BigDecimal valorTotalFinal = calcularValorTotalFinal(memoriaCalculo);
        BigDecimal valorTotalJuros = valorTotalFinal
                .subtract(valorInicial)
                .setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);

        simulacao.setValorTotalFinal(valorTotalFinal);
        simulacao.setValorTotalJuros(valorTotalJuros);

        simulacaoRepository.persistAndFlush(simulacao);

        return toResponseDTO(simulacao);
    }

    @Transactional
    public SimulacaoResponseDTO buscarPorId(Long id) {
        Simulacao simulacao = findSimulacaoById(id);
        return toResponseDTO(simulacao);
    }

    private Simulacao findSimulacaoById(Long id) {
        Optional<Simulacao> optionalSimulacao = simulacaoRepository.findByIdOptional(id);

        if (optionalSimulacao.isPresent()) {
            return optionalSimulacao.get();
        }

        throw new EntityNotFoundException("Simulação não encontrada com o ID: " + id);
    }

    private void validarRequest(SimulacaoRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new RegraDeNegocioException("Os dados da simulação são obrigatórios.");
        }

        if (requestDTO.getValorInicial() == null || requestDTO.getValorInicial().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("O valor inicial deve ser maior que zero.");
        }

        if (requestDTO.getTaxaJurosMensal() == null || requestDTO.getTaxaJurosMensal().compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraDeNegocioException("A taxa de juros mensal não pode ser negativa.");
        }

        if (requestDTO.getPrazoMeses() == null || requestDTO.getPrazoMeses() <= 0) {
            throw new RegraDeNegocioException("O prazo em meses deve ser maior que zero.");
        }
    }

    private List<MemoriaCalculo> gerarMemoriaCalculo(
            BigDecimal valorInicial,
            BigDecimal taxaJurosMensal,
            Integer prazoMeses
    ) {
        List<MemoriaCalculo> memoriaCalculo = new ArrayList<>();

        BigDecimal taxaDecimal = taxaJurosMensal
                .divide(BigDecimal.valueOf(100), MC);

        BigDecimal saldoAtual = valorInicial;

        for (int mes = 1; mes <= prazoMeses; mes++) {
            BigDecimal saldoInicial = saldoAtual.setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);

            BigDecimal juro = saldoInicial
                    .multiply(taxaDecimal, MC)
                    .setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);

            BigDecimal saldoFinal = saldoInicial
                    .add(juro)
                    .setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);

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

    private BigDecimal calcularValorTotalFinal(List<MemoriaCalculo> memoriaCalculo) {
        if (memoriaCalculo == null || memoriaCalculo.isEmpty()) {
            return BigDecimal.ZERO.setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);
        }

        return memoriaCalculo
                .getLast()
                .getSaldoFinal()
                .setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);
    }

    private SimulacaoResponseDTO toResponseDTO(Simulacao simulacao) {
        return SimulacaoResponseDTO.builder()
                .id(simulacao.getId())
                .valorInicial(simulacao.getValorInicial())
                .taxaJurosMensal(simulacao.getTaxaJurosMensal())
                .prazoMeses(simulacao.getPrazoMeses())
                .valorTotalFinal(simulacao.getValorTotalFinal())
                .valorTotalJuros(simulacao.getValorTotalJuros())
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
                .saldoInicial(memoriaCalculo.getSaldoInicial())
                .juro(memoriaCalculo.getJuro())
                .saldoFinal(memoriaCalculo.getSaldoFinal())
                .build();
    }
}