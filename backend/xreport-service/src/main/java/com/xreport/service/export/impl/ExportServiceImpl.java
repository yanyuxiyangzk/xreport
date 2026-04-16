package com.xreport.service.export.impl;

import com.alibaba.excel.EasyExcel;
import com.xreport.common.exception.BusinessException;
import com.xreport.pojo.dto.ExportRequest;
import com.xreport.pojo.dto.ExportRequest.SheetData;
import com.xreport.service.export.IExportService;
import com.xreport.service.export.IWordTemplateService;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * 导出服务实现
 */
@Service
public class ExportServiceImpl implements IExportService {

    private static final Logger log = LoggerFactory.getLogger(ExportServiceImpl.class);

    private final IWordTemplateService wordTemplateService;

    public ExportServiceImpl(IWordTemplateService wordTemplateService) {
        this.wordTemplateService = wordTemplateService;
    }

    @Override
    public byte[] exportToExcel(ExportRequest request) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String tplName = request.getTplName() != null ? request.getTplName() : "Report";

            com.alibaba.excel.ExcelWriter excelWriter = EasyExcel.write(out).build();

            List<SheetData> sheets = request.getSheets();
            if (sheets == null || sheets.isEmpty()) {
                // Create a default sheet if no sheets provided
                sheets = new ArrayList<>();
                SheetData defaultSheet = new SheetData();
                defaultSheet.setSheetName(tplName);
                sheets.add(defaultSheet);
            }

            for (int i = 0; i < sheets.size(); i++) {
                SheetData sheetData = sheets.get(i);
                String sheetName = sheetData.getSheetName() != null ? sheetData.getSheetName() : "Sheet" + (i + 1);

                List<Map<String, Object>> data = convertCellDataToMap(sheetData);

                if (data != null && !data.isEmpty()) {
                    com.alibaba.excel.write.metadata.WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetName).build();
                    excelWriter.write(data, writeSheet);
                } else {
                    // Write empty sheet with just headers
                    com.alibaba.excel.write.metadata.WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetName).build();
                    excelWriter.write(new ArrayList<List<Object>>(), writeSheet);
                }
            }

            excelWriter.finish();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出Excel失败", e);
            throw new BusinessException("导出Excel失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] exportToPdf(ExportRequest request) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, out);
            document.open();

            String tplName = request.getTplName() != null ? request.getTplName() : "Report";

            // Title
            com.itextpdf.text.Font titleFont = com.itextpdf.text.FontFactory.getFont(
                    com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18);
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph(tplName, titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            List<SheetData> sheets = request.getSheets();
            if (sheets != null) {
                for (SheetData sheetData : sheets) {
                    String sheetName = sheetData.getSheetName();
                    if (sheetName != null && !sheetName.isEmpty()) {
                        com.itextpdf.text.Font sheetFont = com.itextpdf.text.FontFactory.getFont(
                                com.itextpdf.text.FontFactory.HELVETICA_BOLD, 14);
                        com.itextpdf.text.Paragraph sheetTitle = new com.itextpdf.text.Paragraph(sheetName, sheetFont);
                        sheetTitle.setSpacingBefore(15);
                        sheetTitle.setSpacingAfter(10);
                        document.add(sheetTitle);
                    }

                    List<Map<String, Object>> data = convertCellDataToMap(sheetData);
                    if (data != null && !data.isEmpty()) {
                        // Create PDF table
                        List<String> headers = new ArrayList<>(data.get(0).keySet());
                        int colCount = headers.size();

                        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(colCount);
                        table.setWidthPercentage(100);

                        // Add header row
                        com.itextpdf.text.Font headerFont = com.itextpdf.text.FontFactory.getFont(
                                com.itextpdf.text.FontFactory.HELVETICA_BOLD, 10);
                        for (String header : headers) {
                            com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                                    new com.itextpdf.text.Phrase(header != null ? header : "", headerFont));
                            cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                            cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                            cell.setPadding(5);
                            table.addCell(cell);
                        }

                        // Add data rows
                        com.itextpdf.text.Font dataFont = com.itextpdf.text.FontFactory.getFont(
                                com.itextpdf.text.FontFactory.HELVETICA, 9);
                        for (Map<String, Object> row : data) {
                            for (String key : headers) {
                                Object value = row.get(key);
                                String cellText = value != null ? value.toString() : "";
                                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                                        new com.itextpdf.text.Phrase(cellText, dataFont));
                                cell.setPadding(4);
                                table.addCell(cell);
                            }
                        }

                        document.add(table);
                    }
                    document.add(new com.itextpdf.text.Paragraph(" "));
                }
            }

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出PDF失败", e);
            throw new BusinessException("导出PDF失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] exportToWord(ExportRequest request) {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            String tplName = request.getTplName() != null ? request.getTplName() : "Report";

            // Title
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText(tplName);
            titleRun.setBold(true);
            titleRun.setFontSize(18);

            List<SheetData> sheets = request.getSheets();
            if (sheets != null) {
                for (SheetData sheetData : sheets) {
                    String sheetName = sheetData.getSheetName();
                    if (sheetName != null && !sheetName.isEmpty()) {
                        // Sheet title
                        XWPFParagraph sheetTitle = document.createParagraph();
                        XWPFRun sheetRun = sheetTitle.createRun();
                        sheetRun.setText(sheetName);
                        sheetRun.setBold(true);
                        sheetRun.setFontSize(14);
                    }

                    List<Map<String, Object>> data = convertCellDataToMap(sheetData);
                    if (data != null && !data.isEmpty()) {
                        // Get headers from first row
                        List<String> headers = new ArrayList<>(data.get(0).keySet());
                        int colCount = headers.size();

                        // Create Word table
                        XWPFTable table = document.createTable(1, colCount);

                        // Set header row
                        XWPFTableRow headerRow = table.getRow(0);
                        for (int i = 0; i < headers.size(); i++) {
                            XWPFParagraph para = headerRow.getCell(i).getParagraphs().get(0);
                            XWPFRun run = para.createRun();
                            run.setText(headers.get(i) != null ? headers.get(i) : "");
                            run.setBold(true);
                        }

                        // Add data rows
                        for (Map<String, Object> row : data) {
                            XWPFTableRow dataRow = table.createRow();
                            for (int i = 0; i < headers.size(); i++) {
                                Object value = row.get(headers.get(i));
                                String cellText = value != null ? value.toString() : "";
                                XWPFParagraph para = dataRow.getCell(i).getParagraphs().get(0);
                                XWPFRun run = para.createRun();
                                run.setText(cellText);
                            }
                        }
                    }
                }
            }

            document.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出Word失败", e);
            throw new BusinessException("导出Word失败: " + e.getMessage());
        }
    }

    /**
     * 将Luckysheet celldata格式转换为List<Map<String, Object>>
     * Luckysheet celldata格式: [[{r:0, c:0, v:"value"}, ...], ...]
     * 转换为: [{col0: val0, col1: val1, ...}, ...]
     */
    private List<Map<String, Object>> convertCellDataToMap(SheetData sheetData) {
        List<List<Object>> celldata = sheetData.getCelldata();
        if (celldata == null || celldata.isEmpty()) {
            return new ArrayList<>();
        }

        // Find dimensions
        int maxRow = 0;
        int maxCol = 0;
        for (List<Object> row : celldata) {
            if (row == null || row.isEmpty()) continue;
            for (Object cell : row) {
                if (cell instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> cellMap = (Map<String, Object>) cell;
                    Object rObj = cellMap.get("r");
                    Object cObj = cellMap.get("c");
                    if (rObj != null) {
                        int r = ((Number) rObj).intValue();
                        if (r > maxRow) maxRow = r;
                    }
                    if (cObj != null) {
                        int c = ((Number) cObj).intValue();
                        if (c > maxCol) maxCol = c;
                    }
                }
            }
        }

        if (maxRow == 0 && maxCol == 0) {
            return new ArrayList<>();
        }

        // Create 2D array to hold cell values
        Object[][] grid = new Object[maxRow + 1][maxCol + 1];

        for (List<Object> row : celldata) {
            if (row == null || row.isEmpty()) continue;
            for (Object cell : row) {
                if (cell instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> cellMap = (Map<String, Object>) cell;
                    int r = ((Number) cellMap.get("r")).intValue();
                    int c = ((Number) cellMap.get("c")).intValue();
                    Object v = cellMap.get("v");
                    if (v instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> vMap = (Map<String, Object>) v;
                        grid[r][c] = vMap.get("v");
                    } else {
                        grid[r][c] = v;
                    }
                }
            }
        }

        // Convert to list of maps with column headers
        List<Map<String, Object>> result = new ArrayList<>();
        for (int r = 0; r <= maxRow; r++) {
            Map<String, Object> rowMap = new LinkedHashMap<>();
            for (int c = 0; c <= maxCol; c++) {
                String header = getColumnHeader(c);
                rowMap.put(header, grid[r][c]);
            }
            result.add(rowMap);
        }

        return result;
    }

    /**
     * 获取列头名称 (A, B, ..., Z, AA, AB, ...)
     */
    private String getColumnHeader(int col) {
        StringBuilder header = new StringBuilder();
        int temp = col;
        while (temp >= 0) {
            header.insert(0, (char) ('A' + (temp % 26)));
            temp = temp / 26 - 1;
        }
        return header.toString();
    }

    @Override
    public byte[] exportWordWithTemplate(Long tplId, Map<String, Object> data) {
        // Delegate to WordTemplateService
        return wordTemplateService.renderTemplate(tplId, data);
    }
}