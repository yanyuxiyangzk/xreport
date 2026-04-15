package com.xreport.pojo.entity.reportdataset;

import java.time.LocalDateTime;

/**
 * 报表数据集实体
 */
public class ReportDataset {

    private Long id;

    private String datasetName;

    private String datasetCode;

    private String datasetDesc;

    private Long datasourceId;

    private String datasourceName;

    private String sqlStatement;

    private String datasetType;

    private Long merchantId;

    private Integer status;

    private Integer delFlag;

    private Long tenantId;

    private Long createUserId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public ReportDataset() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDatasetName() { return datasetName; }
    public void setDatasetName(String datasetName) { this.datasetName = datasetName; }

    public String getDatasetCode() { return datasetCode; }
    public void setDatasetCode(String datasetCode) { this.datasetCode = datasetCode; }

    public String getDatasetDesc() { return datasetDesc; }
    public void setDatasetDesc(String datasetDesc) { this.datasetDesc = datasetDesc; }

    public Long getDatasourceId() { return datasourceId; }
    public void setDatasourceId(Long datasourceId) { this.datasourceId = datasourceId; }

    public String getDatasourceName() { return datasourceName; }
    public void setDatasourceName(String datasourceName) { this.datasourceName = datasourceName; }

    public String getSqlStatement() { return sqlStatement; }
    public void setSqlStatement(String sqlStatement) { this.sqlStatement = sqlStatement; }

    public String getDatasetType() { return datasetType; }
    public void setDatasetType(String datasetType) { this.datasetType = datasetType; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getCreateUserId() { return createUserId; }
    public void setCreateUserId(Long createUserId) { this.createUserId = createUserId; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}