package com.cef.ProjetoCaixaversoServicoFinanceiro.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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

    @Column(name = "valor_inicial", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorInicial;

    @Column(name = "taxa_juros_mensal", nullable = false, precision = 19, scale = 6)
    private BigDecimal taxaJurosMensal;

    @Column(name = "prazo_meses", nullable = false)
    private Integer prazoMeses;

    @Column(name = "valor_total_final", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotalFinal;

    @Column(name = "valor_total_juros", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotalJuros;

    @OneToMany(
            mappedBy = "simulacao",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("mes ASC")
    private List<MemoriaCalculo> memoriaCalculo = new ArrayList<>();

    public Simulacao() {
    }

    public Long getId() {
        return id;
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
            memoriaCalculo.forEach(this::adicionarMemoriaCalculo);
        }
    }

    public void adicionarMemoriaCalculo(MemoriaCalculo memoriaCalculo) {
        if (memoriaCalculo != null) {
            memoriaCalculo.setSimulacao(this);
            this.memoriaCalculo.add(memoriaCalculo);
        }
    }

    public void removerMemoriaCalculo(MemoriaCalculo memoriaCalculo) {
        if (memoriaCalculo != null) {
            memoriaCalculo.setSimulacao(null);
            this.memoriaCalculo.remove(memoriaCalculo);
        }
    }
}