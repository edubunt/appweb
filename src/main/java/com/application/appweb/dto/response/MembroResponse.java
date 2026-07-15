package com.application.appweb.dto.response;

import com.application.appweb.model.Membro;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MembroResponse(
        Long id,
        String nome,
        String endereco,
        String telefone,
        String email,
        LocalDate dataNascimento,
        Integer idade
) {
    public static MembroResponse fromMembro(Membro membro) {
        int idade = calculateAge(membro.getDataNascimento());
        return new MembroResponse(
                membro.getId(),
                membro.getNome(),
                membro.getEndereco(),
                membro.getTelefone(),
                membro.getEmail(),
                membro.getDataNascimento(),
                idade
        );
    }

    private static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        LocalDate today = LocalDate.now();
        return today.getYear() - birthDate.getYear() -
                (today.getMonthValue() < birthDate.getMonthValue() ||
                 today.getMonthValue() == birthDate.getMonthValue() && today.getDayOfMonth() < birthDate.getDayOfMonth() ? 1 : 0);
    }
}
