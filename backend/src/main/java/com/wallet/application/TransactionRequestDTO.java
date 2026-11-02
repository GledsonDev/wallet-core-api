package com.wallet.application;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionRequestDTO {
    public UUID accountId;
    public BigDecimal amount;
    public String type; // DEBIT ou CREDIT
    public String idempotencyKey;
}