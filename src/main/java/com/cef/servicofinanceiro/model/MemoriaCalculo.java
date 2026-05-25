package com.cef.servicofinanceiro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "memorias_calculo")
public class MemoriaCalculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int mes;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoInicial;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal juro;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoFinal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulacao_id", nullable = false)
    private Simulacao simulacao;

    public MemoriaCalculo() {
    }

    public MemoriaCalculo(
            Long id,
            int mes,
            BigDecimal saldoInicial,
            BigDecimal juro,
            BigDecimal saldoFinal,
            Simulacao simulacao
    ) {
        this.id = id;
        this.mes = mes;
        this.saldoInicial = saldoInicial;
        this.juro = juro;
        this.saldoFinal = saldoFinal;
        this.simulacao = simulacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getJuro() {
        return juro;
    }

    public void setJuro(BigDecimal juro) {
        this.juro = juro;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public Simulacao getSimulacao() {
        return simulacao;
    }

    public void setSimulacao(Simulacao simulacao) {
        this.simulacao = simulacao;
    }
}