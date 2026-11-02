package com.wallet.infrastructure;

import com.wallet.domain.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import java.util.UUID;

@ApplicationScoped
public class TransactionWorker {

    @Incoming("transaction-in")
    @Transactional
    public void processTransaction(String transactionId) {
        Transaction tx = Transaction.findById(UUID.fromString(transactionId));
        
        if (tx != null && "PENDING".equals(tx.status)) {
            // Em um cenário completo, buscaríamos a Account para validar o saldo aqui
            tx.status = "COMPLETED";
            tx.persist();
            System.out.println("[WORKER] Transação processada e consolidada: " + transactionId);
        }
    }
}