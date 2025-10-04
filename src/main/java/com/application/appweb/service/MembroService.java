package com.application.appweb.service;

import java.time.LocalDate;
import java.util.List;
import java.time.temporal.ChronoUnit;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.application.appweb.exception.MembroNotFoundException;
import com.application.appweb.model.Membro;
import com.application.appweb.repository.MembroRepository;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MembroService {

    @Autowired
    private MembroRepository membroRepository;


    public Membro findMembroWithRegistrosFinanceiros(Long id) {
        Membro membro = membroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membro não encontrado"));
        Hibernate.initialize(membro.getRegistrosFinanceiros());
        return membro;
    }

    public List<Membro> getAllMembros(){
        return membroRepository.findAll();
    }

    public Membro findMembroById(Long id){
        return membroRepository.findById(id).orElseThrow(() -> new MembroNotFoundException(id));
    }

    public Membro creatMembro(Membro membro){
        return membroRepository.save(membro);
    }


    public Membro updateMembro(Long id, Membro membroDetails){
        Membro membro = membroRepository.findById(id).orElseThrow(() -> new MembroNotFoundException(id));
        if(membro == null){
            return null;
        }
        membro.setDataNascimento(membroDetails.getDataNascimento());
        membro.setEmail(membroDetails.getEmail());
        membro.setEndereco(membroDetails.getEndereco());
        membro.setNome(membroDetails.getNome());
        membro.setTelefone(membroDetails.getTelefone());
        membro.setRegistrosFinanceiros(membroDetails.getRegistrosFinanceiros());

        return membroRepository.save(membro);
    }
    public void deleteMembro(Long id){
        Membro membro = membroRepository.findById(id).orElseThrow(() -> new MembroNotFoundException(id));
        if (membro !=  null) {
            membroRepository.delete(membro);
        }
    }

    public List<Membro> getMembrosByIdade(int idadeMinima,int idadeMaxima){
        LocalDate hoje = LocalDate.now();
        LocalDate dataNascimentoMinima = hoje.minus(idadeMaxima,ChronoUnit.YEARS);
        LocalDate dataNascimentoMaxima = hoje.minus(idadeMinima,ChronoUnit.YEARS);

        return membroRepository.findByDataNascimentoBetween(dataNascimentoMinima, dataNascimentoMaxima);
    }
    public List<Membro> getMembrosByNome(String nome){
        if (nome ==null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser vazio ou nulo");
        }
        nome = nome.trim();
        List<Membro> membros = membroRepository.findByNomeContainingIgnoreCase(nome);
        if (membros.isEmpty()) {
            throw new MembroNotFoundException("Nenhum membro encontrado com o nome: "+nome);
        }
        return membros;
    }
}




