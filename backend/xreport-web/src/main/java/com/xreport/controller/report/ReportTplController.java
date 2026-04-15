package com.xreport.controller.report;

import com.github.pagehelper.PageInfo;
import com.xreport.common.result.Result;
import com.xreport.pojo.dto.ReportTplDto;
import com.xreport.pojo.entity.ReportTpl;
import com.xreport.service.report.IReportTplService;
import com.xreport.service.report.IReportTplSheetService;
import com.xreport.service.report.IReportRenderService;
import com.xreport.pojo.entity.ReportTplSheet;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report/tpls")
@PreAuthorize("hasAuthority('report:manage')")
public class ReportTplController {

    private final IReportTplService tplService;
    private final IReportTplSheetService sheetService;
    private final IReportRenderService renderService;

    public ReportTplController(IReportTplService tplService, IReportTplSheetService sheetService, IReportRenderService renderService) {
        this.tplService = tplService;
        this.sheetService = sheetService;
        this.renderService = renderService;
    }

    @GetMapping
    public Result<PageInfo<ReportTpl>> pageList(ReportTplDto dto) {
        return Result.ok(tplService.pageQuery(dto));
    }

    @GetMapping("/all")
    public Result<List<ReportTpl>> list(ReportTplDto dto) {
        return Result.ok(tplService.list(dto));
    }

    @GetMapping("/{id}")
    public Result<ReportTpl> getById(@PathVariable Long id) {
        return Result.ok(tplService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody ReportTpl tpl) {
        tplService.add(tpl);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ReportTpl tpl) {
        tpl.setId(id);
        tplService.update(tpl);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tplService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        tplService.updateStatus(id, status);
        return Result.ok();
    }

    @GetMapping("/{id}/sheets")
    public Result<List<ReportTplSheet>> listSheets(@PathVariable Long id) {
        return Result.ok(sheetService.getByTplId(id));
    }

    @PostMapping("/{id}/sheets")
    public Result<Void> addSheet(@PathVariable Long id, @RequestBody ReportTplSheet sheet) {
        sheet.setTplId(id);
        sheetService.add(sheet);
        return Result.ok();
    }

    @PutMapping("/{id}/sheets/{sheetId}")
    public Result<Void> updateSheet(@PathVariable Long id, @PathVariable Long sheetId, @RequestBody ReportTplSheet sheet) {
        sheet.setId(sheetId);
        sheet.setTplId(id);
        sheetService.update(sheet);
        return Result.ok();
    }

    @DeleteMapping("/{id}/sheets/{sheetId}")
    public Result<Void> deleteSheet(@PathVariable Long id, @PathVariable Long sheetId) {
        sheetService.delete(sheetId);
        return Result.ok();
    }

    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        tplService.publish(id);
        return Result.ok();
    }

    @GetMapping("/{id}/preview")
    public Result<Map<String, Object>> preview(@PathVariable Long id) {
        return Result.ok(renderService.previewReport(id));
    }
}
