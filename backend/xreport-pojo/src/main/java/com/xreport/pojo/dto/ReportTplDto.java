package com.xreport.pojo.dto;

public class ReportTplDto {
    private String tplName;
    private Integer tplType;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public ReportTplDto() {}

    public String getTplName() { return tplName; }
    public void setTplName(String tplName) { this.tplName = tplName; }
    public Integer getTplType() { return tplType; }
    public void setTplType(Integer tplType) { this.tplType = tplType; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
