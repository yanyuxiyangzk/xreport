package com.xreport.pojo.dto;

import java.util.List;
import java.util.Map;

/**
 * 导出请求
 */
public class ExportRequest {
    private Long tplId;
    private String tplName;
    private List<SheetData> sheets;
    private Map<String, Object> parameters;

    public ExportRequest() {}

    public Long getTplId() {
        return tplId;
    }

    public void setTplId(Long tplId) {
        this.tplId = tplId;
    }

    public String getTplName() {
        return tplName;
    }

    public void setTplName(String tplName) {
        this.tplName = tplName;
    }

    public List<SheetData> getSheets() {
        return sheets;
    }

    public void setSheets(List<SheetData> sheets) {
        this.sheets = sheets;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * Sheet数据
     */
    public static class SheetData {
        private Long sheetId;
        private String sheetName;
        private Integer sheetIndex;
        private List<List<Object>> celldata;
        private Map<String, Object> config;

        public Long getSheetId() {
            return sheetId;
        }

        public void setSheetId(Long sheetId) {
            this.sheetId = sheetId;
        }

        public String getSheetName() {
            return sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

        public Integer getSheetIndex() {
            return sheetIndex;
        }

        public void setSheetIndex(Integer sheetIndex) {
            this.sheetIndex = sheetIndex;
        }

        public List<List<Object>> getCelldata() {
            return celldata;
        }

        public void setCelldata(List<List<Object>> celldata) {
            this.celldata = celldata;
        }

        public Map<String, Object> getConfig() {
            return config;
        }

        public void setConfig(Map<String, Object> config) {
            this.config = config;
        }
    }
}