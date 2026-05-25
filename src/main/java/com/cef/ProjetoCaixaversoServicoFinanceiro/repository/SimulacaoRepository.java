package com.cef.ProjetoCaixaversoServicoFinanceiro.repository;


import com.cef.ProjetoCaixaversoServicoFinanceiro.model.Simulacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SimulacaoRepository implements PanacheRepository<Simulacao> {
}