package com.wallet.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cards")
public class Card extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(nullable = false)
    public UUID accountId;

    @Column(nullable = false, length = 19)
    public String cardNumberMasked;

    @Column(nullable = false)
    public String encryptedCvv; // Armazenado com AES-256

    @Column(nullable = false)
    public LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public CardType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public CardStatus status;

    public enum CardType {
        PHYSICAL, VIRTUAL
    }

    public enum CardStatus {
        ACTIVE, BLOCKED, CANCELLED
    }
}