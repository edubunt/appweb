package com.application.appweb.controller.api;

import com.application.appweb.dto.response.ApiResponse;
import com.application.appweb.service.FinanceiroService;
import com.application.appweb.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/financeiros/relatorios")
@Slf4j
public class FinanceiroRelatorioController {

    private final FinanceiroService financeiroService;

    public FinanceiroRelatorioController(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    @GetMapping("/saldo")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> saldo(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        log.info("Fetching balance from {} to {}", dataInicio, dataFim);
        ValidationUtil.validateDateRange(dataInicio, dataFim);
        BigDecimal saldo = financeiroService.getSaldo(dataInicio, dataFim);
        return ResponseEntity.ok(ApiResponse.success(saldo, "Balance retrieved successfully"));
    }

    @GetMapping("/entradas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> totalEntradas(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        log.info("Fetching total entries from {} to {}", dataInicio, dataFim);
        ValidationUtil.validateDateRange(dataInicio, dataFim);
        BigDecimal total = financeiroService.getTotalEntradas(dataInicio, dataFim);
        return ResponseEntity.ok(ApiResponse.success(total, "Total entries retrieved successfully"));
    }

    @GetMapping("/saidas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> totalSaidas(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        log.info("Fetching total exits from {} to {}", dataInicio, dataFim);
        ValidationUtil.validateDateRange(dataInicio, dataFim);
        BigDecimal total = financeiroService.getTotalSaidas(dataInicio, dataFim);
        return ResponseEntity.ok(ApiResponse.success(total, "Total exits retrieved successfully"));
    }

    @GetMapping("/resumo")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> resumo(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        log.info("Fetching financial summary from {} to {}", dataInicio, dataFim);
        ValidationUtil.validateDateRange(dataInicio, dataFim);

        BigDecimal entradas = financeiroService.getTotalEntradas(dataInicio, dataFim);
        BigDecimal saidas = financeiroService.getTotalSaidas(dataInicio, dataFim);
        BigDecimal saldo = entradas.subtract(saidas);

        Map<String, Object> resumo = new LinkedHashMap<>();
        resumo.put("dataInicio", dataInicio);
        resumo.put("dataFim", dataFim);
        resumo.put("totalEntradas", entradas);
        resumo.put("totalSaidas", saidas);
        resumo.put("saldo", saldo);

        return ResponseEntity.ok(ApiResponse.success(resumo, "Financial summary retrieved successfully"));
    }
}
