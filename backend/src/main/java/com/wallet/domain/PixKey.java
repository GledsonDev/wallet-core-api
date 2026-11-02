package com.wallet.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "pix_keys")
public class PixKey extends PanacheEntityBase {
    @Id
    public UUID id = UUID.randomUUID();
    public UUID accountId;
    
    @Column(unique = true)
    public String keyValue; // Será guardado encriptado
    
    @Enumerated(EnumType.STRING)
    public KeyType keyType;
    
    public enum KeyType { CPF, EMAIL, RANDOM }
}