package com.davidmoura.secureauth.config;

import com.davidmoura.secureauth.domain.Role;
import com.davidmoura.secureauth.domain.User;
import com.davidmoura.secureauth.repository.RoleRepository;
import com.davidmoura.secureauth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            if (!roleRepo.existsByName("ROLE_USER")) roleRepo.save(new Role("ROLE_USER"));
            if (!roleRepo.existsByName("ROLE_ADMIN")) roleRepo.save(new Role("ROLE_ADMIN"));

            String adminEmail = "admin@local";
            if (!userRepo.existsByEmail(adminEmail)) {
                Role userRole = roleRepo.findByName("ROLE_USER").orElseThrow();
                Role adminRole = roleRepo.findByName("ROLE_ADMIN").orElseThrow();

                User admin = new User("Admin", adminEmail, encoder.encode("admin123"));
                admin.getRoles().addAll(Set.of(userRole, adminRole));

                userRepo.save(admin);
            }
        };
    }
}
