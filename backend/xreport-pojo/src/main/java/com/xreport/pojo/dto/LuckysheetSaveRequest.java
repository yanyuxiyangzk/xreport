package com.xreport.pojo.dto;

public class LuckysheetSaveRequest {
    private Long tplId;
    private Long sheetId;
    private String cellData;

    public LuckysheetSaveRequest() {}

    public Long getTplId() { return tplId; }
    public void setTplId(Long tplId) { this.tplId = tplId; }
    public Long getSheetId() { return sheetId; }
    public void setSheetId(Long sheetId) { this.sheetId = sheetId; }
    public String getCellData() { return cellData; }
    public void setCellData(String cellData) { this.cellData = cellData; }
}
