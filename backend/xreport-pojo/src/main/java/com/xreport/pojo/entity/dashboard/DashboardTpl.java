package com.xreport.pojo.entity.dashboard;

import java.time.LocalDateTime;

public class DashboardTpl {
    private Long id;
    private String tplName;
    private String tplCode;
    private String description;
    private String config;
    private String previewUrl;
    private Integer status;
    private Long createUser;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public DashboardTpl() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTplName() { return tplName; }
    public void setTplName(String tplName) { this.tplName = tplName; }
    public String getTplCode() { return tplCode; }
    public void setTplCode(String tplCode) { this.tplCode = tplCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getConfig() { return config; }
    public void setConfig(String config) { this.config = config; }
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getCreateUser() { return createUser; }
    public void setCreateUser(Long createUser) { this.createUser = createUser; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}