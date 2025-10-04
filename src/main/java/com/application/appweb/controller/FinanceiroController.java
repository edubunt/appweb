package com.application.appweb.controller;

import com.application.appweb.model.Financeiro;
import com.application.appweb.service.FinanceiroService;
import com.application.appweb.service.MembroService;
import com.application.appweb.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/financeiros")
@CrossOrigin(origins = "/*")
public class FinanceiroController {

    @Autowired
    private FinanceiroService financeiroService;
    @Autowired
    private MembroService membroService;

    @Autowired
    private PdfService pdfService;


    @GetMapping("/financeiro/membro/{id}")
    public ResponseEntity<List<Financeiro>> getRegistrosByMembroId(@PathVariable Long id) {
        List<Financeiro> registros = financeiroService.findByMembroId(id);
        if (registros.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(registros, HttpStatus.OK);
    }

    @GetMapping("/membro/nome")
    public ResponseEntity<List<Financeiro>> getRegistrosByMembroNome(@RequestParam String nome) {
        List<Financeiro> registros = financeiroService.findByMembroNome(nome);
        if (registros.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(registros, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Financeiro> getRegistroById(@PathVariable("id") Long id) {
        return financeiroService.getRegistroById(id)
                .map(registro -> new ResponseEntity<>(registro, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @PreAuthorize("hasAnyRole( 'ADMIN')")
    @PostMapping
    public ResponseEntity<Financeiro> createRegistro(@RequestBody Financeiro financeiro) {
        Financeiro novoRegistro = financeiroService.createRegistro(financeiro);
        return new ResponseEntity<>(novoRegistro, HttpStatus.CREATED);
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<Financeiro>> getRegistrosByPeriodo(
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim) {
        List<Financeiro> registros = financeiroService.getRegistrosByPeriodo(dataInicio, dataFim);
        return new ResponseEntity<>(registros, HttpStatus.OK);
    }

    @GetMapping("/saldo")
    public ResponseEntity<BigDecimal> getSaldo(
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim) {
        BigDecimal saldo = financeiroService.getSaldo(dataInicio, dataFim);
        return new ResponseEntity<>(saldo, HttpStatus.OK);
    }

    @GetMapping("/total-entradas")
    public ResponseEntity<BigDecimal> getTotalEntradas(
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim) {
        BigDecimal totalEntradas = financeiroService.getTotalEntradas(dataInicio, dataFim);
        return new ResponseEntity<>(totalEntradas, HttpStatus.OK);
    }

    @GetMapping("/total-saidas")
    public ResponseEntity<BigDecimal> getTotalSaidas(
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim) {
        BigDecimal totalSaidas = financeiroService.getTotalSaidas(dataInicio, dataFim);
        return new ResponseEntity<>(totalSaidas, HttpStatus.OK);
    }
    // Atualizar Registro (PUT)
    @PreAuthorize("hasAnyRole( 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Financeiro> updateRegistro(
            @PathVariable("id") Long id,
            @RequestBody Financeiro financeiroAtualizado) {
        return financeiroService.updateRegistro(id, financeiroAtualizado)
                .map(registro -> new ResponseEntity<>(registro, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Deletar Registro (DELETE)
    @PreAuthorize("hasAnyRole( 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistro(@PathVariable("id") Long id) {
        if (financeiroService.deleteRegistro(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 - Sem conteúdo
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 - Não encontrado
        }
    }

    @GetMapping("/relatorio-pdf")
    public ResponseEntity<byte[]> gerarRelatorioPDF(
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim) {

        try {
            // Validação das datas
            if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datas inválidas.");
            }

            // Obter os dados
            List<Financeiro> registros = financeiroService.getRegistrosByPeriodo(dataInicio, dataFim);
            BigDecimal totalEntradas = financeiroService.getTotalEntradas(dataInicio, dataFim);
            BigDecimal totalSaidas = financeiroService.getTotalSaidas(dataInicio, dataFim);
            BigDecimal saldo = financeiroService.getSaldo(dataInicio, dataFim);

            // Gerar o PDF
            byte[] pdfBytes = pdfService.gerarRelatorioFinanceiro(registros, totalEntradas, totalSaidas, saldo, dataInicio, dataFim);

            // Preparar a resposta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "relatorio-financeiro.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (ResponseStatusException e) {
            throw e; // Lança exceções específicas
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar o relatório PDF.");
        }
    }
    @GetMapping("/relatorio-entradas-pdf")
    public ResponseEntity<byte[]> gerarRelatorioEntradasPDF(
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim) {
        try {
            // Validar as datas
            if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datas inválidas.");
            }

            // Obter apenas os registros de ENTRADA no período
            List<Financeiro> entradas = financeiroService.getRegistrosByTipo(Financeiro.TipoRegistro.ENTRADA, dataInicio, dataFim);

            // Calcular o total de entradas
            BigDecimal totalEntradas = financeiroService.getTotalEntradas(dataInicio, dataFim);

            // Gerar o PDF usando o PdfService
            byte[] pdfBytes = pdfService.gerarRelatorioEntradas(entradas, totalEntradas, dataInicio, dataFim);

            // Preparar a resposta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "relatorio-entradas.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (ResponseStatusException e) {
            throw e; // Lança exceções específicas
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar o relatório de entradas.");
        }
    }
    @GetMapping("/relatorio-saidas-pdf")
    public ResponseEntity<byte[]> gerarRelatorioSaidasPDF(
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim) {
        try {
            // Validar as datas
            if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datas inválidas.");
            }

            // Obter apenas os registros de SAÍDA no período
            List<Financeiro> saidas = financeiroService.getRegistrosByTipo(Financeiro.TipoRegistro.SAIDA, dataInicio, dataFim);

            // Calcular o total de saídas
            BigDecimal totalSaidas = financeiroService.getTotalSaidas(dataInicio, dataFim);

            // Gerar o PDF usando o PdfService
            byte[] pdfBytes = pdfService.gerarRelatorioSaidas(saidas, totalSaidas, dataInicio, dataFim);

            // Preparar a resposta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "relatorio-saidas.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (ResponseStatusException e) {
            throw e; // Lança exceções específicas
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar o relatório de saídas.");
        }
    }
}


