package com.wallet.application;

import com.wallet.domain.Account;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    AccountService accountService;

    @POST
    @Path("/login")
    public Response login(LoginRequestDTO dto) {
        Account account = accountService.authenticate(dto.documentCpf, dto.password);
        
        if (account == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity("Credenciais inválidas ou conta inativa.")
                           .build();
        }

        // Retorna os dados da conta autenticada (em breve, encapsulado num Token JWT)
        return Response.ok(account).build();
    }
}