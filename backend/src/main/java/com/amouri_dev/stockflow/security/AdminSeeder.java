package com.amouri_dev.stockflow.security;

import com.amouri_dev.stockflow.common.User;
import com.amouri_dev.stockflow.common.UserRole;
import com.amouri_dev.stockflow.common.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Ensures a bootstrap ADMIN account exists so the very first approval/login can happen.
 * Idempotent: skips if the admin email is already present. Credentials are configurable
 * (see stockflow.admin.* properties) and should be overridden via env vars outside dev.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${stockflow.admin.email}")
    private String adminEmail;

    @Value("${stockflow.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        User admin = new User();
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setUserStatus(UserStatus.ACTIVE);
        admin.setEnabled(true);
        admin.setLocked(false);
        admin.setRoles(Set.of(UserRole.ADMIN));

        userRepository.save(admin);
        log.info("Seeded bootstrap ADMIN account: {}", adminEmail);
    }
}
