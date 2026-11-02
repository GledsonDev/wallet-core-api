package com.wallet.application;

import com.wallet.domain.Transaction;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@SecurityScheme(
    securitySchemeName = "jwt",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@SecurityRequirement(name = "jwt")
@Path("/api/v1/pix")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Pix", description = "Módulo de Transferências Instantâneas")
public class PixController {

    @Inject
    JsonWebToken jwt;

    @Inject
    AccountService accountService;

    @POST
    @Path("/keys")
    @RolesAllowed("USER")
    @Operation(summary = "Registar Chave Pix", description = "Associa uma nova chave Pix à conta autenticada.")
    public Response registerKey(PixKeyRequestDTO dto) {
        try {
            UUID authenticatedAccountId = UUID.fromString(jwt.getName());
            com.wallet.domain.PixKey pixKey = accountService.registerPixKey(authenticatedAccountId, dto.keyValue, dto.keyType);
            return Response.status(Response.Status.CREATED).entity(pixKey).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @RolesAllowed("USER")
    @Operation(summary = "Realizar transferência Pix", description = "Efetua transferência atômica utilizando o CPF de destino.")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Transferência realizada com sucesso"),
        @APIResponse(responseCode = "400", description = "Erro de validação, saldo insuficiente ou chave de idempotência repetida")
    })
    public Response transfer(PixRequestDTO dto) {
        try {
            UUID authenticatedAccountId = UUID.fromString(jwt.getName());

            Transaction transaction = accountService.executePixTransfer(
                authenticatedAccountId, 
                dto.destinationCpf, 
                dto.amount,
                dto.idempotencyKey
            );
            return Response.status(Response.Status.CREATED).entity(transaction).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro interno ao processar o Pix: " + e.getMessage()).build();
        }
    }
}