package com.cef.ProjetoCaixaversoServicoFinanceiro.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerComplementarTest {

    @Test
    void deveTratarRegraDeNegocioExceptionComUriInfo() throws Exception {
        GlobalExceptionHandler handler = criarHandlerComUriInfo("/simulacoes");

        Response response = invocarHandler(
                handler,
                new RegraDeNegocioException("Erro de negócio.")
        );

        ErroResponseDTO erro = validarErroMinimo(response, 400);

        assertEquals("/simulacoes", erro.getCaminho());
        assertEquals("Regra de negócio violada", erro.getErro());
        assertEquals("Erro de negócio.", erro.getMensagem());
    }

    @Test
    void deveTratarEntityNotFoundExceptionComUriInfo() throws Exception {
        GlobalExceptionHandler handler = criarHandlerComUriInfo("/simulacoes/999");

        Response response = invocarHandler(
                handler,
                new EntityNotFoundException("Simulação não encontrada com o ID informado.")
        );

        ErroResponseDTO erro = validarErroMinimo(response, 404);

        assertEquals("/simulacoes/999", erro.getCaminho());
        assertEquals("Simulação não encontrada com o ID informado.", erro.getMensagem());
    }

    @Test
    void deveTratarPersistenceExceptionComUriInfo() throws Exception {
        GlobalExceptionHandler handler = criarHandlerComUriInfo("/simulacoes");

        Response response = invocarHandler(
                handler,
                new PersistenceException("Erro no banco")
        );

        ErroResponseDTO erro = validarErroMinimo(response, 500);

        assertEquals("/simulacoes", erro.getCaminho());
        assertNotNull(erro.getErro());
        assertNotNull(erro.getMensagem());
    }

    @Test
    void deveTratarThrowableGenericoComUriInfo() throws Exception {
        GlobalExceptionHandler handler = criarHandlerComUriInfo("/simulacoes");

        Response response = invocarHandler(
                handler,
                new RuntimeException("Erro inesperado")
        );

        ErroResponseDTO erro = validarErroMinimo(response, 500);

        assertEquals("/simulacoes", erro.getCaminho());
        assertNotNull(erro.getErro());
        assertNotNull(erro.getMensagem());
    }

    @Test
    void deveTratarConstraintViolationExceptionComListaVazia() throws Exception {
        GlobalExceptionHandler handler = criarHandlerComUriInfo("/simulacoes");

        ConstraintViolationException exception = new ConstraintViolationException(
                "Erro de validação",
                Collections.emptySet()
        );

        Response response = invocarHandler(handler, exception);

        ErroResponseDTO erro = validarErroMinimo(response, 400);

        assertEquals("/simulacoes", erro.getCaminho());
        assertNotNull(erro.getDetalhes());
        assertTrue(erro.getDetalhes().isEmpty());
    }

    @Test
    void deveTratarConstraintViolationExceptionComDetalhe() throws Exception {
        GlobalExceptionHandler handler = criarHandlerComUriInfo("/simulacoes");

        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);

        when(path.toString()).thenReturn("valorInicial");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("O valor inicial é obrigatório.");

        ConstraintViolationException exception = new ConstraintViolationException(
                "Erro de validação",
                Set.of(violation)
        );

        Response response = invocarHandler(handler, exception);

        ErroResponseDTO erro = validarErroMinimo(response, 400);

        assertEquals("/simulacoes", erro.getCaminho());
        assertNotNull(erro.getDetalhes());
        assertFalse(erro.getDetalhes().isEmpty());
    }

    @Test
    void deveRetornarCaminhoRaizQuandoNaoHouverUriInfo() throws Exception {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        Response response = invocarHandler(
                handler,
                new RegraDeNegocioException("Erro de negócio.")
        );

        ErroResponseDTO erro = validarErroMinimo(response, 400);

        assertEquals("/", erro.getCaminho());
    }

    private GlobalExceptionHandler criarHandlerComUriInfo(String caminho) throws Exception {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        String caminhoSemBarraInicial = caminho.startsWith("/")
                ? caminho.substring(1)
                : caminho;

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn(caminhoSemBarraInicial);
        when(uriInfo.getRequestUri()).thenReturn(URI.create("http://localhost:8080" + caminho));

        definirUriInfo(handler, uriInfo);

        return handler;
    }

    private void definirUriInfo(GlobalExceptionHandler handler, UriInfo uriInfo) throws Exception {
        Field campoUriInfo = encontrarCampoUriInfo();
        campoUriInfo.setAccessible(true);
        campoUriInfo.set(handler, uriInfo);
    }

    private Field encontrarCampoUriInfo() {
        for (Field field : GlobalExceptionHandler.class.getDeclaredFields()) {
            if (UriInfo.class.isAssignableFrom(field.getType())) {
                return field;
            }
        }

        throw new IllegalStateException("Nenhum campo UriInfo encontrado no GlobalExceptionHandler.");
    }

    private Response invocarHandler(GlobalExceptionHandler handler, Throwable exception) throws Exception {
        Method metodo = encontrarMetodoHandler(exception);
        metodo.setAccessible(true);

        return (Response) metodo.invoke(handler, exception);
    }

    private Method encontrarMetodoHandler(Throwable exception) {
        Method metodoGenerico = null;

        for (Method method : GlobalExceptionHandler.class.getDeclaredMethods()) {
            if (!Response.class.equals(method.getReturnType())) {
                continue;
            }

            if (method.getParameterCount() != 1) {
                continue;
            }

            Class<?> tipoParametro = method.getParameterTypes()[0];

            if (tipoParametro.equals(exception.getClass())) {
                return method;
            }

            if (tipoParametro.isAssignableFrom(exception.getClass())) {
                metodoGenerico = method;
            }
        }

        if (metodoGenerico != null) {
            return metodoGenerico;
        }

        throw new IllegalStateException(
                "Nenhum handler encontrado para: " + exception.getClass().getName()
        );
    }

    private ErroResponseDTO validarErroMinimo(Response response, int statusEsperado) {
        assertNotNull(response);
        assertEquals(statusEsperado, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        assertNotNull(response.getEntity());
        assertInstanceOf(ErroResponseDTO.class, response.getEntity());

        ErroResponseDTO erro = (ErroResponseDTO) response.getEntity();

        assertEquals(statusEsperado, erro.getStatus());
        assertNotNull(erro.getTimestamp());
        assertNotNull(erro.getErro());
        assertNotNull(erro.getMensagem());
        assertNotNull(erro.getCaminho());
        assertNotNull(erro.getDetalhes());

        return erro;
    }
}