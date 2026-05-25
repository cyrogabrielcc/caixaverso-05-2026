package com.cef.servicofinanceiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        name = "SimulacaoRequest",
        description = "Representa os dados de entrada para uma simulação de juros compostos."
)
public class SimulacaoRequestDTO {

    @Schema(
            description = "Valor inicial da operação financeira.",
            example = "1000.00",
            required = true
    )
    @NotNull(message = "O valor inicial é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor inicial deve ser maior que zero.")
    private BigDecimal valorInicial;

    @Schema(
            description = "Taxa de juros mensal em formato percentual. Exemplo: 1.5 representa 1,5% ao mês.",
            example = "1.5",
            required = true
    )
    @NotNull(message = "A taxa de juros mensal é obrigatória.")
    @DecimalMin(value = "0.00", message = "A taxa de juros mensal não pode ser negativa.")
    private BigDecimal taxaJurosMensal;

    @Schema(
            description = "Prazo da operação em meses.",
            example = "12",
            required = true
    )
    @NotNull(message = "O prazo em meses é obrigatório.")
    @Positive(message = "O prazo em meses deve ser maior que zero.")
    private Integer prazoMeses;

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getTaxaJurosMensal() {
        return taxaJurosMensal;
    }

    public void setTaxaJurosMensal(BigDecimal taxaJurosMensal) {
        this.taxaJurosMensal = taxaJurosMensal;
    }

    public Integer getPrazoMeses() {
        return prazoMeses;
    }

    public void setPrazoMeses(Integer prazoMeses) {
        this.prazoMeses = prazoMeses;
    }
}