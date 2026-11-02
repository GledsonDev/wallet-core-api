package com.wallet.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account extends PanacheEntityBase {

    @Id
    public UUID id;
    
    public String ownerName;
    
    public String documentCpf;
    
    public BigDecimal balance;

    public Account() {
    }

    public Account(String ownerName, String documentCpf) {
        this.id = UUID.randomUUID();
        this.ownerName = ownerName;
        this.documentCpf = documentCpf;
        this.balance = BigDecimal.ZERO;
    }
}