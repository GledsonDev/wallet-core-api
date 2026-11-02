package com.wallet.application;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PixRequestDTO {
    @NotBlank(message = "O CPF de destino é obrigatório.")
    public String destinationCpf;
    
    @NotNull(message = "O valor da transferência é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser superior a zero.")
    public BigDecimal amount;
    
    @NotBlank(message = "A chave de idempotência é obrigatória.")
    public String idempotencyKey;
}