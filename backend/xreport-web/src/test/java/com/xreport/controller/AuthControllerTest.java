package com.xreport.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xreport.controller.auth.AuthController;
import com.xreport.pojo.dto.UserLoginRequest;
import com.xreport.pojo.dto.UserLoginResponse;
import com.xreport.pojo.entity.SysUser;
import com.xreport.service.auth.IAuthService;
import com.xreport.service.system.ISysUserService;
import com.xreport.integration.TestApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.xreport.integration.TestSecurityHelper.mockJwt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController Test
 * 测试认证接口：login, logout, getCurrentUser
 */
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("认证控制器测试")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IAuthService authService;

    @MockBean
    private ISysUserService userService;

    @Test
    @DisplayName("测试登录 - 成功")
    void testLoginSuccess() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        UserLoginResponse response = new UserLoginResponse(
                "test-jwt-token",
                "admin",
                "Administrator",
                1L,
                System.currentTimeMillis() + 3600000
        );

        when(authService.login(any(UserLoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.data.username").value("admin"));

        verify(authService).login(any(UserLoginRequest.class));
    }

    @Test
    @DisplayName("测试登录 - 用户名密码错误")
    void testLoginFailure() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("admin");
        request.setPassword("wrongpassword");

        when(authService.login(any(UserLoginRequest.class)))
                .thenThrow(new com.xreport.common.exception.BusinessException(401, "用户名或密码错误"));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("测试登出")
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .with(mockJwt(1L, java.util.Arrays.asList("ROLE_ADMIN")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(authService).logout();
    }

    @Test
    @DisplayName("测试获取当前用户信息")
    void testGetCurrentUser() throws Exception {
        Long userId = 1L;

        SysUser user = new SysUser();
        user.setId(userId);
        user.setUsername("admin");
        user.setNickname("Administrator");
        user.setEmail("admin@example.com");
        user.setCreateTime(java.time.LocalDateTime.now());

        when(userService.getById(userId)).thenReturn(user);

        mockMvc.perform(get("/api/auth/userinfo")
                        .with(mockJwt(userId, java.util.Arrays.asList("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.nickname").value("Administrator"));

        verify(userService).getById(userId);
    }

    @Test
    @DisplayName("测试未登录获取用户信息")
    void testGetCurrentUserUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/userinfo"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("测试登录 - 缺少用户名")
    void testLoginMissingUsername() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setPassword("admin123");

        when(authService.login(any(UserLoginRequest.class)))
                .thenThrow(new com.xreport.common.exception.BusinessException(400, "用户名不能为空"));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }
}
