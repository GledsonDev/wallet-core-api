package com.wallet.infrastructure;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IdempotencyService {
    private final ValueCommands<String, String> valueCommands;

    @SuppressWarnings("null") //apenas para ignorar o aviso por enquanto
    public IdempotencyService(RedisDataSource ds) {
        this.valueCommands = ds.value(String.class);
    }

    public boolean isDuplicate(String idempotencyKey) {
        String key = "idemp:" + idempotencyKey;
        if (valueCommands.get(key) != null) {
            return true;
        }
        // Registra a chave no cache com validade de 24h (86400 segundos)
        valueCommands.setex(key, 86400, "processed");
        return false;
    }
}