package com.application.appweb.dto.response;

import com.application.appweb.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        Long id,
        String username,
        Set<String> roles,
        String token
) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()),
                null
        );
    }

    public static UserResponse fromUserWithToken(User user, String token) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()),
                token
        );
    }
}
