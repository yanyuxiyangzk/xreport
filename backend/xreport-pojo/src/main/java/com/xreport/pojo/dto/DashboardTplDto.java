package com.xreport.pojo.dto;

public class DashboardTplDto {
    private Long id;
    private String tplName;
    private String tplCode;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public DashboardTplDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTplName() { return tplName; }
    public void setTplName(String tplName) { this.tplName = tplName; }
    public String getTplCode() { return tplCode; }
    public void setTplCode(String tplCode) { this.tplCode = tplCode; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}