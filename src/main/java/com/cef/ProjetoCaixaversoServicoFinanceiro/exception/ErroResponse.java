package com.cef.ProjetoCaixaversoServicoFinanceiro.exception;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class ErroResponse {

    private final String timestamp;
    private final int status;
    private final String erro;
    private final String mensagem;
    private final String caminho;
    private final List<String> detalhes;

    private ErroResponse(
            int status,
            String erro,
            String mensagem,
            String caminho,
            List<String> detalhes
    ) {
        this.timestamp = OffsetDateTime.now(ZoneOffset.UTC).toString();
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.caminho = caminho;
        this.detalhes = detalhes;
    }

    public static ErroResponse of(
            int status,
            String erro,
            String mensagem,
            String caminho
    ) {
        return new ErroResponse(status, erro, mensagem, caminho, List.of());
    }

    public static ErroResponse withDetalhes(
            int status,
            String erro,
            String mensagem,
            String caminho,
            List<String> detalhes
    ) {
        return new ErroResponse(status, erro, mensagem, caminho, detalhes);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getErro() {
        return erro;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getCaminho() {
        return caminho;
    }

    public List<String> getDetalhes() {
        return detalhes;
    }
}