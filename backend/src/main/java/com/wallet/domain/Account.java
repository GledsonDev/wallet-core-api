package com.wallet.domain;

import com.wallet.infrastructure.security.EncryptedStringConverter;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(nullable = false)
    public String ownerName;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(nullable = false, unique = true)
    public String documentCpf;

    @Column(nullable = false)
    public String passwordHash;

    @Column(nullable = false)
    public BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public AccountStatus status = AccountStatus.ACTIVE;

    public enum AccountStatus {
        ACTIVE, BLOCKED_SUSPICIOUS, CLOSED
    }

    public Account() {
    }

    public Account(String ownerName, String documentCpf, String passwordHash) {
        this.id = UUID.randomUUID();
        this.ownerName = ownerName;
        this.documentCpf = documentCpf;
        this.passwordHash = passwordHash;
        this.balance = BigDecimal.ZERO;
        this.status = AccountStatus.ACTIVE;
    }
}