package com.wallet.application;

import com.wallet.domain.Card;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.UUID;

@QuarkusTest
public class AuthAndCardTest {

    @Inject
    CardService cardService;

    @Test
    public void testCardEmissionThrowsExceptionForInvalidAccount() {
        UUID randomAccountId = UUID.randomUUID();
        
        IllegalArgumentException exception = Assertions.assertThrows(
            IllegalArgumentException.class, 
            () -> cardService.issueCard(randomAccountId, Card.CardType.PHYSICAL)
        );
        
        Assertions.assertEquals("Conta de utilizador inválida ou inativa.", exception.getMessage());
    }
}