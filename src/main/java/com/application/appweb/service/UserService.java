package com.application.appweb.service;

import com.application.appweb.dto.LoginRequest;
import com.application.appweb.dto.UserResponseDto;
import com.application.appweb.dto.UserUpdateDto;
import com.application.appweb.enumModel.Role;
import com.application.appweb.exception.UserNotFoundException;
import com.application.appweb.model.User;
import com.application.appweb.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Buscar todos os usuários
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDto::fromEntity)
                .toList();
    }

    // Buscar usuário por ID
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return UserResponseDto.fromEntity(user);
    }

    // Criar novo usuário
    public UserResponseDto createUser(LoginRequest userDto, Set<String> roleNames) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new UserNotFoundException("Username already exists");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(convertToRoles(roleNames));

        User savedUser = userRepository.save(user);
        return UserResponseDto.fromEntity(savedUser);
    }

    // Atualizar usuário existente
    public UserResponseDto updateUser(Long id, UserUpdateDto updateDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        Optional.ofNullable(updateDto.username()).ifPresent(existingUser::setUsername);
        Optional.ofNullable(updateDto.password())
                .map(passwordEncoder::encode)
                .ifPresent(existingUser::setPassword);
        Optional.ofNullable(updateDto.roleNames())
                .map(this::convertToRoles)
                .ifPresent(existingUser::setRoles);

        User updatedUser = userRepository.save(existingUser);
        return UserResponseDto.fromEntity(updatedUser);
    }

    // Deletar usuário
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    // Método auxiliar para converter nomes de papéis em objetos Role
    private Set<Role> convertToRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return Set.of(Role.ROLE_USER); // Valor padrão
        }
        return roleNames.stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}

