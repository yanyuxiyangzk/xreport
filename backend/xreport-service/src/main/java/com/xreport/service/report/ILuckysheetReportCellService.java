package com.xreport.service.report;

import com.xreport.pojo.entity.LuckysheetReportCell;

public interface ILuckysheetReportCellService {
    LuckysheetReportCell getBySheetId(Long tplId, Long sheetId);
    void saveCellData(Long tplId, Long sheetId, String cellData);
    void batchSaveCellData(Long tplId, Long sheetId, String cellData);
    void deleteBySheetId(Long tplId, Long sheetId);
}
