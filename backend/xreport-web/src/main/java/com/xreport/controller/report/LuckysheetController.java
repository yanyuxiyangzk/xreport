package com.xreport.controller.report;

import com.xreport.common.result.Result;
import com.xreport.pojo.dto.LuckysheetSaveRequest;
import com.xreport.pojo.entity.LuckysheetReportCell;
import com.xreport.service.report.ILuckysheetReportCellService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report/luckysheet")
public class LuckysheetController {

    private final ILuckysheetReportCellService cellService;

    public LuckysheetController(ILuckysheetReportCellService cellService) {
        this.cellService = cellService;
    }

    @GetMapping("/{tplId}/{sheetId}")
    public Result<LuckysheetReportCell> loadCellData(@PathVariable Long tplId, @PathVariable Long sheetId) {
        return Result.ok(cellService.getBySheetId(tplId, sheetId));
    }

    @PostMapping("/save")
    public Result<Void> saveCellData(@RequestBody LuckysheetSaveRequest request) {
        cellService.saveCellData(request.getTplId(), request.getSheetId(), request.getCellData());
        return Result.ok();
    }
}
