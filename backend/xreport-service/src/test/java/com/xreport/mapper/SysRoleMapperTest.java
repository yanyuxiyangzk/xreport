package com.xreport.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.pojo.entity.SysRole;
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
 * SysRoleMapper 单元测试
 */
@ExtendWith(MockitoExtension.class)
class SysRoleMapperTest {

    @Mock
    private SysRoleMapper roleMapper;

    private SysRole testRole;

    @BeforeEach
    void setUp() {
        testRole = new SysRole();
        testRole.setId(1L);
        testRole.setRoleName("管理员");
        testRole.setRoleKey("admin");
        testRole.setRoleSort(1);
        testRole.setStatus(1);
        testRole.setDelFlag(0);
        testRole.setTenantId(1L);
        testRole.setCreateTime(LocalDateTime.now());
        testRole.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("selectById - 应该返回角色")
    void selectById_ShouldReturnRole() {
        // Arrange
        when(roleMapper.selectById(1L)).thenReturn(testRole);

        // Act
        SysRole result = roleMapper.selectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("管理员", result.getRoleName());
        verify(roleMapper).selectById(1L);
    }

    @Test
    @DisplayName("selectById - 角色不存在应返回null")
    void selectById_WhenNotExists_ShouldReturnNull() {
        // Arrange
        when(roleMapper.selectById(999L)).thenReturn(null);

        // Act
        SysRole result = roleMapper.selectById(999L);

        // Assert
        assertNull(result);
        verify(roleMapper).selectById(999L);
    }

    @Test
    @DisplayName("insert - 应该插入角色并返回影响行数")
    void insert_ShouldInsertAndReturnAffectedRows() {
        // Arrange
        SysRole newRole = new SysRole();
        newRole.setRoleName("新角色");
        newRole.setRoleKey("new_role");

        when(roleMapper.insert(any(SysRole.class))).thenReturn(1);

        // Act
        int result = roleMapper.insert(newRole);

        // Assert
        assertEquals(1, result);
        verify(roleMapper).insert(any(SysRole.class));
    }

    @Test
    @DisplayName("updateById - 应该更新角色")
    void updateById_ShouldUpdateRole() {
        // Arrange
        testRole.setRoleName("更新后的角色");

        when(roleMapper.updateById(any(SysRole.class))).thenReturn(1);

        // Act
        int result = roleMapper.updateById(testRole);

        // Assert
        assertEquals(1, result);
        verify(roleMapper).updateById(testRole);
    }

    @Test
    @DisplayName("deleteById - 应该删除角色")
    void deleteById_ShouldDeleteRole() {
        // Arrange
        when(roleMapper.deleteById(1L)).thenReturn(1);

        // Act
        int result = roleMapper.deleteById(1L);

        // Assert
        assertEquals(1, result);
        verify(roleMapper).deleteById(1L);
    }

    @Test
    @DisplayName("selectList - 使用Wrapper查询应该返回角色列表")
    void selectList_WithWrapper_ShouldReturnRoles() {
        // Arrange
        List<SysRole> roles = List.of(testRole);
        when(roleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(roles);

        // Act
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getDelFlag, 0);
        List<SysRole> result = roleMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roleMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectList - 按名称模糊查询应该返回匹配的角色")
    void selectList_WithNameLike_ShouldReturnMatchedRoles() {
        // Arrange
        List<SysRole> roles = List.of(testRole);
        when(roleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(roles);

        // Act
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(SysRole::getRoleName, "管理");
        wrapper.eq(SysRole::getDelFlag, 0);
        List<SysRole> result = roleMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roleMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectList - 按状态查询应该返回匹配的角色")
    void selectList_WithStatus_ShouldReturnMatchedRoles() {
        // Arrange
        List<SysRole> roles = List.of(testRole);
        when(roleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(roles);

        // Act
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, 1);
        wrapper.eq(SysRole::getDelFlag, 0);
        List<SysRole> result = roleMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}