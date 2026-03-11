package com.davidmoura.secureauth.service;

import com.davidmoura.secureauth.domain.Role;
import com.davidmoura.secureauth.domain.User;
import com.davidmoura.secureauth.dto.CreateUserRequest;
import com.davidmoura.secureauth.dto.UserResponse;
import com.davidmoura.secureauth.repository.RoleRepository;
import com.davidmoura.secureauth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final EmailService emailService;

    public UserService(UserRepository repository, RoleRepository roleRepository, PasswordEncoder encoder, EmailService emailService) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.emailService = emailService;
    }

    @Transactional
    public void grantRole(String email, String roleName) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        user.getRoles().add(role);
        repository.save(user);
    }

    @Transactional
    public void revokeRole(String email, String roleName) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.getRoles().removeIf(r -> r.getName().equals(roleName));
        repository.save(user);
    }

    @Transactional
    public UserResponse create(CreateUserRequest req) {
        if (repository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        String hash = encoder.encode(req.password());
        String verificationToken = java.util.UUID.randomUUID().toString();

        User user = new User(req.name(), req.email(), hash, verificationToken);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not seeded"));
        user.getRoles().add(userRole);

        repository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}