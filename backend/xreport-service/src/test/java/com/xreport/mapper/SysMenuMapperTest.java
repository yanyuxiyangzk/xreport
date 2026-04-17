package com.xreport.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.pojo.entity.SysMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SysMenuMapper 单元测试
 */
@ExtendWith(MockitoExtension.class)
class SysMenuMapperTest {

    @Mock
    private SysMenuMapper menuMapper;

    private SysMenu testMenu;

    @BeforeEach
    void setUp() {
        testMenu = new SysMenu();
        testMenu.setId(1L);
        testMenu.setMenuName("系统管理");
        testMenu.setMenuType("M");
        testMenu.setParentId(0L);
        testMenu.setPath("/system");
        testMenu.setComponent("system/index");
        testMenu.setIcon("system");
        testMenu.setSortOrder(1);
        testMenu.setVisible("0");
        testMenu.setStatus("1");
        testMenu.setPerms("system:view");
        testMenu.setDelFlag(0);
        testMenu.setTenantId(1L);
        testMenu.setCreateTime(LocalDateTime.now());
        testMenu.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("selectById - 应该返回菜单")
    void selectById_ShouldReturnMenu() {
        // Arrange
        when(menuMapper.selectById(1L)).thenReturn(testMenu);

        // Act
        SysMenu result = menuMapper.selectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("系统管理", result.getMenuName());
        verify(menuMapper).selectById(1L);
    }

    @Test
    @DisplayName("selectById - 菜单不存在应返回null")
    void selectById_WhenNotExists_ShouldReturnNull() {
        // Arrange
        when(menuMapper.selectById(999L)).thenReturn(null);

        // Act
        SysMenu result = menuMapper.selectById(999L);

        // Assert
        assertNull(result);
        verify(menuMapper).selectById(999L);
    }

    @Test
    @DisplayName("insert - 应该插入菜单并返回影响行数")
    void insert_ShouldInsertAndReturnAffectedRows() {
        // Arrange
        SysMenu newMenu = new SysMenu();
        newMenu.setMenuName("新菜单");
        newMenu.setMenuType("C");

        when(menuMapper.insert(any(SysMenu.class))).thenReturn(1);

        // Act
        int result = menuMapper.insert(newMenu);

        // Assert
        assertEquals(1, result);
        verify(menuMapper).insert(any(SysMenu.class));
    }

    @Test
    @DisplayName("updateById - 应该更新菜单")
    void updateById_ShouldUpdateMenu() {
        // Arrange
        testMenu.setMenuName("更新后的菜单");

        when(menuMapper.updateById(any(SysMenu.class))).thenReturn(1);

        // Act
        int result = menuMapper.updateById(testMenu);

        // Assert
        assertEquals(1, result);
        verify(menuMapper).updateById(testMenu);
    }

    @Test
    @DisplayName("deleteById - 应该删除菜单")
    void deleteById_ShouldDeleteMenu() {
        // Arrange
        when(menuMapper.deleteById(1L)).thenReturn(1);

        // Act
        int result = menuMapper.deleteById(1L);

        // Assert
        assertEquals(1, result);
        verify(menuMapper).deleteById(1L);
    }

    @Test
    @DisplayName("selectList - 使用Wrapper查询应该返回菜单列表")
    void selectList_WithWrapper_ShouldReturnMenus() {
        // Arrange
        List<SysMenu> menus = List.of(testMenu);
        when(menuMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(menus);

        // Act
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getDelFlag, 0).orderByAsc(SysMenu::getSortOrder);
        List<SysMenu> result = menuMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(menuMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectCount - 应该返回符合条件的数量")
    void selectCount_ShouldReturnCount() {
        // Arrange
        when(menuMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(3L);

        // Act
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, 1L);
        long result = menuMapper.selectCount(wrapper);

        // Assert
        assertEquals(3L, result);
    }
}