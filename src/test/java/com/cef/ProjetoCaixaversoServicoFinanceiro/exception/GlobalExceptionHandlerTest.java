package com.cef.ProjetoCaixaversoServicoFinanceiro.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void configurar() {
        handler = new GlobalExceptionHandler();

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn("simulacoes");

        handler.uriInfo = uriInfo;
    }

    @Test
    void deveTratarRegraDeNegocioExceptionComoBadRequest() {
        Response response = handler.handleRegraDeNegocioException(
                new RegraDeNegocioException("O valor inicial deve ser maior que zero.")
        );

        ErroResponseDTO erro = validarErroBasico(
                response,
                400,
                "Regra de negócio violada",
                "O valor inicial deve ser maior que zero."
        );

        assertEquals("/simulacoes", erro.getCaminho());
        assertTrue(erro.getDetalhes().isEmpty());
    }

    @Test
    void deveTratarEntityNotFoundExceptionComoNotFound() {
        Response response = handler.handleEntityNotFoundException(
                new EntityNotFoundException("Simulação não encontrada.")
        );

        ErroResponseDTO erro = validarErroBasico(
                response,
                404,
                "Recurso não encontrado",
                "Simulação não encontrada."
        );

        assertEquals("/simulacoes", erro.getCaminho());
    }

    @Test
    void deveTratarConstraintViolationExceptionComoBadRequestComDetalhes() {
        ConstraintViolation<?> violacao = criarViolacao(
                "valorInicial",
                "O valor inicial é obrigatório."
        );

        ConstraintViolationException exception =
                new ConstraintViolationException(Set.of(violacao));

        Response response = handler.handleConstraintViolationException(exception);

        ErroResponseDTO erro = validarErroBasico(
                response,
                400,
                "Dados inválidos",
                "A requisição possui campos inválidos."
        );

        assertEquals(1, erro.getDetalhes().size());
        assertEquals(
                "valorInicial: O valor inicial é obrigatório.",
                erro.getDetalhes().get(0)
        );
    }

    @Test
    void deveTratarInvalidFormatExceptionComoBadRequestComDetalhesDoCampo() {
        InvalidFormatException exception = InvalidFormatException.from(
                null,
                "Valor inválido",
                "abc",
                BigDecimal.class
        );

        Response response = handler.handleInvalidFormatException(exception);

        ErroResponseDTO erro = validarErroBasico(
                response,
                400,
                "Tipo de dado inválido",
                "A requisição possui campo com tipo incompatível."
        );

        assertTrue(erro.getDetalhes().contains("Campo: corpo da requisição"));
        assertTrue(erro.getDetalhes().contains("Valor recebido: abc"));
        assertTrue(erro.getDetalhes().contains("Tipo esperado: número decimal"));
        assertTrue(erro.getDetalhes().stream()
                .anyMatch(detalhe -> detalhe.startsWith("Orientação:")));
    }

    @Test
    void deveTratarMismatchedInputExceptionComoBadRequestComDetalhes() {
        MismatchedInputException exception = MismatchedInputException.from(
                (JsonParser) null,
                Integer.class,
                "Tipo incompatível"
        );

        Response response = handler.handleMismatchedInputException(exception);

        ErroResponseDTO erro = validarErroBasico(
                response,
                400,
                "Tipo de dado inválido",
                "A requisição possui campo com tipo incompatível com o contrato da API."
        );

        assertTrue(erro.getDetalhes().contains("Campo: corpo da requisição"));
        assertTrue(erro.getDetalhes().contains("Tipo esperado: número inteiro"));
        assertTrue(erro.getDetalhes().stream()
                .anyMatch(detalhe -> detalhe.startsWith("Orientação:")));
    }

    @Test
    void deveTratarJsonParseExceptionComoBadRequest() {
        JsonParseException exception =
                new JsonParseException((JsonParser) null, "JSON malformado");

        Response response = handler.handleJsonParseException(exception);

        ErroResponseDTO erro = validarErroBasico(
                response,
                400,
                "JSON inválido",
                "O corpo da requisição possui JSON malformado."
        );

        assertEquals(1, erro.getDetalhes().size());
        assertEquals(
                "Verifique a estrutura do JSON enviado, incluindo aspas, vírgulas, chaves e colchetes.",
                erro.getDetalhes().get(0)
        );
    }

    @Test
    void deveTratarJsonProcessingExceptionComoBadRequest() {
        JsonProcessingException exception =
                new JsonProcessingException("Erro ao processar JSON") {
                };

        Response response = handler.handleJsonProcessingException(exception);

        ErroResponseDTO erro = validarErroBasico(
                response,
                400,
                "JSON inválido",
                "O corpo da requisição não pôde ser convertido para o contrato esperado pela API."
        );

        assertEquals(1, erro.getDetalhes().size());
        assertEquals(
                "Verifique se os campos numéricos foram enviados como números, sem aspas.",
                erro.getDetalhes().get(0)
        );
    }

    @Test
    void deveTratarBadRequestExceptionSemCausaJsonComoBadRequestGenerico() {
        Response response = handler.handleBadRequestException(
                new BadRequestException("Requisição ruim")
        );

        validarErroBasico(
                response,
                400,
                "Requisição inválida",
                "A requisição enviada possui formato inválido ou não pôde ser processada."
        );
    }

    @Test
    void deveTratarBadRequestExceptionComCausaJsonComoErroDeJson() {
        InvalidFormatException causa = InvalidFormatException.from(
                null,
                "Valor inválido",
                "abc",
                BigDecimal.class
        );

        Response response = handler.handleBadRequestException(
                new BadRequestException(causa)
        );

        ErroResponseDTO erro = validarErroBasico(
                response,
                400,
                "Tipo de dado inválido",
                "A requisição possui campo com tipo incompatível."
        );

        assertTrue(erro.getDetalhes().contains("Valor recebido: abc"));
        assertTrue(erro.getDetalhes().contains("Tipo esperado: número decimal"));
    }

    @Test
    void deveTratarProcessingExceptionSemCausaJsonComoBadRequestGenerico() {
        Response response = handler.handleProcessingException(
                new ProcessingException("Erro de processamento")
        );

        validarErroBasico(
                response,
                400,
                "Requisição inválida",
                "A requisição enviada não pôde ser processada."
        );
    }

    @Test
    void deveTratarProcessingExceptionComCausaJsonComoErroDeJson() {
        JsonProcessingException causa =
                new JsonProcessingException("Erro ao processar JSON") {
                };

        Response response = handler.handleProcessingException(
                new ProcessingException(causa)
        );

        validarErroBasico(
                response,
                400,
                "JSON inválido",
                "O corpo da requisição não pôde ser convertido para o contrato esperado pela API."
        );
    }

    @Test
    void deveTratarNotFoundExceptionComoNotFound() {
        Response response = handler.handleNotFoundException(
                new NotFoundException()
        );

        validarErroBasico(
                response,
                404,
                "Recurso não encontrado",
                "O recurso solicitado não foi encontrado."
        );
    }

    @Test
    void deveTratarNotAllowedExceptionComoMethodNotAllowed() {
        Response response = handler.handleNotAllowedException(
                mock(NotAllowedException.class)
        );

        validarErroBasico(
                response,
                405,
                "Método não permitido",
                "O método HTTP utilizado não é permitido para este recurso."
        );
    }

    @Test
    void deveTratarNotSupportedExceptionComoUnsupportedMediaType() {
        Response response = handler.handleNotSupportedException(
                mock(NotSupportedException.class)
        );

        validarErroBasico(
                response,
                415,
                "Tipo de mídia não suportado",
                "O tipo de conteúdo enviado não é suportado pela API. Utilize application/json."
        );
    }

    @Test
    void deveTratarPersistenceExceptionComoInternalServerError() {
        Response response = handler.handlePersistenceException(
                new PersistenceException("Erro no banco")
        );

        validarErroBasico(
                response,
                500,
                "Erro interno do servidor",
                "Ocorreu um erro ao acessar a base de dados."
        );
    }

    @Test
    void deveTratarThrowableGenericoComoInternalServerError() {
        Response response = handler.handleThrowable(
                new RuntimeException("Erro inesperado")
        );

        validarErroBasico(
                response,
                500,
                "Erro interno do servidor",
                "Ocorreu um erro inesperado. Tente novamente mais tarde."
        );
    }

    @Test
    void deveTratarThrowableComCausaJsonComoBadRequest() {
        InvalidFormatException causa = InvalidFormatException.from(
                null,
                "Valor inválido",
                "abc",
                BigDecimal.class
        );

        RuntimeException exception = new RuntimeException("Wrapper", causa);

        Response response = handler.handleThrowable(exception);

        ErroResponseDTO erro = validarErroBasico(
                response,
                400,
                "Tipo de dado inválido",
                "A requisição possui campo com tipo incompatível."
        );

        assertTrue(erro.getDetalhes().contains("Valor recebido: abc"));
    }


    @Test
    void deveRetornarCaminhoRaizQuandoUriInfoForNulo() {
        GlobalExceptionHandler handlerSemUriInfo = new GlobalExceptionHandler();

        Response response = handlerSemUriInfo.handleRegraDeNegocioException(
                new RegraDeNegocioException("Erro de negócio.")
        );

        ErroResponseDTO erro = validarErroBasico(
                response,
                400,
                "Regra de negócio violada",
                "Erro de negócio."
        );

        assertEquals("/", erro.getCaminho());
    }

    private ErroResponseDTO validarErroBasico(
            Response response,
            int statusEsperado,
            String erroEsperado,
            String mensagemEsperada
    ) {
        assertEquals(statusEsperado, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        assertNotNull(response.getEntity());
        assertInstanceOf(ErroResponseDTO.class, response.getEntity());

        ErroResponseDTO erro = (ErroResponseDTO) response.getEntity();

        assertNotNull(erro.getTimestamp());
        assertTrue(erro.getTimestamp().matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}"));

        assertEquals(statusEsperado, erro.getStatus());
        assertEquals(erroEsperado, erro.getErro());
        assertEquals(mensagemEsperada, erro.getMensagem());

        assertNotNull(erro.getDetalhes());

        return erro;
    }
    @SuppressWarnings("unchecked")
    private ConstraintViolation<?> criarViolacao(String campo, String mensagem) {
        ConstraintViolation<Object> violacao = mock(ConstraintViolation.class);
        Path path = mock(Path.class);

        when(path.toString()).thenReturn(campo);
        when(violacao.getPropertyPath()).thenReturn(path);
        when(violacao.getMessage()).thenReturn(mensagem);

        return violacao;
    }
}