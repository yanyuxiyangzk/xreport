package com.xreport.pojo.dto;

import java.util.List;

/**
 * 完整模板保存请求DTO
 */
public class FullTemplateSaveRequest {

    private Long id;
    private String tplName;
    private String tplCode;
    private String tplType;
    private List<SheetData> sheets;

    public FullTemplateSaveRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTplName() { return tplName; }
    public void setTplName(String tplName) { this.tplName = tplName; }
    public String getTplCode() { return tplCode; }
    public void setTplCode(String tplCode) { this.tplCode = tplCode; }
    public String getTplType() { return tplType; }
    public void setTplType(String tplType) { this.tplType = tplType; }
    public List<SheetData> getSheets() { return sheets; }
    public void setSheets(List<SheetData> sheets) { this.sheets = sheets; }

    public static class SheetData {
        private Long id;
        private String sheetName;
        private Integer sheetIndex;
        private String sheetConfig;
        private List<Object> cells;

        public SheetData() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getSheetName() { return sheetName; }
        public void setSheetName(String sheetName) { this.sheetName = sheetName; }
        public Integer getSheetIndex() { return sheetIndex; }
        public void setSheetIndex(Integer sheetIndex) { this.sheetIndex = sheetIndex; }
        public String getSheetConfig() { return sheetConfig; }
        public void setSheetConfig(String sheetConfig) { this.sheetConfig = sheetConfig; }
        public List<Object> getCells() { return cells; }
        public void setCells(List<Object> cells) { this.cells = cells; }
    }
}