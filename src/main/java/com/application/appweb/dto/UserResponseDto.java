package com.application.appweb.dto;

import com.application.appweb.model.User;

import java.util.Set;
import java.util.stream.Collectors;

public record UserResponseDto(
        Long id,
        String username,
        Set<String> roles
) {
    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet())
        );
    }
}
