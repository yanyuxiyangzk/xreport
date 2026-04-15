package com.xreport.pojo.entity;

import java.time.LocalDateTime;

public class LuckysheetReportCell {
    private Long id;
    private Long tplId;
    private Long sheetId;
    private String cellData;
    private Integer ver;
    private Integer delFlag;
    private Long tenantId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public LuckysheetReportCell() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTplId() { return tplId; }
    public void setTplId(Long tplId) { this.tplId = tplId; }
    public Long getSheetId() { return sheetId; }
    public void setSheetId(Long sheetId) { this.sheetId = sheetId; }
    public String getCellData() { return cellData; }
    public void setCellData(String cellData) { this.cellData = cellData; }
    public Integer getVer() { return ver; }
    public void setVer(Integer ver) { this.ver = ver; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
