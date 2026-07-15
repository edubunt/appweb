package com.application.appweb.service;

import com.application.appweb.dto.request.MembroRequest;
import com.application.appweb.exception.InvalidInputException;
import com.application.appweb.exception.MembroNotFoundException;
import com.application.appweb.model.Membro;
import com.application.appweb.repository.MembroRepository;
import com.application.appweb.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
@Slf4j
public class MembroService {

    private final MembroRepository membroRepository;

    public MembroService(MembroRepository membroRepository) {
        this.membroRepository = membroRepository;
    }

    @Transactional(readOnly = true)
    public List<Membro> getAllMembros() {
        log.debug("Fetching all members");
        return membroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Membro findMembroById(Long id) {
        log.debug("Fetching member with id: {}", id);
        ValidationUtil.validateNotNull(id, "Member id");
        return membroRepository.findById(id)
                .orElseThrow(() -> new MembroNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Membro findMembroWithRegistrosFinanceiros(Long id) {
        log.debug("Fetching member with financial records, id: {}", id);
        Membro membro = findMembroById(id);
        Hibernate.initialize(membro.getRegistrosFinanceiros());
        return membro;
    }

    public Membro creatMembro(MembroRequest membroRequest) {
        log.info("Creating new member: {}", membroRequest.nome());
        ValidationUtil.validateNotNull(membroRequest, "Member request");

        Membro membro = new Membro();
        membro.setNome(membroRequest.nome());
        membro.setEndereco(membroRequest.endereco());
        membro.setTelefone(membroRequest.telefone());
        membro.setEmail(membroRequest.email());
        membro.setDataNascimento(membroRequest.dataNascimento());

        Membro saved = membroRepository.save(membro);
        log.info("Member created with id: {}", saved.getId());
        return saved;
    }

    public Membro updateMembro(Long id, MembroRequest membroRequest) {
        log.info("Updating member with id: {}", id);
        ValidationUtil.validateNotNull(membroRequest, "Member request");

        Membro membro = findMembroById(id);
        membro.setNome(membroRequest.nome());
        membro.setEndereco(membroRequest.endereco());
        membro.setTelefone(membroRequest.telefone());
        membro.setEmail(membroRequest.email());
        membro.setDataNascimento(membroRequest.dataNascimento());

        Membro updated = membroRepository.save(membro);
        log.info("Member with id {} updated successfully", id);
        return updated;
    }

    public void deleteMembro(Long id) {
        log.info("Deleting member with id: {}", id);
        Membro membro = findMembroById(id);
        membroRepository.delete(membro);
        log.info("Member with id {} deleted successfully", id);
    }

    @Transactional(readOnly = true)
    public List<Membro> getMembrosByIdade(int idadeMinima, int idadeMaxima) {
        log.debug("Fetching members between ages {} and {}", idadeMinima, idadeMaxima);
        if (idadeMinima < 0 || idadeMaxima < 0 || idadeMinima > idadeMaxima) {
            throw new InvalidInputException("Invalid age range provided");
        }

        LocalDate hoje = LocalDate.now();
        LocalDate dataNascimentoMinima = hoje.minus(idadeMaxima, ChronoUnit.YEARS);
        LocalDate dataNascimentoMaxima = hoje.minus(idadeMinima, ChronoUnit.YEARS);

        return membroRepository.findByDataNascimentoBetween(dataNascimentoMinima, dataNascimentoMaxima);
    }

    @Transactional(readOnly = true)
    public List<Membro> getMembrosByNome(String nome) {
        log.debug("Searching members by name: {}", nome);
        ValidationUtil.validateStringNotEmpty(nome, "Member name");

        List<Membro> membros = membroRepository.findByNomeContainingIgnoreCase(nome.trim());
        if (membros.isEmpty()) {
            throw new MembroNotFoundException("No members found with name: " + nome);
        }
        return membros;
    }
}
