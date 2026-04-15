package com.xreport.pojo.entity;

import java.time.LocalDateTime;

public class SysMenu {
    private Long id;
    private Long parentId;
    private String menuType;
    private String menuName;
    private String path;
    private String component;
    private String icon;
    private Integer isFrame;
    private Integer isCache;
    private Integer sortOrder;
    private String visible;
    private String perms;
    private String status;
    private Integer delFlag;
    private Long tenantId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public SysMenu() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getMenuType() { return menuType; }
    public void setMenuType(String menuType) { this.menuType = menuType; }
    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getIsFrame() { return isFrame; }
    public void setIsFrame(Integer isFrame) { this.isFrame = isFrame; }
    public Integer getIsCache() { return isCache; }
    public void setIsCache(Integer isCache) { this.isCache = isCache; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getVisible() { return visible; }
    public void setVisible(String visible) { this.visible = visible; }
    public String getPerms() { return perms; }
    public void setPerms(String perms) { this.perms = perms; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
