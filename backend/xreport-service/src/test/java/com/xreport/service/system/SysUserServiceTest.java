package com.xreport.service.system;

import com.github.pagehelper.PageInfo;
import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.SysUserMapper;
import com.xreport.mapper.SysUserRoleMapper;
import com.xreport.pojo.dto.SysUserDto;
import com.xreport.pojo.entity.SysUser;
import com.xreport.pojo.entity.SysUserRole;
import com.xreport.service.system.impl.SysUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * SysUserService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class SysUserServiceTest {

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SysUserRoleMapper userRoleMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private SysUserServiceImpl userService;

    private SysUser testUser;

    @BeforeEach
    void setUp() {
        userService = new SysUserServiceImpl(userMapper, userRoleMapper, passwordEncoder);

        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword("$2a$10$encoded_password");
        testUser.setNickname("管理员");
        testUser.setEmail("admin@example.com");
        testUser.setPhone("13800138000");
        testUser.setStatus(1);
        testUser.setDelFlag(0);
        testUser.setTenantId(1L);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("根据用户名查询用户 - 成功")
    void getByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userMapper.selectOne(any())).thenReturn(testUser);

        // Act
        SysUser result = userService.getByUsername("admin");

        // Assert
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals("管理员", result.getNickname());
        verify(userMapper).selectOne(any());
    }

    @Test
    @DisplayName("根据用户名查询用户 - 用户不存在")
    void getByUsername_WhenUserNotExists_ShouldReturnNull() {
        // Arrange
        when(userMapper.selectOne(any())).thenReturn(null);

        // Act
        SysUser result = userService.getByUsername("nonexistent");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("分页查询用户列表")
    void pageQuery_ShouldReturnPagedUsers() {
        // Arrange
        SysUserDto dto = new SysUserDto();
        dto.setPageNum(1);
        dto.setPageSize(10);

        List<SysUser> users = Arrays.asList(testUser);
        when(userMapper.selectList(any())).thenReturn(users);

        // Act
        PageInfo<SysUser> result = userService.pageQuery(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals("admin", result.getList().get(0).getUsername());
    }

    @Test
    @DisplayName("分页查询用户列表 - 按用户名筛选")
    void pageQuery_WithUsernameFilter_ShouldReturnFilteredUsers() {
        // Arrange
        SysUserDto dto = new SysUserDto();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setUsername("admin");

        List<SysUser> users = Arrays.asList(testUser);
        when(userMapper.selectList(any())).thenReturn(users);

        // Act
        PageInfo<SysUser> result = userService.pageQuery(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSize());
    }

    @Test
    @DisplayName("根据ID查询用户 - 成功")
    void getById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // Act
        SysUser result = userService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("admin", result.getUsername());
    }

    @Test
    @DisplayName("根据ID查询用户 - 用户不存在")
    void getById_WhenUserNotExists_ShouldThrowException() {
        // Arrange
        when(userMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getById(999L);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    @DisplayName("根据ID查询用户 - 用户已删除")
    void getById_WhenUserDeleted_ShouldThrowException() {
        // Arrange
        testUser.setDelFlag(1);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getById(1L);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    @DisplayName("新增用户 - 成功")
    void add_WhenUsernameNotExists_ShouldCreateUser() {
        // Arrange
        SysUser newUser = new SysUser();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setNickname("新用户");
        newUser.setEmail("new@example.com");

        when(userMapper.selectOne(any())).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded_new_password");

        try (MockedStatic<com.xreport.common.util.IdWorker> idWorkerMock =
                     mockStatic(com.xreport.common.util.IdWorker.class)) {
            idWorkerMock.when(com.xreport.common.util.IdWorker::nextId).thenReturn(100L);

            // Act
            userService.add(newUser, null);

            // Assert
            assertEquals(100L, newUser.getId());
            assertEquals(0, newUser.getDelFlag());
            assertEquals(1, newUser.getStatus());
            assertEquals(1L, newUser.getTenantId());
            verify(userMapper).insert(newUser);
        }
    }

    @Test
    @DisplayName("新增用户 - 用户名已存在")
    void add_WhenUsernameExists_ShouldThrowException() {
        // Arrange
        SysUser newUser = new SysUser();
        newUser.setUsername("admin");
        newUser.setPassword("password123");

        when(userMapper.selectOne(any())).thenReturn(testUser);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.add(newUser, null);
        });

        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    @DisplayName("新增用户 - 包含角色")
    void add_WithRoles_ShouldCreateUserAndRoles() {
        // Arrange
        SysUser newUser = new SysUser();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setNickname("新用户");

        List<Long> roleIds = Arrays.asList(1L, 2L);

        when(userMapper.selectOne(any())).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded_password");

        try (MockedStatic<com.xreport.common.util.IdWorker> idWorkerMock =
                     mockStatic(com.xreport.common.util.IdWorker.class)) {
            idWorkerMock.when(com.xreport.common.util.IdWorker::nextId)
                    .thenReturn(100L)
                    .thenReturn(200L)
                    .thenReturn(201L);

            // Act
            userService.add(newUser, roleIds);

            // Assert
            assertEquals(100L, newUser.getId());
            verify(userMapper).insert(newUser);
            verify(userRoleMapper, times(2)).insert(any(SysUserRole.class));
        }
    }

    @Test
    @DisplayName("更新用户信息")
    void update_ShouldUpdateUserFields() {
        // Arrange
        SysUser updateUser = new SysUser();
        updateUser.setId(1L);
        updateUser.setNickname("更新后的昵称");
        updateUser.setEmail("updated@example.com");

        when(userMapper.selectById(1L)).thenReturn(testUser);

        // Act
        userService.update(updateUser, null);

        // Assert
        assertEquals("更新后的昵称", testUser.getNickname());
        assertEquals("updated@example.com", testUser.getEmail());
        verify(userMapper).updateById(testUser);
    }

    @Test
    @DisplayName("更新用户 - 同时更新角色")
    void update_WithRoles_ShouldUpdateUserAndRoles() {
        // Arrange
        SysUser updateUser = new SysUser();
        updateUser.setId(1L);
        updateUser.setNickname("管理员");
        updateUser.setEmail("admin@example.com");

        List<Long> newRoleIds = Arrays.asList(2L, 3L);

        when(userMapper.selectById(1L)).thenReturn(testUser);

        // Act
        userService.update(updateUser, newRoleIds);

        // Assert
        verify(userRoleMapper).delete(any());
        verify(userRoleMapper, times(2)).insert(any(SysUserRole.class));
    }

    @Test
    @DisplayName("删除用户 - 软删除")
    void delete_ShouldSetDelFlagToOne() {
        // Arrange
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // Act
        userService.delete(1L);

        // Assert
        assertEquals(1, testUser.getDelFlag());
        verify(userMapper).updateById(testUser);
    }

    @Test
    @DisplayName("重置密码")
    void resetPassword_ShouldEncodeAndUpdatePassword() {
        // Arrange
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.encode("newpassword")).thenReturn("$2a$10$new_encoded_password");

        // Act
        userService.resetPassword(1L, "newpassword");

        // Assert
        assertEquals("$2a$10$new_encoded_password", testUser.getPassword());
        verify(userMapper).updateById(testUser);
    }

    @Test
    @DisplayName("更新用户状态")
    void updateStatus_ShouldUpdateStatusField() {
        // Arrange
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // Act
        userService.updateStatus(1L, 0);

        // Assert
        assertEquals(0, testUser.getStatus());
        verify(userMapper).updateById(testUser);
    }

    @Test
    @DisplayName("分配角色 - 替换现有角色")
    void assignRoles_ShouldDeleteOldAndInsertNew() {
        // Arrange
        List<Long> newRoleIds = Arrays.asList(1L, 2L);

        // Act
        try (MockedStatic<com.xreport.common.util.IdWorker> idWorkerMock =
                     mockStatic(com.xreport.common.util.IdWorker.class)) {
            idWorkerMock.when(com.xreport.common.util.IdWorker::nextId)
                    .thenReturn(100L)
                    .thenReturn(101L);

            userService.assignRoles(1L, newRoleIds);

            // Assert
            verify(userRoleMapper).delete(any());
            verify(userRoleMapper, times(2)).insert(any(SysUserRole.class));
        }
    }

    @Test
    @DisplayName("分配角色 - 传入空列表应清除所有角色")
    void assignRoles_WithEmptyList_ShouldDeleteAllRoles() {
        // Act
        userService.assignRoles(1L, Arrays.asList());

        // Assert
        verify(userRoleMapper).delete(any());
        verify(userRoleMapper, never()).insert(any(SysUserRole.class));
    }
}