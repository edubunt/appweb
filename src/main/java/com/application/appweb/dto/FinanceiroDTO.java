package com.application.appweb.dto;


import com.application.appweb.enumModel.TipoTransacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanceiroDTO {

    private BigDecimal valor;
    private String descricao;
    private TipoTransacao tipo;
    private LocalDate dataTransacao;
    private Long membroId; // ID do membro associado (opcional)
    private Long categoriaId; // ID da categoria associada
}


