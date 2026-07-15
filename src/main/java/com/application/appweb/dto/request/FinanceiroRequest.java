package com.application.appweb.dto.request;

import com.application.appweb.enumModel.TipoTransacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinanceiroRequest(
        @NotBlank(message = "Description is required")
        String descricao,

        @NotNull(message = "Value is required")
        @DecimalMin(value = "0.01", message = "Value must be greater than 0")
        BigDecimal valor,

        @NotNull(message = "Type is required")
        TipoTransacao tipo,

        @NotNull(message = "Transaction date is required")
        LocalDate dataTransacao,

        Long membroId
) {}
