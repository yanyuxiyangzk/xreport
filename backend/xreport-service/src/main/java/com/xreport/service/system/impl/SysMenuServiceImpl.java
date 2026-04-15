package com.xreport.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.SysMenuMapper;
import com.xreport.pojo.entity.SysMenu;
import com.xreport.service.system.ISysMenuService;
import com.xreport.common.util.IdWorker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysMenuServiceImpl implements ISysMenuService {

    private final SysMenuMapper menuMapper;

    public SysMenuServiceImpl(SysMenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    @Override
    public List<SysMenu> getTree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getDelFlag, 0).orderByAsc(SysMenu::getSortOrder);
        return menuMapper.selectList(wrapper);
    }

    @Override
    public SysMenu getById(Long id) {
        SysMenu menu = menuMapper.selectById(id);
        if (menu == null || menu.getDelFlag() == 1) {
            throw new BusinessException("菜单不存在");
        }
        return menu;
    }

    @Override
    public void add(SysMenu menu) {
        menu.setId(IdWorker.nextId());
        menu.setDelFlag(0);
        menu.setTenantId(1L);
        menu.setCreateTime(LocalDateTime.now());
        menu.setUpdateTime(LocalDateTime.now());
        menuMapper.insert(menu);
    }

    @Override
    public void update(SysMenu menu) {
        SysMenu existing = getById(menu.getId());
        existing.setParentId(menu.getParentId());
        existing.setMenuType(menu.getMenuType());
        existing.setMenuName(menu.getMenuName());
        existing.setPath(menu.getPath());
        existing.setComponent(menu.getComponent());
        existing.setIcon(menu.getIcon());
        existing.setIsFrame(menu.getIsFrame());
        existing.setIsCache(menu.getIsCache());
        existing.setSortOrder(menu.getSortOrder());
        existing.setVisible(menu.getVisible());
        existing.setPerms(menu.getPerms());
        existing.setStatus(menu.getStatus());
        existing.setUpdateTime(LocalDateTime.now());
        menuMapper.updateById(existing);
    }

    @Override
    public void delete(Long id) {
        SysMenu menu = getById(id);
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id).eq(SysMenu::getDelFlag, 0);
        long count = menuMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("存在下级菜单，无法删除");
        }
        menu.setDelFlag(1);
        menu.setUpdateTime(LocalDateTime.now());
        menuMapper.updateById(menu);
    }
}
