package com.application.appweb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UserRegistrationDto(
        @NotBlank String username,
        @Size(min = 8) String password,
        Set<String> roleNames
) {}
