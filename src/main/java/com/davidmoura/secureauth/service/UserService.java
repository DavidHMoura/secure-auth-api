package com.davidmoura.secureauth.service;

import com.davidmoura.secureauth.domain.Role;
import com.davidmoura.secureauth.domain.User;
import com.davidmoura.secureauth.dto.CreateUserRequest;
import com.davidmoura.secureauth.dto.UserResponse;
import com.davidmoura.secureauth.repository.RoleRepository;
import com.davidmoura.secureauth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    public void grantRole(String email, String roleName) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        user.getRoles().add(role);
        repository.save(user);
    }

    public void revokeRole(String email, String roleName) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.getRoles().removeIf(r -> r.getName().equals(roleName));
        repository.save(user);
    }


    public UserResponse create(CreateUserRequest req) {
        if (repository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        String hash = encoder.encode(req.password());

        User user = new User(req.name(), req.email(), hash);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not seeded"));
        user.getRoles().add(userRole);

        repository.save(user);


        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
