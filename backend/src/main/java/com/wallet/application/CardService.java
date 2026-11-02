package com.wallet.application;

import com.wallet.domain.Account;
import com.wallet.domain.Card;
import com.wallet.infrastructure.security.CryptoUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@ApplicationScoped
public class CardService {

    @Inject
    CryptoUtils cryptoUtils;

    @Transactional
    public Card issueCard(UUID accountId, Card.CardType type) {
        Account account = Account.findById(accountId);
        if (account == null || account.status != Account.AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Conta de utilizador inválida ou inativa.");
        }

        Card card = new Card();
        card.accountId = accountId;
        card.type = type;
        card.status = Card.CardStatus.ACTIVE;
        card.expirationDate = LocalDate.now().plusYears(5);

        Random random = new Random();
        String last4 = String.format("%04d", random.nextInt(10000));
        card.cardNumberMasked = "**** **** **** " + last4;

        // Gera e encripta o CVV com AES-256
        String rawCvv = String.format("%03d", random.nextInt(1000));
        card.encryptedCvv = cryptoUtils.encrypt(rawCvv);

        card.persist();
        return card;
    }

    public List<Card> listCardsByAccount(UUID accountId) {
        return Card.list("accountId", accountId);
    }

    public Card findById(UUID cardId) {
        return Card.findById(cardId);
    }

    @Transactional
    public Card blockCard(UUID cardId) {
        Card card = findById(cardId);
        if (card == null) {
            throw new IllegalArgumentException("Cartão não encontrado.");
        }
        card.status = Card.CardStatus.BLOCKED;
        card.persist();
        return card;
    }

    @Transactional
    public Card unblockCard(UUID cardId) {
        Card card = findById(cardId);
        if (card == null) {
            throw new IllegalArgumentException("Cartão não encontrado.");
        }
        if (card.status == Card.CardStatus.CANCELLED) {
            throw new IllegalArgumentException("Não é possível desbloquear um cartão cancelado.");
        }
        card.status = Card.CardStatus.ACTIVE;
        card.persist();
        return card;
    }

    public String revealCvv(UUID cardId) {
        Card card = findById(cardId);
        if (card == null || card.status != Card.CardStatus.ACTIVE) {
            throw new IllegalArgumentException("Cartão inválido ou inativo.");
        }
        // Descriptografa o CVV usando AES-256
        return cryptoUtils.decrypt(card.encryptedCvv);
    }
}