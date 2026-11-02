package com.wallet.application;

import com.wallet.infrastructure.security.CryptoUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

@QuarkusTest
public class AccountServiceTest {

    @Inject
    AccountService accountService;

    @InjectMock
    CryptoUtils cryptoUtils;

    @Test
    public void testPixTransferThrowsExceptionWhenAmountIsZero() {
        UUID sourceId = UUID.randomUUID();
        
        IllegalArgumentException exception = Assertions.assertThrows(
            IllegalArgumentException.class, 
            () -> accountService.executePixTransfer(sourceId, "meu@email.com", BigDecimal.ZERO, "pix-001")
        );
        
        Assertions.assertEquals("O valor da transferência deve ser maior que zero.", exception.getMessage());
    }
}