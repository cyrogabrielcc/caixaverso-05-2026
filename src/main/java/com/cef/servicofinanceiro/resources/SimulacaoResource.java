package com.cef.servicofinanceiro.resources;

import com.cef.servicofinanceiro.dto.SimulacaoRequestDTO;
import com.cef.servicofinanceiro.dto.SimulacaoResponseDTO;
import com.cef.servicofinanceiro.service.SimulacaoService;
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
        description = "Endpoints para simular juros compostos e consultar simulações existentes."
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
            description = "Recebe os dados da operação financeira, calcula a evolução mês a mês dos juros compostos, persiste a simulação no banco de dados e retorna o resultado completo."
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
            description = "Dados inválidos na requisição ou violação de regra de negócio."
    )
    public Response simular(
            @RequestBody(
                    description = "Dados necessários para realizar a simulação de juros compostos.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SimulacaoRequestDTO.class))
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
            description = "Busca uma simulação previamente persistida pelo seu identificador único, retornando os parâmetros de entrada, os totais calculados e a memória de cálculo completa."
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
            responseCode = "404",
            description = "Nenhuma simulação encontrada para o ID informado."
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