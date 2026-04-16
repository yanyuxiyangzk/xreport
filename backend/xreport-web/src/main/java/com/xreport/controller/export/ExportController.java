package com.xreport.controller.export;

import com.xreport.pojo.dto.ExportRequest;
import com.xreport.service.export.IExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 报表导出控制器
 */
@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final IExportService exportService;

    public ExportController(IExportService exportService) {
        this.exportService = exportService;
    }

    /**
     * 导出Excel - 使用前端发送的模板数据直接渲染
     * 前端发送FullTemplateResponse格式: { tplName, sheets: [{ id, name, celldata, config }] }
     */
    @PostMapping("/excel")
    public ResponseEntity<byte[]> exportExcel(@RequestBody Map<String, Object> request) {
        ExportRequest exportRequest = convertToExportRequest(request);
        byte[] data = exportService.exportToExcel(exportRequest);
        String filename = encodeFilename(exportRequest.getTplName(), "xlsx");
        return buildResponse(data, MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), filename);
    }

    /**
     * 导出PDF - 使用前端发送的模板数据直接渲染
     */
    @PostMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestBody Map<String, Object> request) {
        ExportRequest exportRequest = convertToExportRequest(request);
        byte[] data = exportService.exportToPdf(exportRequest);
        String filename = encodeFilename(exportRequest.getTplName(), "pdf");
        return buildResponse(data, MediaType.APPLICATION_PDF, filename);
    }

    /**
     * 导出Word - 使用前端发送的模板数据直接渲染
     */
    @PostMapping("/word")
    public ResponseEntity<byte[]> exportWord(@RequestBody Map<String, Object> request) {
        ExportRequest exportRequest = convertToExportRequest(request);
        byte[] data = exportService.exportToWord(exportRequest);
        String filename = encodeFilename(exportRequest.getTplName(), "docx");
        return buildResponse(data, MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"), filename);
    }

    /**
     * 使用poi-tl模板导出Word
     * @param request 包含tplId和data的请求
     */
    @PostMapping("/word/template")
    public ResponseEntity<byte[]> exportWordWithTemplate(@RequestBody Map<String, Object> request) {
        Long tplId = ((Number) request.get("tplId")).longValue();
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) request.get("data");
        byte[] result = exportService.exportWordWithTemplate(tplId, data);
        return buildResponse(result, MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"), "rendered_report.docx");
    }

    /**
     * 将前端发送的Map格式转换为ExportRequest
     * 前端sheets格式: [{ id, name, celldata, config }]
     * ExportRequest.SheetData字段: sheetId, sheetName, sheetIndex, celldata, config
     */
    @SuppressWarnings("unchecked")
    private ExportRequest convertToExportRequest(Map<String, Object> request) {
        ExportRequest exportRequest = new ExportRequest();
        exportRequest.setTplName((String) request.get("tplName"));

        List<Map<String, Object>> frontendSheets = (List<Map<String, Object>>) request.get("sheets");
        if (frontendSheets == null) {
            exportRequest.setSheets(new ArrayList<>());
            return exportRequest;
        }

        List<ExportRequest.SheetData> sheetDataList = new ArrayList<>();
        for (Map<String, Object> sheet : frontendSheets) {
            ExportRequest.SheetData sheetData = new ExportRequest.SheetData();
            // 前端发送 id -> 转为 sheetId
            Object id = sheet.get("id");
            if (id instanceof Number) {
                sheetData.setSheetId(((Number) id).longValue());
            }
            // 前端发送 name -> 转为 sheetName
            sheetData.setSheetName((String) sheet.get("name"));
            // 前端发送 index -> 转为 sheetIndex
            Object index = sheet.get("index");
            if (index instanceof Number) {
                sheetData.setSheetIndex(((Number) index).intValue());
            }
            // celldata 字段名一致
            sheetData.setCelldata((List<List<Object>>) sheet.get("celldata"));
            // config 字段名一致
            sheetData.setConfig((Map<String, Object>) sheet.get("config"));
            sheetDataList.add(sheetData);
        }
        exportRequest.setSheets(sheetDataList);
        return exportRequest;
    }

    private String encodeFilename(String tplName, String extension) {
        String name = tplName != null ? tplName : "report";
        String filename = name + "." + extension;
        // URL encode for browser download
        return URLEncoder.encode(filename, StandardCharsets.UTF_8);
    }

    private ResponseEntity<byte[]> buildResponse(byte[] data, MediaType mediaType, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(data.length);
        return ResponseEntity.ok().headers(headers).body(data);
    }
}