package com.wallet.infrastructure;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import java.util.UUID;

@ApplicationScoped
public class TransactionProducer {
    
    @Channel("transaction-out")
    Emitter<String> emitter;

    public void sendTransactionToQueue(UUID transactionId) {
        emitter.send(transactionId.toString());
    }
}