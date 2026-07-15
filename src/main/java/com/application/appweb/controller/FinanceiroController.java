package com.application.appweb.controller;

import com.application.appweb.dto.request.FinanceiroRequest;
import com.application.appweb.dto.response.ApiResponse;
import com.application.appweb.dto.response.FinanceiroResponse;
import com.application.appweb.service.FinanceiroService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/financeiros")
@Slf4j
public class FinanceiroController {

    private final FinanceiroService financeiroService;

    public FinanceiroController(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<FinanceiroResponse>> getFinanceiroById(@PathVariable Long id) {
        log.info("Fetching financial record with id: {}", id);
        Optional<FinanceiroResponse> financeiro = financeiroService.getRegistroById(id)
                .map(FinanceiroResponse::fromFinanceiro);

        if (financeiro.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(financeiro.get(), "Financial record retrieved successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FinanceiroResponse>> createFinanceiro(
            @Valid @RequestBody FinanceiroRequest financeiroRequest) {
        log.info("Creating new financial record: {}", financeiroRequest.descricao());
        FinanceiroResponse financeiro = FinanceiroResponse.fromFinanceiro(
                financeiroService.createRegistro(financeiroRequest)
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(financeiro, "Financial record created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FinanceiroResponse>> updateFinanceiro(
            @PathVariable Long id,
            @Valid @RequestBody FinanceiroRequest financeiroRequest) {
        log.info("Updating financial record with id: {}", id);
        Optional<FinanceiroResponse> financeiro = financeiroService.updateRegistro(id, financeiroRequest)
                .map(FinanceiroResponse::fromFinanceiro);

        if (financeiro.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(financeiro.get(), "Financial record updated successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFinanceiro(@PathVariable Long id) {
        log.info("Deleting financial record with id: {}", id);
        boolean removed = financeiroService.deleteRegistro(id);
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
