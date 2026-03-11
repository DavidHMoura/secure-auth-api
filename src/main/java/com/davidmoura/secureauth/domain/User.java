package com.davidmoura.secureauth.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(name = "verification_token", length = 64)
    private String verificationToken;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    protected User() { }

    public User(String name, String email, String passwordHash, String verificationToken) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.verificationToken = verificationToken;
        this.verified = false;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isVerified() { return verified; }
    public String getVerificationToken() { return verificationToken; }
    public Instant getCreatedAt() { return createdAt; }
    public Set<Role> getRoles() { return roles; }

    public void verify() {
        this.verified = true;
        this.verificationToken = null;
    }
}