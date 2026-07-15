package com.application.appweb.util;

import com.application.appweb.model.User;
import com.application.appweb.repository.UserRepository;
import com.application.appweb.enumModel.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Loading initial data...");

            // Create admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(Role.ROLE_ADMIN));
            userRepository.save(admin);
            log.info("✅ Admin user created");

            // Create regular user
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRoles(Set.of(Role.ROLE_USER));
            userRepository.save(user);
            log.info("✅ Regular user created");

            // Create pastor user
            User pastor = new User();
            pastor.setUsername("pastor");
            pastor.setPassword(passwordEncoder.encode("pastor123"));
            pastor.setRoles(Set.of(Role.ROLE_PASTOR, Role.ROLE_USER));
            userRepository.save(pastor);
            log.info("✅ Pastor user created");

            log.info("✅ Initial data loaded successfully!");
        } else {
            log.info("ℹ️ Database already contains data. Skipping initial data load.");
        }
    }
}
