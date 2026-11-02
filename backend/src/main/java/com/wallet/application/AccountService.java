package com.wallet.application;

import com.wallet.domain.Account;
import com.wallet.domain.Transaction;
import com.wallet.infrastructure.security.CryptoUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.util.UUID;

@ApplicationScoped
public class AccountService {

    @Inject
    CryptoUtils cryptoUtils;

    @Transactional
    public Account registerAccount(String ownerName, String documentCpf, String rawPassword) {
        String encryptedCpf = cryptoUtils.encrypt(documentCpf);
        Account existing = Account.find("documentCpf", encryptedCpf).firstResult();
        if (existing != null) {
            throw new IllegalArgumentException("Já existe uma conta associada a este CPF.");
        }

        // Gera o hash seguro com jBCrypt
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        Account account = new Account();
        account.ownerName = ownerName;
        // CORREÇÃO: Salvar a versão encriptada no banco
        account.documentCpf = encryptedCpf; 
        account.passwordHash = hashedPassword;
        account.persist();

        return account;
    }

    public Account authenticate(String documentCpf, String rawPassword) {
        String encryptedCpf = cryptoUtils.encrypt(documentCpf);
        Account account = Account.find("documentCpf", encryptedCpf).firstResult();
        if (account == null) {
            return null;
        }

        // Valida a senha comparando com o hash do banco
        boolean matches = BCrypt.checkpw(rawPassword, account.passwordHash);
        if (!matches || account.status != Account.AccountStatus.ACTIVE) {
            return null;
        }

        return account;
    }

    @Transactional
    public Transaction executePixTransfer(UUID sourceAccountId, String destinationCpf, BigDecimal amount, String idempotencyKey) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser maior que zero.");
        }

        // 1. Validar idempotência
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Transaction existingTx = Transaction.find("idempotencyKey", idempotencyKey).firstResult();
            if (existingTx != null) {
                throw new IllegalArgumentException("Transação já processada anteriormente (Idempotência).");
            }
        }

        // 2. Buscar conta de origem
        Account sourceAccount = Account.findById(sourceAccountId);
        if (sourceAccount == null || sourceAccount.status != Account.AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Conta de origem inválida ou inativa.");
        }

        // 3. Validar saldo
        if (sourceAccount.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar a transferência.");
        }

        // 4. Buscar conta de destino pelo CPF (CORREÇÃO: Encriptar antes de buscar)
        String encryptedDestCpf = cryptoUtils.encrypt(destinationCpf);
        Account destinationAccount = Account.find("documentCpf", encryptedDestCpf).firstResult();
        if (destinationAccount == null || destinationAccount.status != Account.AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Conta de destino não encontrada ou inativa.");
        }

        if (sourceAccount.id.equals(destinationAccount.id)) {
            throw new IllegalArgumentException("Não é permitido efetuar uma transferência Pix para a mesma conta.");
        }

        // 5. Executar a movimentação financeira
        sourceAccount.balance = sourceAccount.balance.subtract(amount);
        destinationAccount.balance = destinationAccount.balance.add(amount);

        sourceAccount.persist();
        destinationAccount.persist();

        // 6. Registar débito na origem
        Transaction debitTransaction = new Transaction(sourceAccountId, amount, "DEBIT", idempotencyKey);
        debitTransaction.status = "COMPLETED";
        debitTransaction.persist();

        // 7. Registar crédito no destino
        Transaction creditTransaction = new Transaction(destinationAccount.id, amount, "CREDIT", idempotencyKey != null ? idempotencyKey + "-CREDIT" : null);
        creditTransaction.status = "COMPLETED";
        creditTransaction.persist();

        return debitTransaction;
    }

    public java.util.List<Transaction> getStatement(UUID accountId) {
        return Transaction.list("accountId", accountId);
    }

    public Account findById(UUID id) {
        return Account.findById(id);
    }
}
