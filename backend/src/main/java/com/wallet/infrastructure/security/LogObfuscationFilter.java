package com.wallet.infrastructure.security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Provider
public class LogObfuscationFilter implements ContainerRequestFilter {
    
    private static final Logger LOG = Logger.getLogger(LogObfuscationFilter.class);

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        if (context.hasEntity()) {
            byte[] entity = context.getEntityStream().readAllBytes();
            String payload = new String(entity, StandardCharsets.UTF_8);
            
            String safePayload = payload.replaceAll("\"rawPassword\"\\s*:\\s*\"[^\"]+\"", "\"rawPassword\":\"***\"")
                                        .replaceAll("\"documentCpf\"\\s*:\\s*\"[^\"]+\"", "\"documentCpf\":\"***\"");
            
            LOG.info("Endpoint: " + context.getUriInfo().getPath() + " | Payload Seguro: " + safePayload);
            
            // Restaura o stream para que o Quarkus consiga processar o JSON normalmente
            context.setEntityStream(new ByteArrayInputStream(entity));
        }
    }
}