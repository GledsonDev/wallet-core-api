package com.wallet.infrastructure;

import com.wallet.domain.Account;
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
        if (tx == null || !"PENDING".equals(tx.status)) return;

        Account account = Account.findById(tx.accountId);
        if (account == null) {
            tx.status = "FAILED";
            tx.persist();
            return;
        }

        if ("DEBIT".equals(tx.type)) {
            if (account.balance.compareTo(tx.amount) < 0) {
                tx.status = "FAILED"; // Saldo insuficiente
            } else {
                account.balance = account.balance.subtract(tx.amount);
                tx.status = "COMPLETED";
            }
        } else if ("CREDIT".equals(tx.type)) {
            account.balance = account.balance.add(tx.amount);
            tx.status = "COMPLETED";
        }
        
        account.persist();
        tx.persist();
        System.out.println("[WORKER] Transação processada. Novo saldo: " + account.balance);
    }
}