package com.xreport.controller.report;

import com.xreport.common.result.Result;
import com.xreport.pojo.dto.RenderRequest;
import com.xreport.service.report.IReportRenderService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 报表渲染控制器
 */
@RestController
@RequestMapping("/api/report")
public class ReportRenderController {

    private final IReportRenderService renderService;

    public ReportRenderController(IReportRenderService renderService) {
        this.renderService = renderService;
    }

    /**
     * 渲染报表（根据模板和数据生成报表）
     */
    @PostMapping("/render")
    public Result<Map<String, Object>> render(@RequestBody RenderRequest request) {
        Map<String, Object> result = renderService.renderReport(request.getTplId(), request.getParameters());
        return Result.ok(result);
    }

    /**
     * 导出报表
     */
    @PostMapping("/export")
    public Result<byte[]> export(@RequestBody RenderRequest request) {
        byte[] data = renderService.exportReport(request.getTplId(), request.getFormat(), request.getParameters());
        return Result.ok(data);
    }
}