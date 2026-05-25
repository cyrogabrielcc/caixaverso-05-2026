package com.cef.ProjetoCaixaversoServicoFinanceiro.resources;

import com.cef.ProjetoCaixaversoServicoFinanceiro.dto.SimulacaoRequestDTO;
import com.cef.ProjetoCaixaversoServicoFinanceiro.dto.SimulacaoResponseDTO;
import com.cef.ProjetoCaixaversoServicoFinanceiro.exception.ErroResponse;
import com.cef.ProjetoCaixaversoServicoFinanceiro.service.SimulacaoService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;

@Path("/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(
        name = "Simulações",
        description = "Endpoints para simular juros compostos, persistir o resultado e consultar simulações existentes."
)
public class SimulacaoResource {

    private final SimulacaoService simulacaoService;

    @Inject
    public SimulacaoResource(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    @POST
    @Operation(
            summary = "Simula e persiste um cálculo de juros compostos",
            description = "Recebe valor inicial, taxa de juros mensal e prazo em meses. Calcula a evolução mês a mês por juros compostos, persiste a simulação no H2 e retorna o resultado completo com ID, totais e memória de cálculo."
    )
    @APIResponse(
            responseCode = "201",
            description = "Simulação calculada e persistida com sucesso.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SimulacaoResponseDTO.class)
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Dados inválidos na requisição ou violação de regra de negócio.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErroResponse.class)
            )
    )
    @APIResponse(
            responseCode = "415",
            description = "Tipo de mídia não suportado. A API aceita requisições com Content-Type application/json.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErroResponse.class)
            )
    )
    @APIResponse(
            responseCode = "500",
            description = "Erro interno inesperado ao processar a simulação.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErroResponse.class)
            )
    )
    public Response simular(
            @RequestBody(
                    description = "Dados necessários para realizar a simulação de juros compostos.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = SimulacaoRequestDTO.class)
                    )
            )
            @Valid SimulacaoRequestDTO requestDTO
    ) {
        SimulacaoResponseDTO simulacao = simulacaoService.simular(requestDTO);

        URI location = UriBuilder
                .fromResource(SimulacaoResource.class)
                .path("/{id}")
                .build(simulacao.getId());

        return Response
                .created(location)
                .entity(simulacao)
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Consulta uma simulação existente",
            description = "Busca uma simulação previamente persistida pelo ID informado na rota. Retorna os parâmetros de entrada, os totais calculados e a memória de cálculo completa associada."
    )
    @APIResponse(
            responseCode = "200",
            description = "Simulação encontrada e retornada com sucesso.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SimulacaoResponseDTO.class)
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "ID inválido. O ID da simulação deve ser maior que zero.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErroResponse.class)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Nenhuma simulação foi encontrada para o ID informado.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErroResponse.class)
            )
    )
    @APIResponse(
            responseCode = "500",
            description = "Erro interno inesperado ao consultar a simulação.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErroResponse.class)
            )
    )
    public Response buscarPorId(
            @Parameter(
                    description = "ID da simulação a ser consultada.",
                    required = true,
                    example = "1"
            )
            @PathParam("id") Long id
    ) {
        SimulacaoResponseDTO simulacao = simulacaoService.buscarPorId(id);
        return Response.ok(simulacao).build();
    }
}