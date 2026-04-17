package com.xreport.service.system;

import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.SysMenuMapper;
import com.xreport.pojo.entity.SysMenu;
import com.xreport.service.system.impl.SysMenuServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SysMenuService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class SysMenuServiceTest {

    @Mock
    private SysMenuMapper menuMapper;

    private SysMenuServiceImpl menuService;

    private SysMenu testMenu;

    private SysMenu childMenu;

    @BeforeEach
    void setUp() {
        menuService = new SysMenuServiceImpl(menuMapper);

        testMenu = new SysMenu();
        testMenu.setId(1L);
        testMenu.setMenuName("系统管理");
        testMenu.setMenuType("M"); // 目录
        testMenu.setParentId(0L);
        testMenu.setPath("/system");
        testMenu.setComponent("system/index");
        testMenu.setIcon("system");
        testMenu.setSortOrder(1);
        testMenu.setVisible("0"); // 显示
        testMenu.setPerms("system:view");
        testMenu.setStatus("1");
        testMenu.setDelFlag(0);
        testMenu.setTenantId(1L);
        testMenu.setCreateTime(LocalDateTime.now());
        testMenu.setUpdateTime(LocalDateTime.now());

        childMenu = new SysMenu();
        childMenu.setId(2L);
        childMenu.setMenuName("用户管理");
        childMenu.setMenuType("C"); // 菜单
        childMenu.setParentId(1L);
        childMenu.setPath("/system/user");
        childMenu.setComponent("system/user/index");
        childMenu.setIcon("user");
        childMenu.setSortOrder(1);
        childMenu.setVisible("0");
        childMenu.setStatus("1");
        childMenu.setPerms("system:user:view");
        childMenu.setDelFlag(0);
        childMenu.setTenantId(1L);
        childMenu.setCreateTime(LocalDateTime.now());
        childMenu.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("获取菜单树 - 应返回所有启用的菜单")
    void getTree_ShouldReturnAllEnabledMenus() {
        // Arrange
        List<SysMenu> menus = Arrays.asList(testMenu, childMenu);
        when(menuMapper.selectList(any())).thenReturn(menus);

        // Act
        List<SysMenu> result = menuService.getTree();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(menuMapper).selectList(any());
    }

    @Test
    @DisplayName("根据ID查询菜单 - 成功")
    void getById_WhenMenuExists_ShouldReturnMenu() {
        // Arrange
        when(menuMapper.selectById(1L)).thenReturn(testMenu);

        // Act
        SysMenu result = menuService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("系统管理", result.getMenuName());
        verify(menuMapper).selectById(1L);
    }

    @Test
    @DisplayName("根据ID查询菜单 - 菜单不存在")
    void getById_WhenMenuNotExists_ShouldThrowException() {
        // Arrange
        when(menuMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            menuService.getById(999L);
        });

        assertEquals("菜单不存在", exception.getMessage());
    }

    @Test
    @DisplayName("根据ID查询菜单 - 菜单已删除")
    void getById_WhenMenuDeleted_ShouldThrowException() {
        // Arrange
        testMenu.setDelFlag(1);
        when(menuMapper.selectById(1L)).thenReturn(testMenu);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            menuService.getById(1L);
        });

        assertEquals("菜单不存在", exception.getMessage());
    }

    @Test
    @DisplayName("新增菜单")
    void add_ShouldInsertMenu() {
        // Arrange
        SysMenu newMenu = new SysMenu();
        newMenu.setMenuName("新菜单");
        newMenu.setMenuType("C");
        newMenu.setParentId(1L);
        newMenu.setPath("/new");
        newMenu.setComponent("new/index");

        try (MockedStatic<com.xreport.common.util.IdWorker> idWorkerMock =
                     mockStatic(com.xreport.common.util.IdWorker.class)) {
            idWorkerMock.when(com.xreport.common.util.IdWorker::nextId).thenReturn(100L);

            // Act
            menuService.add(newMenu);

            // Assert
            assertEquals(100L, newMenu.getId());
            assertEquals(0, newMenu.getDelFlag());
            assertEquals(1L, newMenu.getTenantId());
            verify(menuMapper).insert(newMenu);
        }
    }

    @Test
    @DisplayName("更新菜单")
    void update_ShouldUpdateMenuFields() {
        // Arrange
        SysMenu updateMenu = new SysMenu();
        updateMenu.setId(1L);
        updateMenu.setMenuName("更新后的菜单");
        updateMenu.setMenuType("M");
        updateMenu.setParentId(0L);
        updateMenu.setPath("/updated");
        updateMenu.setComponent("updated/index");
        updateMenu.setIcon("updated");
        updateMenu.setIsFrame(0);
        updateMenu.setIsCache(0);
        updateMenu.setSortOrder(2);
        updateMenu.setVisible("0");
        updateMenu.setPerms("updated:view");
        updateMenu.setStatus("1");

        when(menuMapper.selectById(1L)).thenReturn(testMenu);

        // Act
        menuService.update(updateMenu);

        // Assert
        assertEquals("更新后的菜单", testMenu.getMenuName());
        assertEquals("/updated", testMenu.getPath());
        assertEquals("updated/index", testMenu.getComponent());
        verify(menuMapper).updateById(testMenu);
    }

    @Test
    @DisplayName("删除菜单 - 存在下级菜单应拒绝")
    void delete_WhenHasChildren_ShouldThrowException() {
        // Arrange
        when(menuMapper.selectById(1L)).thenReturn(testMenu);
        when(menuMapper.selectCount(any())).thenReturn(1L);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            menuService.delete(1L);
        });

        assertEquals("存在下级菜单，无法删除", exception.getMessage());
    }

    @Test
    @DisplayName("删除菜单 - 无下级菜单应软删除")
    void delete_WhenNoChildren_ShouldSoftDelete() {
        // Arrange
        when(menuMapper.selectById(1L)).thenReturn(testMenu);
        when(menuMapper.selectCount(any())).thenReturn(0L);

        // Act
        menuService.delete(1L);

        // Assert
        assertEquals(1, testMenu.getDelFlag());
        verify(menuMapper).updateById(testMenu);
    }
}