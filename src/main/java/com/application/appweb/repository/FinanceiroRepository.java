package com.application.appweb.repository;

import com.application.appweb.enumModel.TipoTransacao;
import com.application.appweb.model.Financeiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinanceiroRepository extends JpaRepository<Financeiro, Long> {

    /**
     * Find financial records between dates
     */
    List<Financeiro> findByDataRegistroBetween(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Find financial records by type and date range
     */
    List<Financeiro> findByTipoRegistroAndDataRegistroBetween(
            TipoTransacao tipoRegistro,
            LocalDate dataInicio,
            LocalDate dataFim
    );

    /**
     * Find financial records by member id
     */
    List<Financeiro> findByMembroId(Long membroId);

    /**
     * Find financial records by member name (case insensitive)
     */
    @Query("SELECT f FROM Financeiro f JOIN f.membro m WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Financeiro> findByMembroNomeContainingIgnoreCase(@Param("nome") String nome);
}
