package com.xreport.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xreport.common.util.JwtUtils;
import com.xreport.pojo.dto.UserLoginRequest;
import com.xreport.pojo.dto.UserLoginResponse;
import com.xreport.pojo.entity.SysUser;
import com.xreport.service.auth.IAuthService;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test Security Helper - 提供集成测试的安全辅助方法
 * 用于 JWT 认证流程测试和权限模拟
 */
public class TestSecurityHelper {

    private static final String JWT_SECRET = "xreport-jwt-secret-key-must-be-at-least-512-bits-long-for-hs512";
    private static final long EXPIRE_TIME = 86400000L;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final IAuthService authService;
    private final BCryptPasswordEncoder passwordEncoder;

    public TestSecurityHelper(MockMvc mockMvc, ObjectMapper objectMapper, IAuthService authService, BCryptPasswordEncoder passwordEncoder) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 执行登录并获取 Token
     * @param username 用户名
     * @param password 密码（明文）
     * @return LoginResponse 包含 token 信息
     * @throws Exception 如果登录失败
     */
    public UserLoginResponse loginAndGetToken(String username, String password) throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String content = result.getResponse().getContentAsString();

        // 解析响应获取 token
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(content, Map.class);

        if ((Integer) responseMap.get("code") != 200) {
            throw new RuntimeException("Login failed: " + responseMap.get("message"));
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");

        UserLoginResponse loginResponse = new UserLoginResponse();
        loginResponse.setToken((String) dataMap.get("token"));
        loginResponse.setUsername((String) dataMap.get("username"));
        loginResponse.setNickname((String) dataMap.get("nickname"));
        loginResponse.setUserId(((Number) dataMap.get("userId")).longValue());
        loginResponse.setExpireTime(((Number) dataMap.get("expireTime")).longValue());

        return loginResponse;
    }

    /**
     * 使用 MockMvc 执行带 JWT Token 的请求
     * @param token JWT Token
     * @return 带有 Authorization header 的请求后处理器
     */
    public static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtToken(String token) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt
                        .subject("1")
                        .claim("username", "admin"));
    }

    /**
     * 使用 MockMvc 执行带自定义用户ID的请求
     * @param userId 用户ID
     * @param authorities 权限列表
     * @return 带有 JWT 的请求后处理器
     */
    public static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor mockJwt(Long userId, List<String> authorities) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt
                        .subject(String.valueOf(userId))
                        .claim("userId", userId)
                )
                .authorities(authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toArray(SimpleGrantedAuthority[]::new));
    }

    /**
     * 生成 JWT Token（不经过登录流程）
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT Token 字符串
     */
    public static String generateToken(Long userId, String username) {
        return JwtUtils.generateToken(userId, username, JWT_SECRET, EXPIRE_TIME);
    }

    /**
     * 验证 Token 是否有效
     * @param token JWT Token
     * @return true 如果有效，false 如果过期或无效
     */
    public static boolean isTokenValid(String token) {
        return !JwtUtils.isTokenExpired(token, JWT_SECRET);
    }

    /**
     * 从 Token 中获取用户ID
     * @param token JWT Token
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token) {
        return JwtUtils.getUserIdFromToken(token, JWT_SECRET);
    }

    /**
     * 创建 BCrypt 加密的密码
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 验证密码是否匹配
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @return true 如果匹配
     */
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 创建模拟用户对象（不保存到数据库）
     * @param id 用户ID
     * @param username 用户名
     * @param nickname 昵称
     * @return SysUser 实体
     */
    public SysUser createMockUser(Long id, String username, String nickname) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPassword(passwordEncoder.encode("password"));
        user.setStatus(1);
        user.setDelFlag(0);
        user.setTenantId(1L);
        return user;
    }

    /**
     * 设置 Security Context（用于测试）
     * @param userId 用户ID
     * @param authorities 权限列表
     * @deprecated Use mockJwt() method with .with() instead
     */
    @Deprecated
    public static void setSecurityContext(Long userId, List<String> authorities) {
        // This method is deprecated. Use mockJwt(userId, authorities) with .with() in actual tests.
        // Example: mockMvc.perform(get("/api/users").with(mockJwt(1L, Arrays.asList("ROLE_ADMIN"))));
    }

    /**
     * 创建测试用的 JWT Authentication 头
     * @param token JWT Token
     * @return Authorization 头值
     */
    public static String createAuthHeader(String token) {
        return "Bearer " + token;
    }
}
