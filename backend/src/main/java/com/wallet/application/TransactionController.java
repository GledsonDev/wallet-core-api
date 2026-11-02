package com.wallet.application;

import com.wallet.domain.Transaction;
import com.wallet.infrastructure.IdempotencyService;
import com.wallet.infrastructure.TransactionProducer;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/api/v1/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionController {

    @Inject
    IdempotencyService idempotencyService;
    
    @Inject
    TransactionProducer producer;

    @POST
    @Transactional
    public Response createTransaction(TransactionRequestDTO dto) {
        // 1. Barrar duplicidade em milissegundos via Redis
        if (idempotencyService.isDuplicate(dto.idempotencyKey)) {
            return Response.status(409).entity("Transação duplicada interceptada pelo Cache.").build();
        }

        // 2. Salvar transação no banco relacional como PENDENTE
        Transaction tx = new Transaction(dto.accountId, dto.amount, dto.type, dto.idempotencyKey);
        tx.persist();

        // 3. Enviar evento para processamento assíncrono
        producer.sendTransactionToQueue(tx.id);

        // 4. Retornar 202 Accepted (Em processamento)
        return Response.status(202).entity(tx).build();
    }

    @GET
    public Response getTransactions(@QueryParam("accountId") UUID accountId) {
        // Buscar transações ordenadas da mais recente para a mais antiga usando Panache
        List<Transaction> transactions = Transaction.find("accountId = ?1 order by createdAt desc", accountId).list();
        
        return Response.ok(transactions).build();
    }
}