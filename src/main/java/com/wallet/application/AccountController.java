package com.wallet.application;

import com.wallet.domain.Account;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;

@Path("/api/v1/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountController {

    @POST
    @Transactional
    public Response createAccount(Account account) {
        Account newAccount = new Account(account.ownerName, account.documentCpf);
        newAccount.persist();
        return Response.status(201).entity(newAccount).build();
    }

    @GET
    @Path("/{id}")
    public Response getAccount(@PathParam("id") UUID id) {
        return Account.findByIdOptional(id)
                .map(acc -> Response.ok(acc).build())
                .orElse(Response.status(404).build());
    }
}