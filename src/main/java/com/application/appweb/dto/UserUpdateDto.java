package com.application.appweb.dto;

import java.util.Set;

public record UserUpdateDto(
        String username,
        String password,
        Set<String> roleNames
) {}
