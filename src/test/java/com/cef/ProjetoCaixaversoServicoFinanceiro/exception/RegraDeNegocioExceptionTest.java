package com.cef.ProjetoCaixaversoServicoFinanceiro.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegraDeNegocioExceptionTest {

    @Test
    void deveCriarExceptionComMensagem() {
        RegraDeNegocioException exception =
                new RegraDeNegocioException("Regra de negócio violada.");

        assertEquals("Regra de negócio violada.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void deveCriarExceptionComMensagemECausa() {
        Throwable causa = new IllegalArgumentException("Valor inválido.");

        RegraDeNegocioException exception =
                new RegraDeNegocioException("Erro ao validar regra de negócio.", causa);

        assertEquals("Erro ao validar regra de negócio.", exception.getMessage());
        assertSame(causa, exception.getCause());
    }

    @Test
    void deveSerUmaRuntimeException() {
        RegraDeNegocioException exception =
                new RegraDeNegocioException("Erro de regra.");

        assertInstanceOf(RuntimeException.class, exception);
    }
}