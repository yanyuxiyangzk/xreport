package com.xreport.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.mapper.SysMenuMapper;
import com.xreport.mapper.SysRoleMenuMapper;
import com.xreport.mapper.SysUserRoleMapper;
import com.xreport.pojo.entity.SysMenu;
import com.xreport.pojo.entity.SysRole;
import com.xreport.pojo.entity.SysUser;
import com.xreport.pojo.entity.SysUserRole;
import com.xreport.service.system.ISysRoleService;
import com.xreport.service.system.ISysUserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ShiroRealm - 认证和授权实现
 *
 * 功能：
 * 1. doGetAuthenticationInfo - 验证JWT Token，返回AuthenticationInfo
 * 2. doGetAuthorizationInfo - 加载用户权限和角色
 *
 * 整合已有的User、Role、Menu数据
 */
@Component
public class ShiroRealm implements UserDetailsService {

    private final ISysUserService userService;
    private final ISysRoleService roleService;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;

    public ShiroRealm(ISysUserService userService, ISysRoleService roleService,
                      SysUserRoleMapper userRoleMapper, SysRoleMenuMapper roleMenuMapper,
                      SysMenuMapper menuMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.userRoleMapper = userRoleMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.menuMapper = menuMapper;
    }

    /**
     * 认证方法 - 验证用户凭据
     * 相当于 Shiro 的 doGetAuthenticationInfo
     *
     * @param username 用户名
     * @return UserDetails 用于认证
     * @throws UsernameNotFoundException 如果用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        // 加载用户角色
        Set<String> roles = getUserRoles(user.getId());
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        // 加载用户权限
        Set<String> permissions = getUserPermissions(user.getId());
        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }

        return new User(
            user.getUsername(),
            user.getPassword(),
            user.getStatus() != null && user.getStatus() == 1,
            true, // accountNonExpired
            true, // credentialsNonExpired
            true, // accountNonLocked
            authorities
        );
    }

    /**
     * 加载用户角色
     * 相当于 Shiro 的 doGetAuthorizationInfo（角色部分）
     */
    public Set<String> getUserRoles(Long userId) {
        Set<String> roles = new HashSet<>();

        LambdaQueryWrapper<SysUserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = userRoleMapper.selectList(urWrapper);

        for (SysUserRole ur : userRoles) {
            SysRole role = roleService.getById(ur.getRoleId());
            if (role != null && role.getDelFlag() != 1) {
                roles.add(role.getRoleKey());
            }
        }

        return roles;
    }

    /**
     * 加载用户权限（从菜单的perms字段获取）
     * 相当于 Shiro 的 doGetAuthorizationInfo（权限部分）
     */
    public Set<String> getUserPermissions(Long userId) {
        Set<String> permissions = new HashSet<>();

        // 获取用户角色
        Set<String> roleKeys = getUserRoles(userId);

        // 遍历每个角色，获取角色关联的菜单
        for (String roleKey : roleKeys) {
            // 通过角色ID获取菜单ID列表
            List<Long> menuIds = getMenuIdsByRoleKey(roleKey);

            // 通过菜单ID获取权限字符串
            for (Long menuId : menuIds) {
                SysMenu menu = menuMapper.selectById(menuId);
                if (menu != null && menu.getPerms() != null && !menu.getPerms().isEmpty()) {
                    permissions.add(menu.getPerms());
                }
            }
        }

        return permissions;
    }

    /**
     * 根据角色标识获取菜单ID列表
     */
    private List<Long> getMenuIdsByRoleKey(String roleKey) {
        // 查找角色
        LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysRole::getRoleKey, roleKey).eq(SysRole::getDelFlag, 0);
        List<SysRole> roles = roleService.pageQuery(null).getList();

        if (roles.isEmpty()) {
            return new ArrayList<>();
        }

        Long roleId = roles.stream()
            .filter(r -> roleKey.equals(r.getRoleKey()))
            .findFirst()
            .map(SysRole::getId)
            .orElse(null);

        if (roleId == null) {
            return new ArrayList<>();
        }

        // 获取角色关联的菜单
        return roleService.getMenuIdsByRoleId(roleId);
    }

    /**
     * 根据用户ID获取完整的UserDetails
     */
    public UserDetails getUserDetailsByUserId(Long userId) {
        SysUser user = userService.getById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return loadUserByUsername(user.getUsername());
    }
}