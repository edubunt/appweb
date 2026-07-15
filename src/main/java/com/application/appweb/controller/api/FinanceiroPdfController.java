package com.application.appweb.controller.api;

import com.application.appweb.dto.response.ApiResponse;
import com.application.appweb.service.FinanceiroService;
import com.application.appweb.service.PdfService;
import com.application.appweb.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/financeiros/pdf")
@Slf4j
public class FinanceiroPdfController {

    private final FinanceiroService financeiroService;
    private final PdfService pdfService;

    public FinanceiroPdfController(FinanceiroService financeiroService, PdfService pdfService) {
        this.financeiroService = financeiroService;
        this.pdfService = pdfService;
    }

    @GetMapping("/financeiro")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> relatorioFinanceiro(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        log.info("Generating complete financial report from {} to {}", dataInicio, dataFim);
        try {
            ValidationUtil.validateDateRange(dataInicio, dataFim);

            var registros = financeiroService.getRegistrosByPeriodo(dataInicio, dataFim);
            var entradas = financeiroService.getTotalEntradas(dataInicio, dataFim);
            var saidas = financeiroService.getTotalSaidas(dataInicio, dataFim);
            var saldo = financeiroService.getSaldo(dataInicio, dataFim);

            byte[] pdf = pdfService.gerarRelatorioFinanceiro(
                    registros, entradas, saidas, saldo, dataInicio, dataFim);

            return criarRespostaPdf(pdf, "relatorio-financeiro.pdf");
        } catch (Exception ex) {
            log.error("Error generating financial report", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error generating report: " + ex.getMessage(), 500));
        }
    }

    @GetMapping("/entradas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> relatorioEntradas(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        log.info("Generating entries report from {} to {}", dataInicio, dataFim);
        try {
            ValidationUtil.validateDateRange(dataInicio, dataFim);

            var entradas = financeiroService.getRegistrosByTipo(
                    com.application.appweb.enumModel.TipoTransacao.ENTRADA, dataInicio, dataFim);
            var total = financeiroService.getTotalEntradas(dataInicio, dataFim);

            byte[] pdf = pdfService.gerarRelatorioEntradas(entradas, total, dataInicio, dataFim);
            return criarRespostaPdf(pdf, "relatorio-entradas.pdf");
        } catch (Exception ex) {
            log.error("Error generating entries report", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error generating report: " + ex.getMessage(), 500));
        }
    }

    @GetMapping("/saidas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> relatorioSaidas(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        log.info("Generating exits report from {} to {}", dataInicio, dataFim);
        try {
            ValidationUtil.validateDateRange(dataInicio, dataFim);

            var saidas = financeiroService.getRegistrosByTipo(
                    com.application.appweb.enumModel.TipoTransacao.SAIDA, dataInicio, dataFim);
            var total = financeiroService.getTotalSaidas(dataInicio, dataFim);

            byte[] pdf = pdfService.gerarRelatorioSaidas(saidas, total, dataInicio, dataFim);
            return criarRespostaPdf(pdf, "relatorio-saidas.pdf");
        } catch (Exception ex) {
            log.error("Error generating exits report", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error generating report: " + ex.getMessage(), 500));
        }
    }

    private ResponseEntity<byte[]> criarRespostaPdf(byte[] pdf, String nomeArquivo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", nomeArquivo);
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
