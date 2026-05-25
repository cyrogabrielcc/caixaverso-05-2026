package com.cef.ProjetoCaixaversoServicoFinanceiro.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "simulacoes")
public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valorInicial;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal taxaJurosMensal;

    @Column(nullable = false)
    private Integer prazoMeses;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotalFinal;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotalJuros;

    @OneToMany(
            mappedBy = "simulacao",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<MemoriaCalculo> memoriaCalculo = new ArrayList<>();

    public Simulacao() {
    }

    public Simulacao(
            Long id,
            BigDecimal valorInicial,
            BigDecimal taxaJurosMensal,
            Integer prazoMeses,
            BigDecimal valorTotalFinal,
            BigDecimal valorTotalJuros,
            List<MemoriaCalculo> memoriaCalculo
    ) {
        this.id = id;
        this.valorInicial = valorInicial;
        this.taxaJurosMensal = taxaJurosMensal;
        this.prazoMeses = prazoMeses;
        this.valorTotalFinal = valorTotalFinal;
        this.valorTotalJuros = valorTotalJuros;
        setMemoriaCalculo(memoriaCalculo);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public BigDecimal getValorTotalFinal() {
        return valorTotalFinal;
    }

    public void setValorTotalFinal(BigDecimal valorTotalFinal) {
        this.valorTotalFinal = valorTotalFinal;
    }

    public BigDecimal getValorTotalJuros() {
        return valorTotalJuros;
    }

    public void setValorTotalJuros(BigDecimal valorTotalJuros) {
        this.valorTotalJuros = valorTotalJuros;
    }

    public List<MemoriaCalculo> getMemoriaCalculo() {
        return memoriaCalculo;
    }

    public void setMemoriaCalculo(List<MemoriaCalculo> memoriaCalculo) {
        this.memoriaCalculo.clear();

        if (memoriaCalculo != null) {
            for (MemoriaCalculo item : memoriaCalculo) {
                adicionarMemoriaCalculo(item);
            }
        }
    }

    public void adicionarMemoriaCalculo(MemoriaCalculo memoria) {
        memoria.setSimulacao(this);
        this.memoriaCalculo.add(memoria);
    }
}