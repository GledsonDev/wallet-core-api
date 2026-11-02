package com.wallet.application;

import com.wallet.domain.Account;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class AccountService {

    @Transactional
    public Account registerAccount(String ownerName, String documentCpf, String rawPassword) {
        Account existing = Account.find("documentCpf", documentCpf).firstResult();
        if (existing != null) {
            throw new IllegalArgumentException("Já existe uma conta associada a este CPF.");
        }

        // Gera o hash seguro com jBCrypt
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        Account account = new Account();
        account.ownerName = ownerName;
        account.documentCpf = documentCpf;
        account.passwordHash = hashedPassword;
        account.persist();

        return account;
    }

    public Account authenticate(String documentCpf, String rawPassword) {
        Account account = Account.find("documentCpf", documentCpf).firstResult();
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
}