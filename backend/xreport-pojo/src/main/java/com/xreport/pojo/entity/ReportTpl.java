package com.xreport.pojo.entity;

import java.time.LocalDateTime;

public class ReportTpl {
    private Long id;
    private String tplName;
    private Integer tplType;
    private Integer isExample;
    private Integer searchFormType;
    private String cdnHost;
    private Integer waterMarkType;
    private Integer permissionType;
    private String shareToken;
    private LocalDateTime shareExpireTime;
    private String thumbnail;
    private Integer status;
    private Integer delFlag;
    private Long tenantId;
    private Long createUserId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public ReportTpl() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTplName() { return tplName; }
    public void setTplName(String tplName) { this.tplName = tplName; }
    public Integer getTplType() { return tplType; }
    public void setTplType(Integer tplType) { this.tplType = tplType; }
    public Integer getIsExample() { return isExample; }
    public void setIsExample(Integer isExample) { this.isExample = isExample; }
    public Integer getSearchFormType() { return searchFormType; }
    public void setSearchFormType(Integer searchFormType) { this.searchFormType = searchFormType; }
    public String getCdnHost() { return cdnHost; }
    public void setCdnHost(String cdnHost) { this.cdnHost = cdnHost; }
    public Integer getWaterMarkType() { return waterMarkType; }
    public void setWaterMarkType(Integer waterMarkType) { this.waterMarkType = waterMarkType; }
    public Integer getPermissionType() { return permissionType; }
    public void setPermissionType(Integer permissionType) { this.permissionType = permissionType; }
    public String getShareToken() { return shareToken; }
    public void setShareToken(String shareToken) { this.shareToken = shareToken; }
    public LocalDateTime getShareExpireTime() { return shareExpireTime; }
    public void setShareExpireTime(LocalDateTime shareExpireTime) { this.shareExpireTime = shareExpireTime; }
    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
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
