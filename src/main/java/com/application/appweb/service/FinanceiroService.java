package com.application.appweb.service;

import com.application.appweb.dto.request.FinanceiroRequest;
import com.application.appweb.exception.InvalidInputException;
import com.application.appweb.model.Financeiro;
import com.application.appweb.model.Membro;
import com.application.appweb.repository.FinanceiroRepository;
import com.application.appweb.repository.MembroRepository;
import com.application.appweb.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class FinanceiroService {

    private final FinanceiroRepository financeiroRepository;
    private final MembroRepository membroRepository;

    public FinanceiroService(FinanceiroRepository financeiroRepository, MembroRepository membroRepository) {
        this.financeiroRepository = financeiroRepository;
        this.membroRepository = membroRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Financeiro> getRegistroById(Long id) {
        log.debug("Fetching financial record with id: {}", id);
        return financeiroRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Financeiro> findByMembroId(Long membroId) {
        log.debug("Fetching financial records for member id: {}", membroId);
        return financeiroRepository.findByMembroId(membroId);
    }

    @Transactional(readOnly = true)
    public List<Financeiro> findByMembroNome(String nome) {
        log.debug("Fetching financial records for member name: {}", nome);
        ValidationUtil.validateStringNotEmpty(nome, "Member name");
        return financeiroRepository.findByMembroNomeContainingIgnoreCase(nome);
    }

    public Financeiro createRegistro(FinanceiroRequest request) {
        log.info("Creating new financial record: {}", request.descricao());
        ValidationUtil.validateNotNull(request, "Financial request");

        Financeiro financeiro = new Financeiro();
        financeiro.setDescricao(request.descricao());
        financeiro.setValor(request.valor());
        financeiro.setTipoRegistro(request.tipo());
        financeiro.setDataRegistro(request.dataTransacao());

        if (request.membroId() != null) {
            Membro membro = membroRepository.findById(request.membroId())
                    .orElseThrow(() -> new InvalidInputException("Member not found with id: " + request.membroId()));
            financeiro.setMembro(membro);
        }

        Financeiro saved = financeiroRepository.save(financeiro);
        log.info("Financial record created with id: {}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Financeiro> getRegistrosByPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        log.debug("Fetching financial records between {} and {}", dataInicio, dataFim);
        ValidationUtil.validateDateRange(dataInicio, dataFim);
        return financeiroRepository.findByDataRegistroBetween(dataInicio, dataFim);
    }

    @Transactional(readOnly = true)
    public List<Financeiro> getRegistrosByTipo(com.application.appweb.enumModel.TipoTransacao tipo, LocalDate dataInicio, LocalDate dataFim) {
        log.debug("Fetching {} records between {} and {}", tipo, dataInicio, dataFim);
        ValidationUtil.validateDateRange(dataInicio, dataFim);
        ValidationUtil.validateNotNull(tipo, "Transaction type");
        return financeiroRepository.findByTipoRegistroAndDataRegistroBetween(tipo, dataInicio, dataFim);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalEntradas(LocalDate dataInicio, LocalDate dataFim) {
        log.debug("Calculating total entries between {} and {}", dataInicio, dataFim);
        ValidationUtil.validateDateRange(dataInicio, dataFim);
        return financeiroRepository
                .findByTipoRegistroAndDataRegistroBetween(com.application.appweb.enumModel.TipoTransacao.ENTRADA, dataInicio, dataFim)
                .stream()
                .map(Financeiro::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalSaidas(LocalDate dataInicio, LocalDate dataFim) {
        log.debug("Calculating total exits between {} and {}", dataInicio, dataFim);
        ValidationUtil.validateDateRange(dataInicio, dataFim);
        return financeiroRepository
                .findByTipoRegistroAndDataRegistroBetween(com.application.appweb.enumModel.TipoTransacao.SAIDA, dataInicio, dataFim)
                .stream()
                .map(Financeiro::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public BigDecimal getSaldo(LocalDate dataInicio, LocalDate dataFim) {
        log.debug("Calculating balance between {} and {}", dataInicio, dataFim);
        BigDecimal totalEntradas = getTotalEntradas(dataInicio, dataFim);
        BigDecimal totalSaidas = getTotalSaidas(dataInicio, dataFim);
        return totalEntradas.subtract(totalSaidas);
    }

    public Optional<Financeiro> updateRegistro(Long id, FinanceiroRequest request) {
        log.info("Updating financial record with id: {}", id);
        ValidationUtil.validateNotNull(request, "Financial request");

        return financeiroRepository.findById(id).map(financeiroExistente -> {
            financeiroExistente.setDescricao(request.descricao());
            financeiroExistente.setValor(request.valor());
            financeiroExistente.setTipoRegistro(request.tipo());
            financeiroExistente.setDataRegistro(request.dataTransacao());

            if (request.membroId() != null) {
                Membro membro = membroRepository.findById(request.membroId())
                        .orElseThrow(() -> new InvalidInputException("Member not found with id: " + request.membroId()));
                financeiroExistente.setMembro(membro);
            }

            Financeiro updated = financeiroRepository.save(financeiroExistente);
            log.info("Financial record with id {} updated successfully", id);
            return updated;
        });
    }

    public boolean deleteRegistro(Long id) {
        log.info("Deleting financial record with id: {}", id);
        if (financeiroRepository.existsById(id)) {
            financeiroRepository.deleteById(id);
            log.info("Financial record with id {} deleted successfully", id);
            return true;
        }
        log.warn("Financial record with id {} not found", id);
        return false;
    }
}
