package com.xreport.service.system;

import com.xreport.security.ShiroRealm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

/**
 * 权限验证服务
 *
 * 提供功能：
 * 1. hasPermission(permission) - 检查当前用户是否有指定权限
 * 2. hasRole(role) - 检查当前用户是否有指定角色
 * 3. getUserPermissions(userId) - 获取用户的所有权限
 */
@Service
public class PermissionService {

    private final ShiroRealm shiroRealm;

    public PermissionService(ShiroRealm shiroRealm) {
        this.shiroRealm = shiroRealm;
    }

    /**
     * 检查当前用户是否有指定权限
     *
     * @param permission 权限标识，如 "system:user:list"
     * @return true-有权限，false-无权限
     */
    public boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            return false;
        }

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(permission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查当前用户是否有指定角色
     *
     * @param role 角色标识（不含ROLE_前缀），如 "admin"
     * @return true-有角色，false-无角色
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            return false;
        }

        String roleAuthority = "ROLE_" + role;
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(roleAuthority)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取当前用户的权限列表
     *
     * @return 权限标识集合
     */
    public Set<String> getCurrentUserPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return java.util.Collections.emptySet();
        }

        return shiroRealm.getUserPermissions(getCurrentUserId());
    }

    /**
     * 获取指定用户的所有权限
     *
     * @param userId 用户ID
     * @return 权限标识集合
     */
    public Set<String> getUserPermissions(Long userId) {
        return shiroRealm.getUserPermissions(userId);
    }

    /**
     * 获取当前登录用户的ID
     *
     * @return 用户ID，如果未登录返回null
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }

        return null;
    }

    /**
     * 检查当前用户是否为管理员
     *
     * @return true-是管理员，false-不是
     */
    public boolean isAdmin() {
        return hasRole("admin");
    }
}