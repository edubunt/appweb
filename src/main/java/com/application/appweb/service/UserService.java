package com.application.appweb.service;

import com.application.appweb.dto.request.UserRegisterRequest;
import com.application.appweb.dto.response.UserResponse;
import com.application.appweb.enumModel.Role;
import com.application.appweb.exception.InvalidInputException;
import com.application.appweb.exception.UserNotFoundException;
import com.application.appweb.model.User;
import com.application.appweb.repository.UserRepository;
import com.application.appweb.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user with id: {}", id);
        ValidationUtil.validateNotNull(id, "User id");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return UserResponse.fromUser(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        ValidationUtil.validateStringNotEmpty(username, "Username");
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    public UserResponse createUser(UserRegisterRequest userRequest) {
        log.info("Creating new user: {}", userRequest.username());
        ValidationUtil.validateStringNotEmpty(userRequest.username(), "Username");
        ValidationUtil.validateStringNotEmpty(userRequest.password(), "Password");

        if (userRepository.findByUsername(userRequest.username()).isPresent()) {
            throw new InvalidInputException("Username already exists: " + userRequest.username());
        }

        User user = new User();
        user.setUsername(userRequest.username());
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user.setRoles(convertToRoles(userRequest.roles()));

        User saved = userRepository.save(user);
        log.info("User created with id: {}", saved.getId());
        return UserResponse.fromUser(saved);
    }

    public UserResponse updateUser(Long id, UserRegisterRequest updateRequest) {
        log.info("Updating user with id: {}", id);
        ValidationUtil.validateNotNull(id, "User id");

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (updateRequest.username() != null && !updateRequest.username().equals(existingUser.getUsername())) {
            if (userRepository.findByUsername(updateRequest.username()).isPresent()) {
                throw new InvalidInputException("Username already exists: " + updateRequest.username());
            }
            existingUser.setUsername(updateRequest.username());
        }

        if (updateRequest.password() != null && !updateRequest.password().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updateRequest.password()));
        }

        if (updateRequest.roles() != null && !updateRequest.roles().isEmpty()) {
            existingUser.setRoles(convertToRoles(updateRequest.roles()));
        }

        User updated = userRepository.save(existingUser);
        log.info("User with id {} updated successfully", id);
        return UserResponse.fromUser(updated);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        ValidationUtil.validateNotNull(id, "User id");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
        log.info("User with id {} deleted successfully", id);
    }

    @Transactional(readOnly = true)
    public boolean isCurrentUser(Long id) {
        // Este método pode ser expandido para verificar o usuário logado
        return true;
    }

    private Set<Role> convertToRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return Set.of(Role.ROLE_USER);
        }
        return roleNames.stream()
                .map(roleName -> {
                    try {
                        return Role.valueOf(roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName);
                    } catch (IllegalArgumentException ex) {
                        log.warn("Invalid role: {}", roleName);
                        return Role.ROLE_USER;
                    }
                })
                .collect(Collectors.toSet());
    }
}
