package com.example.tiamedsadmin.config;

import com.example.tiamedsadmin.entity.auth.Role;
import com.example.tiamedsadmin.entity.auth.User;
import com.example.tiamedsadmin.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates the first SUPER_ADMIN on startup so login is possible on a fresh database.
 * Credentials come from ADMIN_SEED_EMAIL / ADMIN_SEED_PASSWORD env vars.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin-seed.email}")
    private String email;

    @Value("${app.admin-seed.password}")
    private String password;

    @Value("${app.admin-seed.full-name}")
    private String fullName;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User admin = new User();
        admin.setFullName(fullName);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(Role.SUPER_ADMIN);
        admin.setActive(true);
        userRepository.save(admin);

        log.info("Seeded SUPER_ADMIN user: {}", email);
    }
}
