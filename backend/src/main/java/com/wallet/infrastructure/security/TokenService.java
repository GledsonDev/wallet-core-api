package com.wallet.infrastructure.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class TokenService {
    public String generateToken(UUID accountId) {
        return Jwt.issuer("wallet-core-api")
                  .upn(accountId.toString())
                  .groups(Set.of("USER"))
                  .expiresIn(3600) // Token válido por 1 hora
                  .sign();
    }
}