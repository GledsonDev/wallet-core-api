package com.wallet.application;

import com.wallet.domain.PixKey;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PixKeyRequestDTO {
    @NotBlank(message = "O valor da chave é obrigatório.")
    public String keyValue;
    
    @NotNull(message = "O tipo de chave é obrigatório.")
    public PixKey.KeyType keyType;
}