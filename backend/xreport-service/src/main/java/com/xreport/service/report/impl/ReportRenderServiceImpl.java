package com.xreport.service.report.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.report.ReportDatasourceMapper;
import com.xreport.mapper.report.ReportTplSheetMapper;
import com.xreport.pojo.entity.reportdatasource.ReportDatasource;
import com.xreport.pojo.entity.ReportTpl;
import com.xreport.pojo.entity.ReportTplSheet;
import com.xreport.service.dataset.IDatasetQueryEngine;
import com.xreport.service.report.IReportRenderService;
import com.xreport.service.report.IReportTplService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * 报表渲染服务实现
 */
@Service
public class ReportRenderServiceImpl implements IReportRenderService {

    private static final Logger log = LoggerFactory.getLogger(ReportRenderServiceImpl.class);

    private final IReportTplService tplService;
    private final ReportTplSheetMapper sheetMapper;
    private final ReportDatasourceMapper datasourceMapper;
    private final IDatasetQueryEngine queryEngine;

    public ReportRenderServiceImpl(IReportTplService tplService,
                                   ReportTplSheetMapper sheetMapper,
                                   ReportDatasourceMapper datasourceMapper,
                                   IDatasetQueryEngine queryEngine) {
        this.tplService = tplService;
        this.sheetMapper = sheetMapper;
        this.datasourceMapper = datasourceMapper;
        this.queryEngine = queryEngine;
    }

    private List<ReportTplSheet> getSheetsByTplId(Long tplId) {
        LambdaQueryWrapper<ReportTplSheet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportTplSheet::getTplId, tplId)
               .eq(ReportTplSheet::getDelFlag, 0)
               .orderByAsc(ReportTplSheet::getSheetOrder);
        return sheetMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> previewReport(Long tplId) {
        ReportTpl tpl = tplService.getById(tplId);
        Map<String, Object> result = new HashMap<>();
        result.put("tplId", tplId);
        result.put("tplName", tpl.getTplName());

        List<ReportTplSheet> sheets = getSheetsByTplId(tplId);
        List<Map<String, Object>> sheetResults = new ArrayList<>();

        for (ReportTplSheet sheet : sheets) {
            Map<String, Object> sheetData = new HashMap<>();
            sheetData.put("sheetId", sheet.getId());
            sheetData.put("sheetName", sheet.getSheetName());

            if (sheet.getDatasourceId() != null) {
                ReportDatasource datasource = datasourceMapper.selectById(sheet.getDatasourceId());
                if (datasource != null) {
                    List<Map<String, Object>> data = queryEngine.execute(datasource, sheet.getSqlStr(), null);
                    sheetData.put("data", data);
                    sheetData.put("rowCount", data.size());
                }
            }
            sheetResults.add(sheetData);
        }

        result.put("sheets", sheetResults);
        result.put("sheetCount", sheets.size());
        return result;
    }

    @Override
    public Map<String, Object> renderReport(Long tplId, Map<String, Object> parameters) {
        ReportTpl tpl = tplService.getById(tplId);
        Map<String, Object> result = new HashMap<>();
        result.put("tplId", tplId);
        result.put("tplName", tpl.getTplName());
        result.put("renderTime", new Date());

        List<ReportTplSheet> sheets = getSheetsByTplId(tplId);
        List<Map<String, Object>> sheetResults = new ArrayList<>();

        for (ReportTplSheet sheet : sheets) {
            Map<String, Object> sheetData = new HashMap<>();
            sheetData.put("sheetId", sheet.getId());
            sheetData.put("sheetName", sheet.getSheetName());

            if (sheet.getDatasourceId() != null) {
                ReportDatasource datasource = datasourceMapper.selectById(sheet.getDatasourceId());
                if (datasource != null) {
                    Map<String, Object> queryParams = parseParams(sheet.getParams(), parameters);
                    List<Map<String, Object>> data = queryEngine.execute(datasource, sheet.getSqlStr(), queryParams);
                    sheetData.put("data", data);
                    sheetData.put("rowCount", data.size());
                }
            }
            sheetResults.add(sheetData);
        }

        result.put("sheets", sheetResults);
        result.put("sheetCount", sheets.size());
        return result;
    }

    @Override
    public byte[] exportReport(Long tplId, String format, Map<String, Object> parameters) {
        Map<String, Object> renderData = renderReport(tplId, parameters);

        if ("excel".equalsIgnoreCase(format)) {
            return exportToExcel(renderData);
        } else if ("pdf".equalsIgnoreCase(format)) {
            return exportToPdf(renderData);
        } else if ("word".equalsIgnoreCase(format)) {
            return exportToWord(renderData);
        } else {
            throw new BusinessException("不支持的导出格式: " + format);
        }
    }

    private byte[] exportToExcel(Map<String, Object> renderData) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<Map<String, Object>> sheets = (List<Map<String, Object>>) renderData.get("sheets");

            com.alibaba.excel.ExcelWriter excelWriter = EasyExcel.write(out).build();

            for (int i = 0; i < sheets.size(); i++) {
                Map<String, Object> sheetData = sheets.get(i);
                String sheetName = (String) sheetData.getOrDefault("sheetName", "Sheet" + (i + 1));
                List<Map<String, Object>> data = (List<Map<String, Object>>) sheetData.get("data");

                if (data != null && !data.isEmpty()) {
                    com.alibaba.excel.write.metadata.WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetName).build();
                    // Create header from first row's keys
                    List<String> headers = new ArrayList<>(data.get(0).keySet());
                    List<List<String>> headRows = new ArrayList<>();
                    headRows.add(headers);
                    excelWriter.write(headRows, writeSheet);
                    // Write data rows matching header order
                    List<List<Object>> dataRows = new ArrayList<>();
                    for (Map<String, Object> row : data) {
                        List<Object> rowList = new ArrayList<>();
                        for (String key : headers) {
                            rowList.add(row.get(key));
                        }
                        dataRows.add(rowList);
                    }
                    excelWriter.write(dataRows, writeSheet);
                }
            }

            excelWriter.finish();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出Excel失败", e);
            throw new BusinessException("导出Excel失败: " + e.getMessage());
        }
    }

    private byte[] exportToPdf(Map<String, Object> renderData) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, out);
            document.open();

            String tplName = (String) renderData.getOrDefault("tplName", "Report");
            document.add(new com.itextpdf.text.Paragraph(tplName, com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18)));
            document.add(new com.itextpdf.text.Paragraph(" "));

            List<Map<String, Object>> sheets = (List<Map<String, Object>>) renderData.get("sheets");
            for (Map<String, Object> sheetData : sheets) {
                String sheetName = (String) sheetData.get("sheetName");
                document.add(new com.itextpdf.text.Paragraph(sheetName, com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 14)));
                document.add(new com.itextpdf.text.Paragraph(" "));

                List<Map<String, Object>> data = (List<Map<String, Object>>) sheetData.get("data");
                if (data != null && !data.isEmpty()) {
                    // Get headers from first row
                    List<String> headers = new ArrayList<>(data.get(0).keySet());
                    int colCount = headers.size();

                    // Create PDF table
                    com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(colCount);
                    table.setWidthPercentage(100);

                    // Add header row
                    com.itextpdf.text.Font headerFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 10);
                    for (String header : headers) {
                        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(header, headerFont));
                        cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                        cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                        cell.setPadding(5);
                        table.addCell(cell);
                    }

                    // Add data rows
                    com.itextpdf.text.Font dataFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA, 9);
                    for (Map<String, Object> row : data) {
                        for (String key : headers) {
                            Object value = row.get(key);
                            String cellText = value != null ? value.toString() : "";
                            com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(cellText, dataFont));
                            cell.setPadding(4);
                            table.addCell(cell);
                        }
                    }

                    document.add(table);
                }
                document.add(new com.itextpdf.text.Paragraph(" "));
            }

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出PDF失败", e);
            throw new BusinessException("导出PDF失败: " + e.getMessage());
        }
    }

    private byte[] exportToWord(Map<String, Object> renderData) {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            String tplName = (String) renderData.getOrDefault("tplName", "Report");
            XWPFParagraph title = document.createParagraph();
            XWPFRun titleRun = title.createRun();
            titleRun.setText(tplName);
            titleRun.setBold(true);
            titleRun.setFontSize(18);

            List<Map<String, Object>> sheets = (List<Map<String, Object>>) renderData.get("sheets");
            for (Map<String, Object> sheetData : sheets) {
                String sheetName = (String) sheetData.get("sheetName");
                XWPFParagraph sheetTitle = document.createParagraph();
                XWPFRun sheetRun = sheetTitle.createRun();
                sheetRun.setText(sheetName);
                sheetRun.setBold(true);
                sheetRun.setFontSize(14);

                List<Map<String, Object>> data = (List<Map<String, Object>>) sheetData.get("data");
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
                        run.setText(headers.get(i));
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

            document.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出Word失败", e);
            throw new BusinessException("导出Word失败: " + e.getMessage());
        }
    }

    private Map<String, Object> parseParams(String paramsJson, Map<String, Object> requestParams) {
        if (paramsJson == null || paramsJson.isEmpty()) {
            return requestParams != null ? requestParams : new HashMap<>();
        }
        try {
            Map<String, Object> params = new HashMap<>();
            if (requestParams != null) {
                params.putAll(requestParams);
            }
            return params;
        } catch (Exception e) {
            log.warn("解析参数失败: {}", paramsJson, e);
            return requestParams != null ? requestParams : new HashMap<>();
        }
    }
}