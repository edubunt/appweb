package com.application.appweb.repository;


import com.application.appweb.model.Financeiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinanceiroRepository extends JpaRepository<Financeiro, Long> {
    List<Financeiro> findByDataRegistroBetween(LocalDate dataInicio, LocalDate dataFim);
    List<Financeiro> findByTipoRegistroAndDataRegistroBetween(Financeiro.TipoRegistro tipoRegistro, LocalDate dataInicio, LocalDate dataFim);
    List<Financeiro> findByMembroId(Long membroId);
    List<Financeiro> findByMembroNomeContainingIgnoreCase(String nome);
}

