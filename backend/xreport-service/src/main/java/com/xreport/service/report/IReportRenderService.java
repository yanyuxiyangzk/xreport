package com.xreport.service.report;

import java.util.Map;

/**
 * 报表渲染服务接口
 */
public interface IReportRenderService {

    /**
     * 渲染报表（根据模板生成示例数据）
     * @param tplId 模板ID
     * @return 渲染结果
     */
    Map<String, Object> previewReport(Long tplId);

    /**
     * 渲染报表（根据模板和数据渲染）
     * @param tplId 模板ID
     * @param parameters 参数
     * @return 渲染结果
     */
    Map<String, Object> renderReport(Long tplId, Map<String, Object> parameters);

    /**
     * 导出报表
     * @param tplId 模板ID
     * @param format 格式：pdf、excel、word
     * @param parameters 参数
     * @return 导出的文件字节数组
     */
    byte[] exportReport(Long tplId, String format, Map<String, Object> parameters);
}