package com.application.appweb.service;

import com.application.appweb.model.Financeiro;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class PdfService {

    public byte[] gerarRelatorioFinanceiro(
            List<Financeiro> registros,
            BigDecimal totalEntradas,
            BigDecimal totalSaidas,
            BigDecimal saldo,
            LocalDate dataInicio,
            LocalDate dataFim) throws Exception {
        log.info("Generating complete financial report from {} to {}", dataInicio, dataFim);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        var boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        var regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // Title
        document.add(new Paragraph("Relatório Financeiro Completo")
                .setFont(boldFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Period Info
        document.add(new Paragraph(String.format("Período: %s até %s", dataInicio, dataFim))
                .setFont(regularFont)
                .setFontSize(12)
                .setMarginBottom(15));

        // Table
        float[] columnWidths = {1, 3, 3, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

        addTableHeader(table, boldFont, "ID", "Descrição", "Valor", "Tipo");

        for (Financeiro registro : registros) {
            table.addCell(new Cell().add(new Paragraph(registro.getId().toString())).setFont(regularFont));
            table.addCell(new Cell().add(new Paragraph(registro.getDescricao())).setFont(regularFont));
            table.addCell(new Cell().add(new Paragraph(String.format("R$ %.2f", registro.getValor()))).setFont(regularFont));
            table.addCell(new Cell().add(new Paragraph(registro.getTipoRegistro().toString())).setFont(regularFont));
        }

        document.add(table);

        // Summary
        document.add(new Paragraph("\nResumo Financeiro:")
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginTop(20));
        document.add(new Paragraph(String.format("Total Entradas: R$ %.2f", totalEntradas)).setFont(regularFont));
        document.add(new Paragraph(String.format("Total Saídas: R$ %.2f", totalSaidas)).setFont(regularFont));
        document.add(new Paragraph(String.format("Saldo Final: R$ %.2f", saldo))
                .setFont(boldFont)
                .setFontSize(12));

        document.close();
        log.info("Financial report generated successfully");
        return outputStream.toByteArray();
    }

    public byte[] gerarRelatorioEntradas(
            List<Financeiro> entradas,
            BigDecimal totalEntradas,
            LocalDate dataInicio,
            LocalDate dataFim) throws Exception {
        log.info("Generating entries report from {} to {}", dataInicio, dataFim);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        var boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        var regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        document.add(new Paragraph("Relatório de Entradas")
                .setFont(boldFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        document.add(new Paragraph(String.format("Período: %s até %s", dataInicio, dataFim))
                .setFont(regularFont)
                .setFontSize(12)
                .setMarginBottom(15));

        float[] columnWidths = {1, 3, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

        addTableHeader(table, boldFont, "ID", "Descrição", "Valor");

        for (Financeiro entrada : entradas) {
            table.addCell(new Cell().add(new Paragraph(entrada.getId().toString())).setFont(regularFont));
            table.addCell(new Cell().add(new Paragraph(entrada.getDescricao())).setFont(regularFont));
            table.addCell(new Cell().add(new Paragraph(String.format("R$ %.2f", entrada.getValor()))).setFont(regularFont));
        }

        document.add(table);

        document.add(new Paragraph("\nResumo de Entradas:")
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginTop(20));
        document.add(new Paragraph(String.format("Total: R$ %.2f", totalEntradas))
                .setFont(boldFont)
                .setFontSize(12));

        document.close();
        log.info("Entries report generated successfully");
        return outputStream.toByteArray();
    }

    public byte[] gerarRelatorioSaidas(
            List<Financeiro> saidas,
            BigDecimal totalSaidas,
            LocalDate dataInicio,
            LocalDate dataFim) throws Exception {
        log.info("Generating exits report from {} to {}", dataInicio, dataFim);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        var boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        var regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        document.add(new Paragraph("Relatório de Saídas")
                .setFont(boldFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        document.add(new Paragraph(String.format("Período: %s até %s", dataInicio, dataFim))
                .setFont(regularFont)
                .setFontSize(12)
                .setMarginBottom(15));

        float[] columnWidths = {1, 3, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

        addTableHeader(table, boldFont, "ID", "Descrição", "Valor");

        for (Financeiro saida : saidas) {
            table.addCell(new Cell().add(new Paragraph(saida.getId().toString())).setFont(regularFont));
            table.addCell(new Cell().add(new Paragraph(saida.getDescricao())).setFont(regularFont));
            table.addCell(new Cell().add(new Paragraph(String.format("R$ %.2f", saida.getValor()))).setFont(regularFont));
        }

        document.add(table);

        document.add(new Paragraph("\nResumo de Saídas:")
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginTop(20));
        document.add(new Paragraph(String.format("Total: R$ %.2f", totalSaidas))
                .setFont(boldFont)
                .setFontSize(12));

        document.close();
        log.info("Exits report generated successfully");
        return outputStream.toByteArray();
    }

    private void addTableHeader(Table table, com.itextpdf.kernel.font.PdfFont boldFont, String... headers) {
        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setFont(boldFont)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(cell);
        }
    }
}
