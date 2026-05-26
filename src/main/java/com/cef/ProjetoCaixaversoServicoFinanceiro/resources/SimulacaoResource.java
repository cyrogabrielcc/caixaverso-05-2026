package com.cef.ProjetoCaixaversoServicoFinanceiro.resources;

import com.cef.ProjetoCaixaversoServicoFinanceiro.dto.SimulacaoRequestDTO;
import com.cef.ProjetoCaixaversoServicoFinanceiro.dto.SimulacaoResponseDTO;
import com.cef.ProjetoCaixaversoServicoFinanceiro.exception.ErroResponseDTO;
import com.cef.ProjetoCaixaversoServicoFinanceiro.openapi.OpenApiExamples;
import com.cef.ProjetoCaixaversoServicoFinanceiro.service.SimulacaoService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
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
            description = "Recebe os parâmetros da simulação, calcula a evolução mês a mês, persiste o resultado no banco de dados e retorna a simulação completa."
    )
    @APIResponse(
            responseCode = "201",
            description = "Simulação calculada, persistida e retornada com sucesso.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SimulacaoResponseDTO.class),
                    examples = @ExampleObject(
                            name = "Simulação criada",
                            value = OpenApiExamples.SIMULACAO_RESPONSE
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Dados inválidos na requisição ou JSON malformado.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErroResponseDTO.class),
                    examples = {
                            @ExampleObject(
                                    name = "Erro de validação",
                                    value = OpenApiExamples.ERRO_POST_VALIDACAO
                            ),
                            @ExampleObject(
                                    name = "Campo numérico com texto",
                                    value = OpenApiExamples.ERRO_POST_JSON_INVALIDO
                            )
                    }
            )
    )
    @APIResponse(
            responseCode = "500",
            description = "Erro interno inesperado ao processar a simulação.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErroResponseDTO.class),
                    examples = @ExampleObject(
                            name = "Erro interno",
                            value = OpenApiExamples.ERRO_INTERNO_POST
                    )
            )
    )
    public Response simular(
            @RequestBody(
                    description = "Dados necessários para realizar a simulação de juros compostos.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SimulacaoRequestDTO.class))
            )
            @Valid SimulacaoRequestDTO requestDTO,

            @Context UriInfo uriInfo
    ) {
        SimulacaoResponseDTO responseDTO = simulacaoService.simular(requestDTO);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(responseDTO.getId().toString())
                .build();

        return Response.created(location)
                .entity(responseDTO)
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
                    schema = @Schema(implementation = SimulacaoResponseDTO.class),
                    examples = @ExampleObject(
                            name = "Simulação encontrada",
                            value = OpenApiExamples.SIMULACAO_RESPONSE
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "ID inválido. O ID da simulação deve ser maior que zero.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErroResponseDTO.class),
                    examples = @ExampleObject(
                            name = "ID inválido",
                            value = OpenApiExamples.ERRO_GET_ID_INVALIDO
                    )
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Nenhuma simulação foi encontrada para o ID informado.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErroResponseDTO.class),
                    examples = @ExampleObject(
                            name = "Simulação não encontrada",
                            value = OpenApiExamples.ERRO_GET_NAO_ENCONTRADA
                    )
            )
    )
    @APIResponse(
            responseCode = "500",
            description = "Erro interno inesperado ao consultar a simulação.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErroResponseDTO.class),
                    examples = @ExampleObject(
                            name = "Erro interno",
                            value = OpenApiExamples.ERRO_INTERNO_GET
                    )
            )
    )
    public Response buscarPorId(
            @Parameter(
                    description = "ID da simulação a ser consultada.",
                    required = true,
                    example = "1"
            )
            @PathParam("id")
            @Min(value = 1, message = "O ID da simulação deve ser maior que zero.")
            Long id
    ) {
        SimulacaoResponseDTO responseDTO = simulacaoService.buscarPorId(id);
        return Response.ok(responseDTO).build();
    }
}