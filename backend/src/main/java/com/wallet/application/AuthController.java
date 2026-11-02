package com.wallet.application;

import com.wallet.domain.Account;
import com.wallet.infrastructure.security.TokenService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Auth", description = "Autenticação e Geração de Tokens")
public class AuthController {

    @Inject
    AccountService accountService;

    @Inject
    TokenService tokenService;

    @POST
    @Path("/login")
    @Operation(summary = "Autenticar utilizador", description = "Valida CPF e Password e devolve um token JWT.")
    public Response login(LoginRequestDTO dto) {
        Account account = accountService.authenticate(dto.documentCpf, dto.rawPassword);
        if (account == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Credenciais inválidas.").build();
        }
        String token = tokenService.generateToken(account.id);
        return Response.ok("{\"token\": \"" + token + "\"}").build();
    }
}