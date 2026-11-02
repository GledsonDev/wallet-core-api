package com.wallet.application;

import com.wallet.domain.Account;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@SecurityScheme(
    securitySchemeName = "jwt",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@Path("/api/v1/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Accounts", description = "Gestão de Contas e Perfil")
public class AccountController {

    @Inject
    AccountService accountService;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/register")
    @Operation(summary = "Criar nova conta", description = "Regista um novo utilizador no banco (endpoint público).")
    public Response register(RegisterAccountDTO dto) {
        try {
            Account account = accountService.registerAccount(dto.ownerName, dto.documentCpf, dto.rawPassword);
            return Response.status(Response.Status.CREATED).entity(account).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/me")
    @SecurityRequirement(name = "jwt")
    @RolesAllowed("USER")
    @Operation(summary = "Dados do perfil", description = "Retorna os dados da conta autenticada, incluindo saldo.")
    public Response getMyProfile() {
        UUID authenticatedAccountId = UUID.fromString(jwt.getName());
        Account account = accountService.findById(authenticatedAccountId);
        return Response.ok(account).build();
    }

    @GET
    @Path("/statement")
    @SecurityRequirement(name = "jwt")
    @RolesAllowed("USER")
    @Operation(summary = "Extrato", description = "Retorna o histórico de transações da conta.")
    public Response getStatement() {
        UUID authenticatedAccountId = UUID.fromString(jwt.getName());
        return Response.ok(accountService.getStatement(authenticatedAccountId)).build();
    }
}