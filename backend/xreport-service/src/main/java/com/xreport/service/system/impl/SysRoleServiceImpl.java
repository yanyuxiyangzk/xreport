package com.xreport.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.SysRoleMapper;
import com.xreport.mapper.SysRoleMenuMapper;
import com.xreport.pojo.dto.SysRoleDto;
import com.xreport.pojo.entity.SysRole;
import com.xreport.pojo.entity.SysRoleMenu;
import com.xreport.service.system.ISysRoleService;
import com.xreport.common.util.IdWorker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysRoleServiceImpl implements ISysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;

    public SysRoleServiceImpl(SysRoleMapper roleMapper, SysRoleMenuMapper roleMenuMapper) {
        this.roleMapper = roleMapper;
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    public PageInfo<SysRole> pageQuery(SysRoleDto dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (dto.getRoleName() != null && !dto.getRoleName().isEmpty()) {
            wrapper.like(SysRole::getRoleName, dto.getRoleName());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, dto.getStatus());
        }
        wrapper.eq(SysRole::getDelFlag, 0).orderByAsc(SysRole::getRoleSort);
        List<SysRole> list = roleMapper.selectList(wrapper);
        return new PageInfo<>(list);
    }

    @Override
    public SysRole getById(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null || role.getDelFlag() == 1) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    @Override
    public void add(SysRole role) {
        role.setId(IdWorker.nextId());
        role.setDelFlag(0);
        role.setTenantId(1L);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.insert(role);
    }

    @Override
    public void update(SysRole role) {
        SysRole existing = getById(role.getId());
        existing.setRoleName(role.getRoleName());
        existing.setRoleKey(role.getRoleKey());
        existing.setRoleSort(role.getRoleSort());
        existing.setStatus(role.getStatus());
        existing.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(existing);
    }

    @Override
    public void delete(Long id) {
        SysRole role = getById(id);
        role.setDelFlag(1);
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(role);
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> list = roleMenuMapper.selectList(wrapper);
        List<Long> menuIds = new ArrayList<>();
        for (SysRoleMenu rm : list) {
            menuIds.add(rm.getMenuId());
        }
        return menuIds;
    }

    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(wrapper);

        if (menuIds != null && !menuIds.isEmpty()) {
            List<SysRoleMenu> list = new ArrayList<>();
            for (Long menuId : menuIds) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setId(IdWorker.nextId());
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                rm.setTenantId(1L);
                rm.setCreateTime(LocalDateTime.now());
                list.add(rm);
            }
            for (SysRoleMenu rm : list) { roleMenuMapper.insert(rm); }
        }
    }
}
