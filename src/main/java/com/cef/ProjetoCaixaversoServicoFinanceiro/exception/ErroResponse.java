package com.cef.ProjetoCaixaversoServicoFinanceiro.exception;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Schema(
        name = "ErroResponse",
        description = "Resposta padronizada para erros da API."
)
public class ErroResponse {

    @Schema(
            description = "Data e hora em que o erro ocorreu, no formato ISO-8601.",
            example = "2026-05-25T17:30:00Z"
    )
    private final String timestamp;

    @Schema(
            description = "Código HTTP da resposta.",
            example = "400"
    )
    private final int status;

    @Schema(
            description = "Título resumido do erro.",
            example = "Dados inválidos"
    )
    private final String erro;

    @Schema(
            description = "Mensagem detalhada sobre o erro ocorrido.",
            example = "A requisição possui campos inválidos."
    )
    private final String mensagem;

    @Schema(
            description = "Caminho do endpoint em que o erro ocorreu.",
            example = "/simulacoes"
    )
    private final String caminho;

    @Schema(
            description = "Lista de detalhes adicionais sobre o erro.",
            example = "[\"valorInicial: O valor inicial é obrigatório.\"]"
    )
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