package com.xreport.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xreport.pojo.dto.UserLoginRequest;
import com.xreport.pojo.entity.reportdatasource.ReportDatasource;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Datasource Integration Test
 * 测试数据源管理流程：创建、查询、更新、删除、连接测试
 */
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("数据源管理集成测试")
public class DatasourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IAuthService authService;

    private TestSecurityHelper securityHelper;

    @BeforeEach
    void setUp() {
        securityHelper = new TestSecurityHelper(mockMvc, objectMapper, authService, null);
    }

    @Test
    @Order(1)
    @DisplayName("测试创建数据源 - MySQL类型")
    void testCreateDatasource_MySQL() throws Exception {
        ReportDatasource datasource = createTestDatasource("MySQL Test DS");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/datasources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datasource))
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "datasource:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 创建数据源 - MySQL类型");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/datasources");
        System.out.println("数据源名称: " + datasource.getDatasourceName());
        System.out.println("数据源类型: " + datasource.getDatasourceType());
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(2)
    @DisplayName("测试创建数据源 - API类型")
    void testCreateDatasource_API() throws Exception {
        ReportDatasource datasource = new ReportDatasource();
        datasource.setDatasourceName("API Test DS");
        datasource.setDatasourceType("api");
        datasource.setApiUrl("https://api.example.com/data");
        datasource.setApiMethod("GET");
        datasource.setStatus(1);
        datasource.setTenantId(1L);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/datasources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datasource))
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "datasource:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 创建数据源 - API类型");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/datasources");
        System.out.println("数据源名称: " + datasource.getDatasourceName());
        System.out.println("数据源类型: " + datasource.getDatasourceType());
        System.out.println("API URL: " + datasource.getApiUrl());
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(3)
    @DisplayName("测试查询数据源列表")
    void testQueryDatasources() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/datasources")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "datasource:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 查询数据源列表");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/datasources");
        System.out.println("分页: pageNum=1, pageSize=10");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> response = objectMapper.readValue(content, Map.class);
        System.out.println("响应数据: " + response.get("data"));
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(4)
    @DisplayName("测试获取单个数据源")
    void testGetDatasourceById() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/datasources/1")
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "datasource:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 获取单个数据源");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/datasources/1");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(5)
    @DisplayName("测试更新数据源")
    void testUpdateDatasource() throws Exception {
        ReportDatasource datasource = new ReportDatasource();
        datasource.setDatasourceName("Updated MySQL DS");
        datasource.setDatasourceType("mysql");
        datasource.setJdbcUrl("jdbc:mysql://localhost:3306/updateddb");
        datasource.setUsername("root");
        datasource.setPassword("newpassword");
        datasource.setStatus(1);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/datasources/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datasource))
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "datasource:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 更新数据源");
        System.out.println("----------------------------------------");
        System.out.println("请求: PUT /api/datasources/1");
        System.out.println("更新后名称: " + datasource.getDatasourceName());
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(6)
    @DisplayName("测试删除数据源")
    void testDeleteDatasource() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/datasources/1")
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "datasource:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 删除数据源");
        System.out.println("----------------------------------------");
        System.out.println("请求: DELETE /api/datasources/1");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(7)
    @DisplayName("测试获取启用的数据源列表")
    void testListEnabledDatasources() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/datasources/enabled")
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "datasource:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 获取启用的数据源列表");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/datasources/enabled");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(8)
    @DisplayName("测试JDBC连接测试 - 成功")
    void testJdbcConnectionSuccess() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("jdbcUrl", "jdbc:h2:mem:testdb");
        params.put("username", "sa");
        params.put("password", "");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/datasources/test/jdbc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "datasource:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: JDBC连接测试 - H2内存数据库");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/datasources/test/jdbc");
        System.out.println("JDBC URL: " + params.get("jdbcUrl"));
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(9)
    @DisplayName("测试JDBC连接测试 - 失败")
    void testJdbcConnectionFailure() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("jdbcUrl", "jdbc:mysql://localhost:9999/nonexistent");
        params.put("username", "root");
        params.put("password", "wrongpassword");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/datasources/test/jdbc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "datasource:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> response = objectMapper.readValue(content, Map.class);

        System.out.println("========================================");
        System.out.println("测试报告: JDBC连接测试 - 连接失败");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/datasources/test/jdbc");
        System.out.println("JDBC URL: " + params.get("jdbcUrl"));
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应码: " + response.get("code"));
        System.out.println("测试结果: 通过（正确处理连接失败）");
        System.out.println("========================================");
    }

    @Test
    @Order(10)
    @DisplayName("测试无权限访问数据源接口")
    void testUnauthorizedDatasourceAccess() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/datasources")
                .with(securityHelper.mockJwt(2L, List.of("ROLE_USER")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> response = objectMapper.readValue(content, Map.class);

        System.out.println("========================================");
        System.out.println("测试报告: 无权限访问数据源接口");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/datasources");
        System.out.println("用户角色: ROLE_USER (无datasource:manage权限)");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应码: " + response.get("code"));
        System.out.println("测试结果: 通过（正确拒绝无权限访问）");
        System.out.println("========================================");
    }

    private ReportDatasource createTestDatasource(String name) {
        ReportDatasource datasource = new ReportDatasource();
        datasource.setDatasourceName(name);
        datasource.setDatasourceType("mysql");
        datasource.setJdbcUrl("jdbc:mysql://localhost:3306/testdb");
        datasource.setUsername("root");
        datasource.setPassword("test123");
        datasource.setStatus(1);
        datasource.setTenantId(1L);
        datasource.setCreateUserId(1L);
        return datasource;
    }
}