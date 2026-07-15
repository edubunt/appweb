package com.application.appweb.repository;

import com.application.appweb.model.Membro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MembroRepository extends JpaRepository<Membro, Long> {

    /**
     * Find members by birth date range
     */
    List<Membro> findByDataNascimentoBetween(LocalDate dataNascimentoInicio, LocalDate dataNascimentoFim);

    /**
     * Find members by name (case insensitive)
     */
    List<Membro> findByNomeContainingIgnoreCase(String nome);
}
