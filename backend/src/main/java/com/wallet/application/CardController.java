package com.wallet.application;

import com.wallet.domain.Card;
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
@SecurityRequirement(name = "jwt")
@Path("/api/v1/cards")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Cards", description = "Gestão e Emissão de Cartões")
public class CardController {

    @Inject
    JsonWebToken jwt;

    @Inject
    CardService cardService;

    @POST
    @RolesAllowed("USER")
    @Operation(summary = "Emitir novo cartão")
    public Response issueCard(CardRequestDTO dto) {
        try {
            UUID authenticatedAccountId = UUID.fromString(jwt.getName());
            Card card = cardService.issueCard(authenticatedAccountId, dto.type);
            return Response.status(Response.Status.CREATED).entity(card).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @RolesAllowed("USER")
    @Operation(summary = "Listar meus cartões")
    public Response listMyCards() {
        UUID authenticatedAccountId = UUID.fromString(jwt.getName());
        return Response.ok(cardService.listCardsByAccount(authenticatedAccountId)).build();
    }

    @PATCH
    @Path("/{cardId}/block")
    @RolesAllowed("USER")
    @Operation(summary = "Bloquear cartão")
    public Response blockCard(@PathParam("cardId") UUID cardId) {
        try {
            // Futuro: Validar se o cartão pertence ao utilizador logado
            Card card = cardService.blockCard(cardId);
            return Response.ok(card).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{cardId}/reveal-cvv")
    @RolesAllowed("USER")
    @Operation(summary = "Revelar CVV do cartão original")
    public Response revealCvv(@PathParam("cardId") UUID cardId) {
        try {
            String decryptedCvv = cardService.revealCvv(cardId);
            return Response.ok("{\"cvv\": \"" + decryptedCvv + "\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao processar requisição").build();
        }
    }
}