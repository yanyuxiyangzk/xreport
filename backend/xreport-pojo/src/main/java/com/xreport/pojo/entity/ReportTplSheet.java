package com.xreport.pojo.entity;

import java.time.LocalDateTime;

public class ReportTplSheet {
    private Long id;
    private Long tplId;
    private String sheetName;
    private Integer sheetOrder;
    private Integer isLoop;
    private String loopSettings;
    private Long datasourceId;
    private String sqlStr;
    private String params;
    private Integer sort;
    private Integer delFlag;
    private Long tenantId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public ReportTplSheet() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTplId() { return tplId; }
    public void setTplId(Long tplId) { this.tplId = tplId; }
    public String getSheetName() { return sheetName; }
    public void setSheetName(String sheetName) { this.sheetName = sheetName; }
    public Integer getSheetOrder() { return sheetOrder; }
    public void setSheetOrder(Integer sheetOrder) { this.sheetOrder = sheetOrder; }
    public Integer getIsLoop() { return isLoop; }
    public void setIsLoop(Integer isLoop) { this.isLoop = isLoop; }
    public String getLoopSettings() { return loopSettings; }
    public void setLoopSettings(String loopSettings) { this.loopSettings = loopSettings; }
    public Long getDatasourceId() { return datasourceId; }
    public void setDatasourceId(Long datasourceId) { this.datasourceId = datasourceId; }
    public String getSqlStr() { return sqlStr; }
    public void setSqlStr(String sqlStr) { this.sqlStr = sqlStr; }
    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
