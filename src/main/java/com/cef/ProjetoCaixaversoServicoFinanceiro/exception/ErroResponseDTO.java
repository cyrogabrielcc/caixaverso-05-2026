package com.cef.ProjetoCaixaversoServicoFinanceiro.exception;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Schema(
        name = "ErroResponse",
        description = "Representa uma resposta padronizada de erro da API."
)
public class ErroResponseDTO {

    private static final ZoneId ZONA_PADRAO = ZoneId.of("America/Sao_Paulo");

    private static final DateTimeFormatter FORMATADOR_TIMESTAMP =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Schema(
            description = "Data e hora em que o erro ocorreu.",
            example = "25/05/2026 21:47:32"
    )
    private String timestamp;

    @Schema(description = "Código HTTP retornado.", example = "400")
    private int status;

    @Schema(description = "Título resumido do erro.", example = "Dados inválidos")
    private String erro;

    @Schema(description = "Mensagem principal explicando o erro.", example = "A requisição possui campos inválidos.")
    private String mensagem;

    @Schema(description = "Caminho do endpoint onde o erro ocorreu.", example = "/simulacoes")
    private String caminho;

    @Schema(description = "Lista de detalhes específicos sobre o erro.")
    private List<String> detalhes;

    public ErroResponseDTO() {
    }

    public ErroResponseDTO(
            String timestamp,
            int status,
            String erro,
            String mensagem,
            String caminho,
            List<String> detalhes
    ) {
        this.timestamp = timestamp;
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.caminho = caminho;
        this.detalhes = detalhes;
    }

    public static ErroResponseDTO of(
            int status,
            String erro,
            String mensagem,
            String caminho
    ) {
        return new ErroResponseDTO(
                gerarTimestamp(),
                status,
                erro,
                mensagem,
                caminho,
                new ArrayList<>()
        );
    }

    public static ErroResponseDTO withDetalhes(
            int status,
            String erro,
            String mensagem,
            String caminho,
            List<String> detalhes
    ) {
        return new ErroResponseDTO(
                gerarTimestamp(),
                status,
                erro,
                mensagem,
                caminho,
                detalhes == null ? new ArrayList<>() : detalhes
        );
    }

    private static String gerarTimestamp() {
        return LocalDateTime.now(ZONA_PADRAO).format(FORMATADOR_TIMESTAMP);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public List<String> getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(List<String> detalhes) {
        this.detalhes = detalhes;
    }
}