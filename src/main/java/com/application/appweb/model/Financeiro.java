package com.application.appweb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "financeiro")
public class Financeiro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao; // Descrição do registro (ex.: Dízimo, Oferta, Gasto)
    private BigDecimal valor; // Valor em dinheiro

    @Enumerated(EnumType.STRING)
    private TipoRegistro tipoRegistro; // ENTRADA ou SAÍDA

    private LocalDate dataRegistro; // Data do registro

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membro_id", nullable = true)
    @JsonBackReference
    private Membro membro;

    public enum TipoRegistro {
        ENTRADA, SAIDA
    }
}

