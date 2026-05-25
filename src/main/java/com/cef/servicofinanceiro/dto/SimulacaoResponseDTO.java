package com.cef.servicofinanceiro.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(
        name = "SimulacaoResponse",
        description = "Representa o resultado completo de uma simulação de juros compostos."
)
public class SimulacaoResponseDTO {

    @Schema(description = "Identificador único da simulação.", example = "1", readOnly = true)
    private Long id;

    @Schema(description = "Valor inicial informado para a simulação.", example = "1000.00")
    private BigDecimal valorInicial;

    @Schema(description = "Taxa de juros mensal em formato percentual.", example = "1.5")
    private BigDecimal taxaJurosMensal;

    @Schema(description = "Prazo da simulação em meses.", example = "12")
    private Integer prazoMeses;

    @Schema(description = "Valor total final após a aplicação dos juros compostos.", example = "1195.62")
    private BigDecimal valorTotalFinal;

    @Schema(description = "Valor total de juros acumulados no período.", example = "195.62")
    private BigDecimal valorTotalJuros;

    @Schema(description = "Memória de cálculo mensal da simulação.")
    private List<MemoriaCalculoDTO> memoriaCalculo;

    private SimulacaoResponseDTO(
            Long id,
            BigDecimal valorInicial,
            BigDecimal taxaJurosMensal,
            Integer prazoMeses,
            BigDecimal valorTotalFinal,
            BigDecimal valorTotalJuros,
            List<MemoriaCalculoDTO> memoriaCalculo
    ) {
        this.id = id;
        this.valorInicial = valorInicial;
        this.taxaJurosMensal = taxaJurosMensal;
        this.prazoMeses = prazoMeses;
        this.valorTotalFinal = valorTotalFinal;
        this.valorTotalJuros = valorTotalJuros;
        this.memoriaCalculo = memoriaCalculo;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public BigDecimal getTaxaJurosMensal() {
        return taxaJurosMensal;
    }

    public Integer getPrazoMeses() {
        return prazoMeses;
    }

    public BigDecimal getValorTotalFinal() {
        return valorTotalFinal;
    }

    public BigDecimal getValorTotalJuros() {
        return valorTotalJuros;
    }

    public List<MemoriaCalculoDTO> getMemoriaCalculo() {
        return memoriaCalculo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private BigDecimal valorInicial;
        private BigDecimal taxaJurosMensal;
        private Integer prazoMeses;
        private BigDecimal valorTotalFinal;
        private BigDecimal valorTotalJuros;
        private List<MemoriaCalculoDTO> memoriaCalculo;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder valorInicial(BigDecimal valorInicial) {
            this.valorInicial = valorInicial;
            return this;
        }

        public Builder taxaJurosMensal(BigDecimal taxaJurosMensal) {
            this.taxaJurosMensal = taxaJurosMensal;
            return this;
        }

        public Builder prazoMeses(Integer prazoMeses) {
            this.prazoMeses = prazoMeses;
            return this;
        }

        public Builder valorTotalFinal(BigDecimal valorTotalFinal) {
            this.valorTotalFinal = valorTotalFinal;
            return this;
        }

        public Builder valorTotalJuros(BigDecimal valorTotalJuros) {
            this.valorTotalJuros = valorTotalJuros;
            return this;
        }

        public Builder memoriaCalculo(List<MemoriaCalculoDTO> memoriaCalculo) {
            this.memoriaCalculo = memoriaCalculo;
            return this;
        }

        public SimulacaoResponseDTO build() {
            return new SimulacaoResponseDTO(
                    id,
                    valorInicial,
                    taxaJurosMensal,
                    prazoMeses,
                    valorTotalFinal,
                    valorTotalJuros,
                    memoriaCalculo
            );
        }
    }
}