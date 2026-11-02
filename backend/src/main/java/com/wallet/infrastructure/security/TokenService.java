package com.wallet.infrastructure.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class TokenService {
    
    public String generateAccessToken(UUID accountId) {
        return Jwt.issuer("wallet-core-api")
                  .upn(accountId.toString())
                  .groups(Set.of("USER"))
                  .expiresIn(900) // 15 minutos
                  .sign();
    }

    public String generateRefreshToken(UUID accountId) {
        return Jwt.issuer("wallet-core-api")
                  .upn(accountId.toString())
                  .claim("type", "refresh")
                  .expiresIn(86400) // 24 horas
                  .sign();
    }
}