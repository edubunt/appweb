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
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
public class PdfService {

    public byte[] gerarRelatorioFinanceiro(
            List<Financeiro> registros,
            BigDecimal totalEntradas,
            BigDecimal totalSaidas,
            BigDecimal saldo,
            LocalDate dataInicio,
            LocalDate dataFim) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Título
        document.add(new Paragraph("Relatório Financeiro")
                .setBold()
                .setFontSize(18));

        // Informações do período
        document.add(new Paragraph(String.format("Período: %s até %s", dataInicio, dataFim)));

        // Criar tabela
        float[] columnWidths = {2, 4, 3, 3};
        Table table = new Table(columnWidths);
        table.addCell("ID");
        table.addCell("Descrição");
        table.addCell("Valor");
        table.addCell("Tipo");

        // Preencher a tabela com os registros
        for (Financeiro registro : registros) {
            table.addCell(registro.getId().toString());
            table.addCell(registro.getDescricao());
            table.addCell(registro.getValor().toString());
            table.addCell(registro.getTipoRegistro().toString());
        }

        document.add(table);

        // Totais
        document.add(new Paragraph("\nResumo Financeiro:"));
        document.add(new Paragraph(String.format("Total Entradas: R$ %.2f", totalEntradas)));
        document.add(new Paragraph(String.format("Total Saídas: R$ %.2f", totalSaidas)));
        document.add(new Paragraph(String.format("Saldo Final: R$ %.2f", saldo)));

        document.close();
        return outputStream.toByteArray();
    }

    public byte[] gerarRelatorioEntradas(
            List<Financeiro> entradas,
            BigDecimal totalEntradas,
            LocalDate dataInicio,
            LocalDate dataFim) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Fontes personalizadas
        var boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        var regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // Título
        Paragraph titulo = new Paragraph("Relatório de Entradas Financeiras")
                .setFont(boldFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(titulo);

        // Informações do período
        Paragraph periodo = new Paragraph(String.format("Período: %s até %s", dataInicio, dataFim))
                .setFont(regularFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(15);
        document.add(periodo);

        // Criar tabela
        float[] columnWidths = {1, 3, 2}; // Larguras relativas das colunas
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

        // Estilização do cabeçalho da tabela
        Cell headerCell = new Cell().add(new Paragraph("ID"))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(headerCell);

        headerCell = new Cell().add(new Paragraph("Descrição"))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(headerCell);

        headerCell = new Cell().add(new Paragraph("Valor"))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addHeaderCell(headerCell);

        // Preencher a tabela com os registros de entrada
        boolean isAlternatingRow = false;
        for (Financeiro entrada : entradas) {
            Cell cell = new Cell().add(new Paragraph(entrada.getId().toString()))
                    .setFont(regularFont)
                    .setTextAlignment(TextAlignment.CENTER);
            if (isAlternatingRow) cell.setBackgroundColor(ColorConstants.WHITE);
            else cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            table.addCell(cell);

            cell = new Cell().add(new Paragraph(entrada.getDescricao()))
                    .setFont(regularFont)
                    .setTextAlignment(TextAlignment.LEFT);
            if (isAlternatingRow) cell.setBackgroundColor(ColorConstants.WHITE);
            else cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            table.addCell(cell);

            cell = new Cell().add(new Paragraph(String.format("R$ %.2f", entrada.getValor())))
                    .setFont(regularFont)
                    .setTextAlignment(TextAlignment.RIGHT);
            if (isAlternatingRow) cell.setBackgroundColor(ColorConstants.WHITE);
            else cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            table.addCell(cell);

            isAlternatingRow = !isAlternatingRow; // Alternar cor de fundo
        }

        document.add(table);

        // Totais
        Paragraph totais = new Paragraph("\nResumo de Entradas:")
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginTop(20);
        document.add(totais);

        document.add(new Paragraph(String.format("Total Entradas: R$ %.2f", totalEntradas))
                .setFont(regularFont)
                .setFontSize(12)
                .setMarginBottom(10));

        document.close();
        return outputStream.toByteArray();
    }

    public byte[] gerarRelatorioSaidas(
            List<Financeiro> saidas,
            BigDecimal totalSaidas,
            LocalDate dataInicio,
            LocalDate dataFim) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Fontes personalizadas
        var boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        var regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // Título
        Paragraph titulo = new Paragraph("Relatório de Saídas Financeiras")
                .setFont(boldFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(titulo);

        // Informações do período
        Paragraph periodo = new Paragraph(String.format("Período: %s até %s", dataInicio, dataFim))
                .setFont(regularFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(15);
        document.add(periodo);

        // Criar tabela
        float[] columnWidths = {1, 3, 2}; // Larguras relativas das colunas
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

        // Estilização do cabeçalho da tabela
        Cell headerCell = new Cell().add(new Paragraph("ID"))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(headerCell);

        headerCell = new Cell().add(new Paragraph("Descrição"))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(headerCell);

        headerCell = new Cell().add(new Paragraph("Valor"))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addHeaderCell(headerCell);

        // Preencher a tabela com os registros de saída
        boolean isAlternatingRow = false;
        for (Financeiro saida : saidas) {
            Cell cell = new Cell().add(new Paragraph(saida.getId().toString()))
                    .setFont(regularFont)
                    .setTextAlignment(TextAlignment.CENTER);
            if (isAlternatingRow) cell.setBackgroundColor(ColorConstants.WHITE);
            else cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            table.addCell(cell);

            cell = new Cell().add(new Paragraph(saida.getDescricao()))
                    .setFont(regularFont)
                    .setTextAlignment(TextAlignment.LEFT);
            if (isAlternatingRow) cell.setBackgroundColor(ColorConstants.WHITE);
            else cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            table.addCell(cell);

            cell = new Cell().add(new Paragraph(String.format("R$ %.2f", saida.getValor())))
                    .setFont(regularFont)
                    .setTextAlignment(TextAlignment.RIGHT);
            if (isAlternatingRow) cell.setBackgroundColor(ColorConstants.WHITE);
            else cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            table.addCell(cell);

            isAlternatingRow = !isAlternatingRow; // Alternar cor de fundo
        }

        document.add(table);

        // Totais
        Paragraph totais = new Paragraph("\nResumo de Saídas:")
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginTop(20);
        document.add(totais);

        document.add(new Paragraph(String.format("Total Saídas: R$ %.2f", totalSaidas))
                .setFont(regularFont)
                .setFontSize(12)
                .setMarginBottom(10));

        document.close();
        return outputStream.toByteArray();
    }
}

