package com.wallet.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog extends PanacheEntityBase {
    @Id
    public UUID id = UUID.randomUUID();
    public UUID accountId;
    public String action;
    public String details;
    public LocalDateTime timestamp = LocalDateTime.now();

    public AuditLog() {}

    public AuditLog(UUID accountId, String action, String details) {
        this.accountId = accountId;
        this.action = action;
        this.details = details;
    }
}