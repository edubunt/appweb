package com.application.appweb.dto.response;

import com.application.appweb.enumModel.TipoTransacao;
import com.application.appweb.model.Financeiro;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FinanceiroResponse(
        Long id,
        String descricao,
        BigDecimal valor,
        TipoTransacao tipo,
        LocalDate dataTransacao,
        Long membroId,
        String membroNome
) {
    public static FinanceiroResponse fromFinanceiro(Financeiro financeiro) {
        return new FinanceiroResponse(
                financeiro.getId(),
                financeiro.getDescricao(),
                financeiro.getValor(),
                TipoTransacao.valueOf(financeiro.getTipoRegistro().name()),
                financeiro.getDataRegistro(),
                financeiro.getMembro() != null ? financeiro.getMembro().getId() : null,
                financeiro.getMembro() != null ? financeiro.getMembro().getNome() : null
        );
    }
}
