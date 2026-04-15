package com.xreport.pojo.dto;

/**
 * 数据源DTO - 用于创建和更新数据源
 */
public class DatasourceDTO {

    private Long id;

    private String datasourceName;

    private String datasourceType;

    private String jdbcUrl;

    private String username;

    private String password;

    private Long merchantId;

    private Integer status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    public DatasourceDTO() {}

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

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}