package com.xreport.pojo.entity;

import java.time.LocalDateTime;

public class SysRole {
    private Long id;
    private String roleName;
    private String roleKey;
    private Integer roleSort;
    private Integer status;
    private Integer delFlag;
    private Long tenantId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public SysRole() {}

    public SysRole(Long id, String roleName, String roleKey, Integer roleSort, Integer status,
                   Integer delFlag, Long tenantId, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.roleName = roleName;
        this.roleKey = roleKey;
        this.roleSort = roleSort;
        this.status = status;
        this.delFlag = delFlag;
        this.tenantId = tenantId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getRoleKey() { return roleKey; }
    public void setRoleKey(String roleKey) { this.roleKey = roleKey; }
    public Integer getRoleSort() { return roleSort; }
    public void setRoleSort(Integer roleSort) { this.roleSort = roleSort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
