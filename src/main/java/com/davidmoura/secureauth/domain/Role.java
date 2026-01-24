package com.davidmoura.secureauth.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table
public class Role {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // "ROLE_USER", "ROLE_ADMIN"

    protected Role() {}

    public Role(String name) {
        this.name = name;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
}