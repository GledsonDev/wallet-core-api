package com.wallet.infrastructure.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Provider
public class RateLimitingFilter implements ContainerRequestFilter {
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Num cenário real de produção, utiliza-se o cabeçalho X-Forwarded-For
        String ip = requestContext.getUriInfo().getBaseUri().getHost(); 
        Bucket bucket = buckets.computeIfAbsent(ip, this::createNewBucket);
        
        if (!bucket.tryConsume(1)) {
            requestContext.abortWith(Response.status(429).entity("Limite de requisições excedido. Tente novamente mais tarde.").build());
        }
    }

    private Bucket createNewBucket(String key) {
        return Bucket.builder()
            .addLimit(Bandwidth.builder()
                .capacity(10)
                .refillGreedy(10, Duration.ofMinutes(1))
                .build())
            .build();
    }
}