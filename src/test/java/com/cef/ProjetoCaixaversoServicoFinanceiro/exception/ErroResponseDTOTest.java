package com.cef.ProjetoCaixaversoServicoFinanceiro.exception;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErroResponseDTOTest {

    @Test
    void deveCriarErroResponseDTOComConstrutorCompleto() {
        List<String> detalhes = List.of("Campo obrigatório", "Valor inválido");

        ErroResponseDTO dto = new ErroResponseDTO(
                "25/05/2026 21:47:32",
                400,
                "Dados inválidos",
                "A requisição possui campos inválidos.",
                "/simulacoes",
                detalhes
        );

        assertEquals("25/05/2026 21:47:32", dto.getTimestamp());
        assertEquals(400, dto.getStatus());
        assertEquals("Dados inválidos", dto.getErro());
        assertEquals("A requisição possui campos inválidos.", dto.getMensagem());
        assertEquals("/simulacoes", dto.getCaminho());
        assertEquals(detalhes, dto.getDetalhes());
    }

    @Test
    void deveCriarErroResponseDTOComMetodoOf() {
        ErroResponseDTO dto = ErroResponseDTO.of(
                400,
                "Regra de negócio violada",
                "O valor inicial deve ser maior que zero.",
                "/simulacoes"
        );

        assertNotNull(dto.getTimestamp());
        assertTrue(dto.getTimestamp().matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}"));

        assertEquals(400, dto.getStatus());
        assertEquals("Regra de negócio violada", dto.getErro());
        assertEquals("O valor inicial deve ser maior que zero.", dto.getMensagem());
        assertEquals("/simulacoes", dto.getCaminho());

        assertNotNull(dto.getDetalhes());
        assertTrue(dto.getDetalhes().isEmpty());
    }

    @Test
    void deveCriarErroResponseDTOComDetalhes() {
        List<String> detalhes = List.of(
                "valorInicial: O valor inicial é obrigatório.",
                "prazoMeses: O prazo em meses deve ser maior que zero."
        );

        ErroResponseDTO dto = ErroResponseDTO.withDetalhes(
                400,
                "Dados inválidos",
                "A requisição possui campos inválidos.",
                "/simulacoes",
                detalhes
        );

        assertNotNull(dto.getTimestamp());
        assertTrue(dto.getTimestamp().matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}"));

        assertEquals(400, dto.getStatus());
        assertEquals("Dados inválidos", dto.getErro());
        assertEquals("A requisição possui campos inválidos.", dto.getMensagem());
        assertEquals("/simulacoes", dto.getCaminho());
        assertEquals(detalhes, dto.getDetalhes());
    }

    @Test
    void deveCriarErroResponseDTOComListaVaziaQuandoDetalhesForNulo() {
        ErroResponseDTO dto = ErroResponseDTO.withDetalhes(
                400,
                "Dados inválidos",
                "A requisição possui campos inválidos.",
                "/simulacoes",
                null
        );

        assertNotNull(dto.getDetalhes());
        assertTrue(dto.getDetalhes().isEmpty());
    }

    @Test
    void devePermitirAlterarCamposComSetters() {
        ErroResponseDTO dto = new ErroResponseDTO();

        List<String> detalhes = List.of("Detalhe 1", "Detalhe 2");

        dto.setTimestamp("25/05/2026 22:00:00");
        dto.setStatus(500);
        dto.setErro("Erro interno do servidor");
        dto.setMensagem("Ocorreu um erro inesperado.");
        dto.setCaminho("/simulacoes");
        dto.setDetalhes(detalhes);

        assertEquals("25/05/2026 22:00:00", dto.getTimestamp());
        assertEquals(500, dto.getStatus());
        assertEquals("Erro interno do servidor", dto.getErro());
        assertEquals("Ocorreu um erro inesperado.", dto.getMensagem());
        assertEquals("/simulacoes", dto.getCaminho());
        assertEquals(detalhes, dto.getDetalhes());
    }
}