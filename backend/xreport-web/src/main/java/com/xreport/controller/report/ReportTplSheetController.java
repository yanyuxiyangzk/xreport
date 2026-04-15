package com.xreport.controller.report;

import com.xreport.common.result.Result;
import com.xreport.pojo.entity.ReportTplSheet;
import com.xreport.service.report.IReportTplSheetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report/tpl/{tplId}/sheets")
public class ReportTplSheetController {

    private final IReportTplSheetService sheetService;

    public ReportTplSheetController(IReportTplSheetService sheetService) {
        this.sheetService = sheetService;
    }

    @GetMapping
    public Result<List<ReportTplSheet>> list(@PathVariable Long tplId) {
        return Result.ok(sheetService.getByTplId(tplId));
    }

    @GetMapping("/{id}")
    public Result<ReportTplSheet> getById(@PathVariable Long tplId, @PathVariable Long id) {
        return Result.ok(sheetService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@PathVariable Long tplId, @RequestBody ReportTplSheet sheet) {
        sheet.setTplId(tplId);
        sheetService.add(sheet);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long tplId, @PathVariable Long id, @RequestBody ReportTplSheet sheet) {
        sheet.setId(id);
        sheet.setTplId(tplId);
        sheetService.update(sheet);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long tplId, @PathVariable Long id) {
        sheetService.delete(id);
        return Result.ok();
    }
}
