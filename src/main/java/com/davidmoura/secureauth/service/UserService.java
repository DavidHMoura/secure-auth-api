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
    private final RoleRepository roleRepository; // ✅ novo
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.repository = repository;
        this.roleRepository = roleRepository; // ✅ novo
        this.encoder = encoder;
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
