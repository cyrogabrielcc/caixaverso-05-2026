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

import java.util.List;

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

        ErroResponse erroResponse = ErroResponse.withDetalhes(
                Response.Status.BAD_REQUEST.getStatusCode(),
                "Dados inválidos",
                "A requisição possui campos inválidos.",
                obterCaminho(),
                detalhes
        );

        return Response
                .status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(erroResponse)
                .build();
    }

    @ServerExceptionMapper
    public Response handleBadRequestException(BadRequestException ex) {
        return buildResponse(
                Response.Status.BAD_REQUEST,
                "Requisição inválida",
                "A requisição enviada possui formato inválido ou não pôde ser processada."
        );
    }

    @ServerExceptionMapper
    public Response handleJsonProcessingException(JsonProcessingException ex) {
        return buildResponse(
                Response.Status.BAD_REQUEST,
                "JSON inválido",
                "O corpo da requisição possui JSON inválido ou incompatível com o contrato da API."
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
                "O tipo de conteúdo enviado não é suportado pela API."
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
        ErroResponse erroResponse = ErroResponse.of(
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

    private String formatarViolacao(ConstraintViolation<?> violacao) {
        return violacao.getPropertyPath() + ": " + violacao.getMessage();
    }

    private String obterCaminho() {
        if (uriInfo == null) {
            return null;
        }

        return "/" + uriInfo.getPath();
    }
}