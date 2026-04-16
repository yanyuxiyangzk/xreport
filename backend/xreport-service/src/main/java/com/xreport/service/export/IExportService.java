package com.xreport.service.export;

import com.xreport.pojo.dto.ExportRequest;

/**
 * 导出服务接口
 */
public interface IExportService {

    /**
     * 导出Excel
     * @param request 导出请求
     * @return Excel文件字节数组
     */
    byte[] exportToExcel(ExportRequest request);

    /**
     * 导出PDF
     * @param request 导出请求
     * @return PDF文件字节数组
     */
    byte[] exportToPdf(ExportRequest request);

    /**
     * 导出Word
     * @param request 导出请求
     * @return Word文件字节数组
     */
    byte[] exportToWord(ExportRequest request);

    /**
     * 使用poi-tl模板渲染导出Word
     * @param tplId 模板ID
     * @param data 渲染数据
     * @return Word文件字节数组
     */
    byte[] exportWordWithTemplate(Long tplId, java.util.Map<String, Object> data);
}