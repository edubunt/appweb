package com.application.appweb.service;


import com.application.appweb.model.Financeiro;
import com.application.appweb.model.Membro;
import com.application.appweb.repository.FinanceiroRepository;
import com.application.appweb.repository.MembroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FinanceiroService {

    @Autowired
    private FinanceiroRepository financeiroRepository;
    @Autowired
    private MembroRepository membroRepository;

    public List<Financeiro> findByMembroNome(String nome) {
        return financeiroRepository.findByMembroNomeContainingIgnoreCase(nome);
    }

    public List<Financeiro> findByMembroId(Long membroId) {
        return financeiroRepository.findByMembroId(membroId);
    }

    public Optional<Financeiro> getRegistroById(Long id) {
        return financeiroRepository.findById(id);
    }

    public Financeiro createRegistro(Financeiro financeiro) {
        if (financeiro.getMembro() != null && financeiro.getMembro().getId() != null) {
            Membro membro = membroRepository.findById(financeiro.getMembro().getId())
                    .orElseThrow(() -> new RuntimeException("Membro não encontrado"));

            financeiro.setMembro(membro); // Garantindo que o membro está gerenciado
        }

        return financeiroRepository.save(financeiro);
    }


    public List<Financeiro> getRegistrosByPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return financeiroRepository.findByDataRegistroBetween(dataInicio, dataFim);
    }

    public BigDecimal getTotalEntradas(LocalDate dataInicio, LocalDate dataFim) {
        return financeiroRepository.findByTipoRegistroAndDataRegistroBetween(Financeiro.TipoRegistro.ENTRADA, dataInicio, dataFim)
                .stream()
                .map(Financeiro::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalSaidas(LocalDate dataInicio, LocalDate dataFim) {
        return financeiroRepository.findByTipoRegistroAndDataRegistroBetween(Financeiro.TipoRegistro.SAIDA, dataInicio, dataFim)
                .stream()
                .map(Financeiro::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSaldo(LocalDate dataInicio, LocalDate dataFim) {
        BigDecimal totalEntradas = getTotalEntradas(dataInicio, dataFim);
        BigDecimal totalSaidas = getTotalSaidas(dataInicio, dataFim);
        return totalEntradas.subtract(totalSaidas);
    }

    // Atualizar Registro
    public Optional<Financeiro> updateRegistro(Long id, Financeiro financeiroAtualizado) {
        Optional<Financeiro> optionalFinanceiro = financeiroRepository.findById(id);
        if (optionalFinanceiro.isPresent()) {
            Financeiro financeiroExistente = optionalFinanceiro.get();

            // Validação básica: Garantir que o valor não seja negativo
            if (financeiroAtualizado.getValor() != null && financeiroAtualizado.getValor().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("O valor do registro não pode ser negativo.");
            }

            // Atualiza os campos permitidos
            financeiroExistente.setDescricao(financeiroAtualizado.getDescricao());
            financeiroExistente.setValor(financeiroAtualizado.getValor());
            financeiroExistente.setTipoRegistro(financeiroAtualizado.getTipoRegistro());
            financeiroExistente.setDataRegistro(financeiroAtualizado.getDataRegistro());
            financeiroExistente.setMembro(financeiroAtualizado.getMembro());

            return Optional.of(financeiroRepository.save(financeiroExistente));
        }
        return Optional.empty(); // Retorna vazio se o registro não for encontrado
    }

    // Deletar Registro
    public boolean deleteRegistro(Long id) {
        Optional<Financeiro> optionalFinanceiro = financeiroRepository.findById(id);
        if (optionalFinanceiro.isPresent()) {
            financeiroRepository.deleteById(id);
            return true; // Registro deletado com sucesso
        }
        return false; // Registro não encontrado
    }

    public List<Financeiro> getRegistrosByTipo(Financeiro.TipoRegistro tipoRegistro, LocalDate dataInicio, LocalDate dataFim) {
        return financeiroRepository.findByTipoRegistroAndDataRegistroBetween(tipoRegistro, dataInicio, dataFim);
    }
}

