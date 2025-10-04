package com.application.appweb.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "membros")
public class Membro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String endereco;
    private String telefone;
    private String email;
    private LocalDate dataNascimento;

    @OneToMany(mappedBy = "membro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference //
    private List<Financeiro> registrosFinanceiros;

}
