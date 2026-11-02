package com.wallet.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction extends PanacheEntityBase {

    @Id
    public UUID id;
    
    public UUID accountId;
    
    public BigDecimal amount;
    
    public String type; // DEBIT ou CREDIT
    
    public String status; // PENDING, COMPLETED, FAILED
    
    public String idempotencyKey;
    
    public Instant createdAt;

    public Transaction() {
    }

    public Transaction(UUID accountId, BigDecimal amount, String type, String idempotencyKey) {
        this.id = UUID.randomUUID();
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.status = "PENDING";
        this.idempotencyKey = idempotencyKey;
        this.createdAt = Instant.now();
    }
}