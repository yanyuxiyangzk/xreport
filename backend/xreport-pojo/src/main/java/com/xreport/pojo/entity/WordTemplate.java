package com.xreport.pojo.entity;

import java.time.LocalDateTime;

public class WordTemplate {
    private Long id;
    private String tplName;
    private String tplPath;
    private String description;
    private Integer status;
    private Integer delFlag;
    private Long tenantId;
    private Long createUserId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public WordTemplate() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTplName() { return tplName; }
    public void setTplName(String tplName) { this.tplName = tplName; }
    public String getTplPath() { return tplPath; }
    public void setTplPath(String tplPath) { this.tplPath = tplPath; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
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