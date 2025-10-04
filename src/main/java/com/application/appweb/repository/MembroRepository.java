package com.application.appweb.repository;



import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.appweb.model.Membro;
import org.springframework.stereotype.Repository;

@Repository
public interface MembroRepository extends JpaRepository<Membro, Long> {
    List<Membro> findByDataNascimentoBetween(LocalDate dataNascimentoInicio, LocalDate dataNascimentoFim);
    List<Membro> findByNomeContainingIgnoreCase(String nome);
}
