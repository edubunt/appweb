package com.application.appweb.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record MembroRequest(
        @NotBlank(message = "Name is required")
        String nome,

        @NotBlank(message = "Address is required")
        String endereco,

        @NotBlank(message = "Phone is required")
        String telefone,

        @Email(message = "Email should be valid")
        String email,

        LocalDate dataNascimento
) {}
