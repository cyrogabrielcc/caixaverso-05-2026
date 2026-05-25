package com.cef.servicofinanceiro.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        name = "MemoriaCalculo",
        description = "Representa a evolução mensal da simulação de juros compostos."
)
public class MemoriaCalculoDTO {

    @Schema(description = "Mês correspondente ao cálculo.", example = "1")
    private int mes;

    @Schema(description = "Saldo no início do mês.", example = "1000.00")
    private BigDecimal saldoInicial;

    @Schema(description = "Valor do juro aplicado no mês.", example = "15.00")
    private BigDecimal juro;

    @Schema(description = "Saldo ao final do mês após a incidência dos juros.", example = "1015.00")
    private BigDecimal saldoFinal;

    private MemoriaCalculoDTO(
            int mes,
            BigDecimal saldoInicial,
            BigDecimal juro,
            BigDecimal saldoFinal
    ) {
        this.mes = mes;
        this.saldoInicial = saldoInicial;
        this.juro = juro;
        this.saldoFinal = saldoFinal;
    }

    public int getMes() {
        return mes;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public BigDecimal getJuro() {
        return juro;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int mes;
        private BigDecimal saldoInicial;
        private BigDecimal juro;
        private BigDecimal saldoFinal;

        public Builder mes(int mes) {
            this.mes = mes;
            return this;
        }

        public Builder saldoInicial(BigDecimal saldoInicial) {
            this.saldoInicial = saldoInicial;
            return this;
        }

        public Builder juro(BigDecimal juro) {
            this.juro = juro;
            return this;
        }

        public Builder saldoFinal(BigDecimal saldoFinal) {
            this.saldoFinal = saldoFinal;
            return this;
        }

        public MemoriaCalculoDTO build() {
            return new MemoriaCalculoDTO(
                    mes,
                    saldoInicial,
                    juro,
                    saldoFinal
            );
        }
    }
}