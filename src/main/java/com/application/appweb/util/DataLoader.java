package com.application.appweb.util;


import com.application.appweb.enumModel.Role;
import com.application.appweb.model.User;
import com.application.appweb.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
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
        // Verifica se já existem usuários no banco de dados
        if (userRepository.count() == 0) {

            // Criar usuário padrão "user"
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("12345678"));
            user.setRoles(Set.of(Role.ROLE_USER));
            userRepository.save(user);

            // Criar usuário administrador "admin"
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(Role.ROLE_ADMIN));
            userRepository.save(admin);

            // Criar usuário "membro" com papel PASTOR
            User membro = new User();
            membro.setUsername("membro");
            membro.setPassword(passwordEncoder.encode("membro123"));
            membro.setRoles(Set.of(Role.ROLE_PASTOR));
            userRepository.save(membro);

            System.out.println("✅ Dados iniciais criados com sucesso!");
        } else {
            System.out.println("ℹ️  Dados iniciais já existem no banco de dados.");
        }
    }
}

