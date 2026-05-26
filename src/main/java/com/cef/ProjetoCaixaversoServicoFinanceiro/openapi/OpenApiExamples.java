package com.cef.ProjetoCaixaversoServicoFinanceiro.openapi;

public final class OpenApiExamples {

    private OpenApiExamples() {
    }

    public static final String SIMULACAO_RESPONSE = """
            {
              "id": 1,
              "valorInicial": 1000.00,
              "taxaJurosMensal": 1.5,
              "prazoMeses": 3,
              "valorTotalFinal": 1045.68,
              "valorTotalJuros": 45.68,
              "memoriaCalculo": [
                {
                  "mes": 1,
                  "saldoInicial": 1000.00,
                  "juro": 15.00,
                  "saldoFinal": 1015.00
                },
                {
                  "mes": 2,
                  "saldoInicial": 1015.00,
                  "juro": 15.23,
                  "saldoFinal": 1030.23
                },
                {
                  "mes": 3,
                  "saldoInicial": 1030.23,
                  "juro": 15.45,
                  "saldoFinal": 1045.68
                }
              ]
            }
            """;

    public static final String ERRO_POST_VALIDACAO = """
            {
              "timestamp": "2026-05-25 17:30:00",
              "status": 400,
              "erro": "Dados inválidos",
              "mensagem": "A requisição possui campos inválidos.",
              "caminho": "/simulacoes",
              "detalhes": [
                "valorInicial: O valor inicial é obrigatório.",
                "taxaJurosMensal: A taxa de juros mensal é obrigatória.",
                "prazoMeses: O prazo em meses deve ser maior que zero."
              ]
            }
            """;

    public static final String ERRO_POST_JSON_INVALIDO = """
            {
              "timestamp": "2026-05-25 17:30:00",
              "status": 400,
              "erro": "Requisição inválida",
              "mensagem": "O campo 'valorInicial' deve receber apenas números.",
              "caminho": "/simulacoes",
              "detalhes": [
                "valorInicial: valor inválido para campo numérico"
              ]
            }
            """;

    public static final String ERRO_GET_ID_INVALIDO = """
            {
              "timestamp": "2026-05-25 17:30:00",
              "status": 400,
              "erro": "Dados inválidos",
              "mensagem": "O ID da simulação deve ser maior que zero.",
              "caminho": "/simulacoes/0",
              "detalhes": [
                "id: deve ser maior que zero"
              ]
            }
            """;

    public static final String ERRO_GET_NAO_ENCONTRADA = """
           {
              "timestamp": "2026-05-25 17:30:00",
              "status": 404,
              "erro": "Recurso não encontrado",
              "mensagem": "Simulação não encontrada com o ID informado.",
              "caminho": "/simulacoes/999",
              "detalhes": []
           }
           """;

    public static final String ERRO_INTERNO_POST = """
            {
              "timestamp": "2026-05-25 17:30:00",
              "status": 500,
              "erro": "Erro interno do servidor",
              "mensagem": "Ocorreu um erro inesperado ao processar a simulação. Tente novamente mais tarde.",
              "caminho": "/simulacoes",
              "detalhes": []
            }
            """;

    public static final String ERRO_INTERNO_GET = """
            {
              "timestamp": "2026-05-25 17:30:00",
              "status": 500,
              "erro": "Erro interno do servidor",
              "mensagem": "Ocorreu um erro inesperado ao consultar a simulação. Tente novamente mais tarde.",
              "caminho": "/simulacoes/1",
              "detalhes": []
            }
            """;
}