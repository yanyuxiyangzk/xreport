package com.xreport.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xreport.pojo.dto.UserLoginRequest;
import com.xreport.pojo.dto.UserLoginResponse;
import com.xreport.service.auth.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Authentication Integration Test
 * 测试 JWT 认证流程：登录、Token验证、Token刷新、权限验证
 */
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("认证流程集成测试")
public class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IAuthService authService;

    private TestSecurityHelper securityHelper;

    private static final String TEST_USERNAME = "admin";
    private static final String TEST_PASSWORD = "admin123";

    @BeforeEach
    void setUp() {
        securityHelper = new TestSecurityHelper(mockMvc, objectMapper, authService, null);
    }

    @Test
    @Order(1)
    @DisplayName("测试登录接口 - 成功登录")
    void testLoginSuccess() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                .andReturn();

        // 输出测试报告
        System.out.println("========================================");
        System.out.println("测试报告: 登录接口 - 成功登录");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/auth/login");
        System.out.println("用户名: " + TEST_USERNAME);
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("Token: " + getTokenFromResult(result));
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(2)
    @DisplayName("测试登录接口 - 密码错误")
    void testLoginWrongPassword() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword("wrongpassword");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 登录接口 - 密码错误");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/auth/login");
        System.out.println("用户名: " + TEST_USERNAME);
        System.out.println("密码: wrongpassword");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应码: 401");
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(3)
    @DisplayName("测试登录接口 - 用户不存在")
    void testLoginUserNotFound() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("nonexistentuser");
        request.setPassword("anypassword");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 登录接口 - 用户不存在");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/auth/login");
        System.out.println("用户名: nonexistentuser");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应码: 401");
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(4)
    @DisplayName("测试Token验证 - 获取用户信息")
    void testGetUserInfo() throws Exception {
        // First login to get token
        String token = loginAndGetToken();

        // Then use token to get user info
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/auth/userinfo")
                .header("Authorization", "Bearer " + token);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: Token验证 - 获取用户信息");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/auth/userinfo");
        System.out.println("Token: " + token);
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("用户名: " + TEST_USERNAME);
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(5)
    @DisplayName("测试Token验证 - 无Token访问受保护资源")
    void testUnauthorizedAccess() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/auth/userinfo");

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> response = objectMapper.readValue(content, Map.class);

        // 未登录情况下获取用户信息应该返回错误
        // Spring Security 返回 401 或返回 code=500（取决于配置）
        System.out.println("========================================");
        System.out.println("测试报告: Token验证 - 无Token访问");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/auth/userinfo");
        System.out.println("Token: 无");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应码: " + response.get("code"));
        System.out.println("测试结果: 通过（正确拒绝未授权访问）");
        System.out.println("========================================");
    }

    @Test
    @Order(6)
    @DisplayName("测试Token验证 - 无效Token")
    void testInvalidToken() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/auth/userinfo")
                .header("Authorization", "Bearer invalid.token.here");

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> response = objectMapper.readValue(content, Map.class);

        System.out.println("========================================");
        System.out.println("测试报告: Token验证 - 无效Token");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/auth/userinfo");
        System.out.println("Token: invalid.token.here");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应码: " + response.get("code"));
        System.out.println("测试结果: 通过（正确拒绝无效Token）");
        System.out.println("========================================");
    }

    @Test
    @Order(7)
    @DisplayName("测试Token验证 - 过期Token")
    void testExpiredToken() throws Exception {
        // 生成一个过期的token（使用负的过期时间）
        String expiredToken = com.xreport.common.util.JwtUtils.generateToken(
                1L, "admin",
                "xreport-jwt-secret-key-must-be-at-least-512-bits-long-for-hs512algorithm",
                -1000L // 负的过期时间，导致立即过期
        );

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/auth/userinfo")
                .header("Authorization", "Bearer " + expiredToken);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> response = objectMapper.readValue(content, Map.class);

        System.out.println("========================================");
        System.out.println("测试报告: Token验证 - 过期Token");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/auth/userinfo");
        System.out.println("Token: [已过期]");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应码: " + response.get("code"));
        System.out.println("测试结果: 通过（正确拒绝过期Token）");
        System.out.println("========================================");
    }

    @Test
    @Order(8)
    @DisplayName("测试登出接口")
    void testLogout() throws Exception {
        // Login first
        String token = loginAndGetToken();

        // Then logout
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/auth/logout")
                .header("Authorization", "Bearer " + token);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 登出接口");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/auth/logout");
        System.out.println("Token: " + token);
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    /**
     * 测试公开接口 - 不需要认证即可访问
     */
    @Test
    @Order(9)
    @DisplayName("测试公开接口 - 登录接口无需认证")
    void testPublicEndpointNoAuthRequired() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        // 不带任何token，应该可以访问登录接口
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());

        System.out.println("========================================");
        System.out.println("测试报告: 公开接口 - 登录无需认证");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/auth/login (无Token)");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过（公开接口正确开放）");
        System.out.println("========================================");
    }

    // ==================== Helper Methods ====================

    /**
     * 执行登录并获取Token
     */
    private String loginAndGetToken() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String content = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(content, Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");
        return (String) dataMap.get("token");
    }

    /**
     * 从MvcResult中提取Token
     */
    private String getTokenFromResult(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(content, Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");
        return dataMap != null ? (String) dataMap.get("token") : null;
    }
}
