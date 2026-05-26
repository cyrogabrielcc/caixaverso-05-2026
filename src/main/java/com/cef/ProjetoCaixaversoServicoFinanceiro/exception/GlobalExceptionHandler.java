package com.cef.ProjetoCaixaversoServicoFinanceiro.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import jakarta.ws.rs.ProcessingException;


import java.util.ArrayList;


@Provider
public class GlobalExceptionHandler {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @Context
    UriInfo uriInfo;

    @ServerExceptionMapper
    public Response handleRegraDeNegocioException(RegraDeNegocioException ex) {
        return buildResponse(
                Response.Status.BAD_REQUEST,
                "Regra de negócio violada",
                ex.getMessage()
        );
    }

    @ServerExceptionMapper
    public Response handleEntityNotFoundException(EntityNotFoundException ex) {
        return buildResponse(
                Response.Status.NOT_FOUND,
                "Recurso não encontrado",
                ex.getMessage()
        );
    }

    @ServerExceptionMapper
    public Response handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> detalhes = ex.getConstraintViolations()
                .stream()
                .map(this::formatarViolacao)
                .sorted()
                .toList();

        return buildResponseComDetalhes(
                Response.Status.BAD_REQUEST,
                "Dados inválidos",
                "A requisição possui campos inválidos.",
                detalhes
        );
    }

    @ServerExceptionMapper
    public Response handleInvalidFormatException(InvalidFormatException ex) {
        return buildResponseComDetalhes(
                Response.Status.BAD_REQUEST,
                "Tipo de dado inválido",
                "A requisição possui campo com tipo incompatível.",
                montarDetalhesCampoInvalido(ex)
        );
    }

    @ServerExceptionMapper
    public Response handleMismatchedInputException(MismatchedInputException ex) {
        return buildResponseComDetalhes(
                Response.Status.BAD_REQUEST,
                "Tipo de dado inválido",
                "A requisição possui campo com tipo incompatível com o contrato da API.",
                montarDetalhesMapeamentoInvalido(ex)
        );
    }

    @ServerExceptionMapper
    public Response handleJsonParseException(JsonParseException ex) {
        return buildResponseComDetalhes(
                Response.Status.BAD_REQUEST,
                "JSON inválido",
                "O corpo da requisição possui JSON malformado.",
                List.of("Verifique a estrutura do JSON enviado, incluindo aspas, vírgulas, chaves e colchetes.")
        );
    }

    @ServerExceptionMapper
    public Response handleJsonProcessingException(JsonProcessingException ex) {
        return buildResponseComDetalhes(
                Response.Status.BAD_REQUEST,
                "JSON inválido",
                "O corpo da requisição não pôde ser convertido para o contrato esperado pela API.",
                List.of("Verifique se os campos numéricos foram enviados como números, sem aspas.")
        );
    }

    @ServerExceptionMapper
    public Response handleBadRequestException(BadRequestException ex) {
        Throwable causaJson = encontrarCausaJson(ex);

        if (causaJson instanceof InvalidFormatException invalidFormatException) {
            return handleInvalidFormatException(invalidFormatException);
        }

        if (causaJson instanceof MismatchedInputException mismatchedInputException) {
            return handleMismatchedInputException(mismatchedInputException);
        }

        if (causaJson instanceof JsonParseException jsonParseException) {
            return handleJsonParseException(jsonParseException);
        }

        if (causaJson instanceof JsonProcessingException jsonProcessingException) {
            return handleJsonProcessingException(jsonProcessingException);
        }

        return buildResponse(
                Response.Status.BAD_REQUEST,
                "Requisição inválida",
                "A requisição enviada possui formato inválido ou não pôde ser processada."
        );
    }

    @ServerExceptionMapper
    public Response handleProcessingException(ProcessingException ex) {
        Throwable causaJson = encontrarCausaJson(ex);

        if (causaJson instanceof InvalidFormatException invalidFormatException) {
            return handleInvalidFormatException(invalidFormatException);
        }

        if (causaJson instanceof MismatchedInputException mismatchedInputException) {
            return handleMismatchedInputException(mismatchedInputException);
        }

        if (causaJson instanceof JsonParseException jsonParseException) {
            return handleJsonParseException(jsonParseException);
        }

        if (causaJson instanceof JsonProcessingException jsonProcessingException) {
            return handleJsonProcessingException(jsonProcessingException);
        }

        return buildResponse(
                Response.Status.BAD_REQUEST,
                "Requisição inválida",
                "A requisição enviada não pôde ser processada."
        );
    }

    @ServerExceptionMapper
    public Response handleNotFoundException(NotFoundException ex) {
        return buildResponse(
                Response.Status.NOT_FOUND,
                "Recurso não encontrado",
                "O recurso solicitado não foi encontrado."
        );
    }

    @ServerExceptionMapper
    public Response handleNotAllowedException(NotAllowedException ex) {
        return buildResponse(
                Response.Status.METHOD_NOT_ALLOWED,
                "Método não permitido",
                "O método HTTP utilizado não é permitido para este recurso."
        );
    }

    @ServerExceptionMapper
    public Response handleNotSupportedException(NotSupportedException ex) {
        return buildResponse(
                Response.Status.UNSUPPORTED_MEDIA_TYPE,
                "Tipo de mídia não suportado",
                "O tipo de conteúdo enviado não é suportado pela API. Utilize application/json."
        );
    }

    @ServerExceptionMapper
    public Response handlePersistenceException(PersistenceException ex) {
        LOG.error("Erro de persistência ao processar a requisição.", ex);

        return buildResponse(
                Response.Status.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor",
                "Ocorreu um erro ao acessar a base de dados."
        );
    }

    @ServerExceptionMapper
    public Response handleThrowable(Throwable ex) {
        Throwable causaJson = encontrarCausaJson(ex);

        if (causaJson instanceof InvalidFormatException invalidFormatException) {
            return handleInvalidFormatException(invalidFormatException);
        }

        if (causaJson instanceof MismatchedInputException mismatchedInputException) {
            return handleMismatchedInputException(mismatchedInputException);
        }

        if (causaJson instanceof JsonParseException jsonParseException) {
            return handleJsonParseException(jsonParseException);
        }

        if (causaJson instanceof JsonProcessingException jsonProcessingException) {
            return handleJsonProcessingException(jsonProcessingException);
        }

        LOG.error("Erro inesperado ao processar a requisição.", ex);

        return buildResponse(
                Response.Status.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor",
                "Ocorreu um erro inesperado. Tente novamente mais tarde."
        );
    }

    private Response buildResponse(
            Response.Status status,
            String erro,
            String mensagem
    ) {
        ErroResponseDTO erroResponse = ErroResponseDTO.of(
                status.getStatusCode(),
                erro,
                mensagem,
                obterCaminho()
        );

        return Response
                .status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(erroResponse)
                .build();
    }

    private Response buildResponseComDetalhes(
            Response.Status status,
            String erro,
            String mensagem,
            List<String> detalhes
    ) {
        ErroResponseDTO erroResponse = ErroResponseDTO.withDetalhes(
                status.getStatusCode(),
                erro,
                mensagem,
                obterCaminho(),
                detalhes
        );

        return Response
                .status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(erroResponse)
                .build();
    }

    private List<String> montarDetalhesCampoInvalido(InvalidFormatException ex) {
        String campo = obterCampoJson(ex);
        String valorRecebido = String.valueOf(ex.getValue());
        String tipoEsperado = obterTipoEsperado(ex.getTargetType());

        List<String> detalhes = new ArrayList<>();

        detalhes.add("Campo: " + campo);
        detalhes.add("Valor recebido: " + valorRecebido);
        detalhes.add("Tipo esperado: " + tipoEsperado);

        if (Number.class.isAssignableFrom(ex.getTargetType())
                || ex.getTargetType().isPrimitive()) {
            detalhes.add("Orientação: envie campos numéricos sem aspas e sem letras. Exemplo correto: \"valorInicial\": 1000.00");
        }

        return detalhes;
    }

    private List<String> montarDetalhesMapeamentoInvalido(MismatchedInputException ex) {
        String campo = obterCampoJson(ex);
        String tipoEsperado = obterTipoEsperado(ex.getTargetType());

        return List.of(
                "Campo: " + campo,
                "Tipo esperado: " + tipoEsperado,
                "Orientação: confira se o valor enviado respeita o tipo definido no contrato da API."
        );
    }

    private String obterCampoJson(JsonMappingException ex) {
        if (ex.getPath() == null || ex.getPath().isEmpty()) {
            return "corpo da requisição";
        }

        JsonMappingException.Reference ultimaReferencia = ex.getPath().get(ex.getPath().size() - 1);

        if (ultimaReferencia.getFieldName() != null) {
            return ultimaReferencia.getFieldName();
        }

        return "corpo da requisição";
    }

    private String obterTipoEsperado(Class<?> targetType) {
        if (targetType == null) {
            return "tipo compatível com o contrato da API";
        }

        if (targetType.equals(Integer.class) || targetType.equals(Integer.TYPE)) {
            return "número inteiro";
        }

        if (targetType.equals(Long.class) || targetType.equals(Long.TYPE)) {
            return "número inteiro longo";
        }

        if (targetType.equals(Double.class)
                || targetType.equals(Double.TYPE)
                || targetType.equals(Float.class)
                || targetType.equals(Float.TYPE)
                || targetType.equals(BigDecimal.class)) {
            return "número decimal";
        }

        return targetType.getSimpleName();
    }

    private String formatarViolacao(ConstraintViolation<?> violacao) {
        return violacao.getPropertyPath() + ": " + violacao.getMessage();
    }

    private Throwable encontrarCausaJson(Throwable ex) {
        Throwable atual = ex;

        while (atual != null) {
            if (atual instanceof JsonProcessingException) {
                return atual;
            }

            atual = atual.getCause();
        }

        return null;
    }

    private String obterCaminho() {
        if (uriInfo == null || uriInfo.getPath() == null || uriInfo.getPath().isBlank()) {
            return "/";
        }

        String caminho = uriInfo.getPath();

        if (caminho.startsWith("/")) {
            return caminho;
        }

        return "/" + caminho;
    }
}