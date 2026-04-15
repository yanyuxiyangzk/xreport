package com.xreport.pojo.dto;

import java.util.List;

/**
 * 数据集查询DTO
 */
public class DatasetDTO {

    private Long id;
    private String datasetName;
    private String datasetCode;
    private String datasetDesc;
    private Long datasourceId;
    private String sqlStatement;
    private String datasetType;
    private Long merchantId;
    private Integer status;
    private Integer pageNum;
    private Integer pageSize;

    // 聚合计算参数
    private String aggType;
    private String groupFields;
    private String aggFields;
    private String orderFields;
    private Integer limit;

    public DatasetDTO() {
        this.pageNum = 1;
        this.pageSize = 10;
    }

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

    public String getSqlStatement() { return sqlStatement; }
    public void setSqlStatement(String sqlStatement) { this.sqlStatement = sqlStatement; }

    public String getDatasetType() { return datasetType; }
    public void setDatasetType(String datasetType) { this.datasetType = datasetType; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

    public String getAggType() { return aggType; }
    public void setAggType(String aggType) { this.aggType = aggType; }

    public String getGroupFields() { return groupFields; }
    public void setGroupFields(String groupFields) { this.groupFields = groupFields; }

    public String getAggFields() { return aggFields; }
    public void setAggFields(String aggFields) { this.aggFields = aggFields; }

    public String getOrderFields() { return orderFields; }
    public void setOrderFields(String orderFields) { this.orderFields = orderFields; }

    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }
}