package com.application.appweb.controller.api;

import com.application.appweb.dto.response.ApiResponse;
import com.application.appweb.dto.response.FinanceiroResponse;
import com.application.appweb.service.FinanceiroService;
import com.application.appweb.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/financeiros/consulta")
@Slf4j
public class FinanceiroConsultaController {

    private final FinanceiroService financeiroService;

    public FinanceiroConsultaController(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    @GetMapping("/membro/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FinanceiroResponse>>> buscarPorMembro(@PathVariable Long id) {
        log.info("Fetching financial records for member id: {}", id);
        List<FinanceiroResponse> registros = financeiroService.findByMembroId(id)
                .stream()
                .map(FinanceiroResponse::fromFinanceiro)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(registros, "Financial records retrieved successfully"));
    }

    @GetMapping("/membro")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FinanceiroResponse>>> buscarPorNome(@RequestParam String nome) {
        log.info("Searching financial records by member name: {}", nome);
        List<FinanceiroResponse> registros = financeiroService.findByMembroNome(nome)
                .stream()
                .map(FinanceiroResponse::fromFinanceiro)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(registros, "Financial records found successfully"));
    }

    @GetMapping("/periodo")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FinanceiroResponse>>> buscarPorPeriodo(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        log.info("Fetching financial records from {} to {}", dataInicio, dataFim);
        ValidationUtil.validateDateRange(dataInicio, dataFim);
        List<FinanceiroResponse> registros = financeiroService.getRegistrosByPeriodo(dataInicio, dataFim)
                .stream()
                .map(FinanceiroResponse::fromFinanceiro)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(registros, "Financial records retrieved successfully"));
    }

    @GetMapping("/entradas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FinanceiroResponse>>> buscarEntradas(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        log.info("Fetching entries from {} to {}", dataInicio, dataFim);
        ValidationUtil.validateDateRange(dataInicio, dataFim);
        List<FinanceiroResponse> entradas = financeiroService.getRegistrosByTipo(
                        com.application.appweb.enumModel.TipoTransacao.ENTRADA,
                        dataInicio,
                        dataFim)
                .stream()
                .map(FinanceiroResponse::fromFinanceiro)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(entradas, "Entries retrieved successfully"));
    }

    @GetMapping("/saidas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FinanceiroResponse>>> buscarSaidas(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        log.info("Fetching exits from {} to {}", dataInicio, dataFim);
        ValidationUtil.validateDateRange(dataInicio, dataFim);
        List<FinanceiroResponse> saidas = financeiroService.getRegistrosByTipo(
                        com.application.appweb.enumModel.TipoTransacao.SAIDA,
                        dataInicio,
                        dataFim)
                .stream()
                .map(FinanceiroResponse::fromFinanceiro)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(saidas, "Exits retrieved successfully"));
    }
}
