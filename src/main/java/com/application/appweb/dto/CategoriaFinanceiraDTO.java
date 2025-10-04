package com.application.appweb.dto;

import com.application.appweb.enumModel.TipoTransacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoriaFinanceiraDTO {

    private String nome;
    private TipoTransacao tipo;

}

