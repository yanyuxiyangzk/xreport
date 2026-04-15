package com.xreport.pojo.entity.reportdatasource;

import java.time.LocalDateTime;

/**
 * 报表数据源实体
 */
public class ReportDatasource {

    private Long id;

    private String datasourceName;

    private String datasourceType;

    private String jdbcUrl;

    private String username;

    private String password;

    private String apiUrl;

    private String apiMethod;

    private String apiHeaders;

    private String apiBody;

    private Long merchantId;

    private Integer status;

    private Integer delFlag;

    private Long tenantId;

    private Long createUserId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public ReportDatasource() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDatasourceName() { return datasourceName; }
    public void setDatasourceName(String datasourceName) { this.datasourceName = datasourceName; }

    public String getDatasourceType() { return datasourceType; }
    public void setDatasourceType(String datasourceType) { this.datasourceType = datasourceType; }

    public String getJdbcUrl() { return jdbcUrl; }
    public void setJdbcUrl(String jdbcUrl) { this.jdbcUrl = jdbcUrl; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

    public String getApiMethod() { return apiMethod; }
    public void setApiMethod(String apiMethod) { this.apiMethod = apiMethod; }

    public String getApiHeaders() { return apiHeaders; }
    public void setApiHeaders(String apiHeaders) { this.apiHeaders = apiHeaders; }

    public String getApiBody() { return apiBody; }
    public void setApiBody(String apiBody) { this.apiBody = apiBody; }

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