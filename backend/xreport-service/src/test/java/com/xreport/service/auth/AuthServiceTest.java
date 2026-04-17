package com.xreport.service.auth;

import com.xreport.common.exception.BusinessException;
import com.xreport.pojo.dto.UserLoginRequest;
import com.xreport.pojo.dto.UserLoginResponse;
import com.xreport.pojo.entity.SysUser;
import com.xreport.service.auth.impl.AuthServiceImpl;
import com.xreport.service.system.ISysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ISysUserService userService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private AuthServiceImpl authService;

    private SysUser testUser;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userService, passwordEncoder);

        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword("$2a$10$encoded_password");
        testUser.setNickname("管理员");
        testUser.setStatus(1);
        testUser.setDelFlag(0);
        testUser.setTenantId(1L);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("登录成功 - 返回token和用户信息")
    void login_Success() {
        // Arrange
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("admin");
        request.setPassword("password123");

        when(userService.getByUsername("admin")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);

        try (MockedStatic<com.xreport.common.util.JwtUtils> jwtUtilsMock = mockStatic(com.xreport.common.util.JwtUtils.class)) {
            jwtUtilsMock.when(() -> com.xreport.common.util.JwtUtils.generateToken(
                    anyLong(), anyString(), anyString(), anyLong()))
                    .thenReturn("mocked.jwt.token");

            // Act
            UserLoginResponse response = authService.login(request);

            // Assert
            assertNotNull(response);
            assertEquals("mocked.jwt.token", response.getToken());
            assertEquals("admin", response.getUsername());
            assertEquals("管理员", response.getNickname());
            assertEquals(1L, response.getUserId());
            assertNotNull(response.getExpireTime());

            verify(userService).getByUsername("admin");
            verify(passwordEncoder).matches("password123", testUser.getPassword());
        }
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void login_UserNotFound() {
        // Arrange
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password");

        when(userService.getByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(request);
        });

        assertEquals(401, exception.getCode());
        assertEquals("用户名或密码错误", exception.getMessage());
        verify(userService).getByUsername("nonexistent");
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("登录失败 - 用户已被禁用")
    void login_UserDisabled() {
        // Arrange
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("admin");
        request.setPassword("password123");

        testUser.setStatus(0); // 禁用状态
        when(userService.getByUsername("admin")).thenReturn(testUser);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(request);
        });

        assertEquals(401, exception.getCode());
        assertEquals("账号已被禁用", exception.getMessage());
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void login_WrongPassword() {
        // Arrange
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("admin");
        request.setPassword("wrongpassword");

        when(userService.getByUsername("admin")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(request);
        });

        assertEquals(401, exception.getCode());
        assertEquals("用户名或密码错误", exception.getMessage());
        verify(passwordEncoder).matches("wrongpassword", testUser.getPassword());
    }

    @Test
    @DisplayName("登出 - JWT无状态实现，无需处理")
    void logout_ShouldDoNothing() {
        // Act & Assert - JWT is stateless, logout is handled by client
        assertDoesNotThrow(() -> authService.logout());
        verifyNoInteractions(userService);
        verifyNoInteractions(passwordEncoder);
    }
}