package com.wallet.application;

import com.wallet.domain.Account;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/api/v1/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Accounts", description = "Gestão de Contas Bancárias")
public class AccountController {

    @Inject
    AccountService accountService;

    @POST
    @Transactional
    @Operation(summary = "Criar nova conta bancária", description = "Regista uma nova conta com CPF encriptado e senha com hash seguro.")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Conta criada com sucesso"),
        @APIResponse(responseCode = "400", description = "Erro de validação ou CPF já existente")
    })
    public Response createAccount(AccountRequestDTO dto) {
        try {
            Account newAccount = accountService.registerAccount(dto.ownerName, dto.documentCpf, dto.password);
            return Response.status(201).entity(newAccount).build();
        } catch (Exception e) {
            return Response.status(400).entity("Erro ao criar conta: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar conta por ID", description = "Retorna os detalhes de uma conta bancária com base no UUID.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Conta encontrada com sucesso"),
        @APIResponse(responseCode = "404", description = "Conta não encontrada")
    })
    public Response getAccount(@PathParam("id") UUID id) {
        return Account.findByIdOptional(id)
                .map(acc -> Response.ok(acc).build())
                .orElse(Response.status(404).build());
    }
}