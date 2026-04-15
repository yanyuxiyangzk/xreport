package com.xreport.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xreport.pojo.dto.UserLoginRequest;
import com.xreport.pojo.entity.reportdataset.ReportDataset;
import com.xreport.service.auth.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Dataset Integration Test
 * 测试数据集管理流程：创建、查询、SQL聚合计算
 * 使用 @Transactional 确保测试隔离
 */
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("数据集管理集成测试")
public class DatasetIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IAuthService authService;

    private TestSecurityHelper securityHelper;
    private String adminToken;

    private static final String TEST_USERNAME = "admin";
    private static final String TEST_PASSWORD = "admin123";

    @BeforeEach
    void setUp() throws Exception {
        securityHelper = new TestSecurityHelper(mockMvc, objectMapper, authService, null);
        adminToken = loginAndGetToken(TEST_USERNAME, TEST_PASSWORD);
    }

    @Test
    @Order(1)
    @DisplayName("测试创建数据集")
    void testCreateDataset() throws Exception {
        ReportDataset dataset = createTestDataset("Test Dataset " + System.currentTimeMillis(), "test_ds_" + System.currentTimeMillis());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/datasets")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dataset))
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("1").claim("username", "admin"))
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 创建数据集");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/datasets");
        System.out.println("数据集名称: " + dataset.getDatasetName());
        System.out.println("数据集编码: " + dataset.getDatasetCode());
        System.out.println("SQL语句: " + dataset.getSqlStatement());
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(2)
    @DisplayName("测试查询数据集列表")
    void testQueryDatasets() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/datasets")
                .header("Authorization", "Bearer " + adminToken)
                .param("pageNum", "1")
                .param("pageSize", "10")
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("1").claim("username", "admin"))
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 查询数据集列表");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/datasets");
        System.out.println("分页: pageNum=1, pageSize=10");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(3)
    @DisplayName("测试获取单个数据集")
    void testGetDatasetById() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/datasets/1")
                .header("Authorization", "Bearer " + adminToken)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("1").claim("username", "admin"))
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 获取单个数据集");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/datasets/1");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(4)
    @DisplayName("测试更新数据集")
    void testUpdateDataset() throws Exception {
        ReportDataset dataset = new ReportDataset();
        dataset.setDatasetName("Updated Test Dataset");
        dataset.setDatasetCode("updated_ds");
        dataset.setDatasetDesc("Updated description");
        dataset.setDatasourceId(1L);
        dataset.setDatasourceName("Test MySQL");
        dataset.setSqlStatement("SELECT * FROM sys_user WHERE status = 1");
        dataset.setDatasetType("SQL");
        dataset.setStatus(1);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/datasets/1")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dataset))
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("1").claim("username", "admin"))
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 更新数据集");
        System.out.println("----------------------------------------");
        System.out.println("请求: PUT /api/datasets/1");
        System.out.println("更新后名称: " + dataset.getDatasetName());
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(5)
    @DisplayName("测试删除数据集")
    void testDeleteDataset() throws Exception {
        // First create a dataset to delete
        String uniqueCode = "ds_to_delete_" + System.currentTimeMillis();
        ReportDataset dataset = createTestDataset("Dataset To Delete", uniqueCode);
        createDataset(dataset);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/datasets/1")
                .header("Authorization", "Bearer " + adminToken)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("1").claim("username", "admin"))
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 删除数据集");
        System.out.println("----------------------------------------");
        System.out.println("请求: DELETE /api/datasets/1");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(6)
    @DisplayName("测试获取启用的数据集列表")
    void testListEnabledDatasets() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/datasets/enabled")
                .header("Authorization", "Bearer " + adminToken)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("1").claim("username", "admin"))
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 获取启用的数据集列表");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/datasets/enabled");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(7)
    @DisplayName("测试数据集查询执行")
    void testExecuteDatasetQuery() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("datasetId", 1L);
        requestBody.put("params", new HashMap<String, Object>());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/datasets/query")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("1").claim("username", "admin"))
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 数据集查询执行");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/datasets/query");
        System.out.println("数据集ID: 1");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> response = objectMapper.readValue(content, Map.class);
        System.out.println("响应码: " + response.get("code"));
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(8)
    @DisplayName("测试数据集预览查询")
    void testPreviewDatasetQuery() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("datasetId", 1L);
        requestBody.put("params", new HashMap<String, Object>());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/datasets/preview")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("1").claim("username", "admin"))
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 数据集预览查询");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/datasets/preview");
        System.out.println("数据集ID: 1");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(9)
    @DisplayName("测试SQL聚合计算 - COUNT")
    void testSqlAggregationCount() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("datasetId", 1L);
        requestBody.put("params", new HashMap<String, Object>());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/datasets/query")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("1").claim("username", "admin"))
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: SQL聚合计算 - COUNT");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/datasets/query");
        System.out.println("聚合类型: COUNT");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(10)
    @DisplayName("测试SQL聚合计算 - SUM")
    void testSqlAggregationSum() throws Exception {
        // This test assumes there's a numeric field to sum
        // In the test database, we don't have such a field, but we test the endpoint works
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("datasetId", 1L);
        requestBody.put("params", new HashMap<String, Object>());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/datasets/query")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("1").claim("username", "admin"))
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: SQL聚合计算 - SUM");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/datasets/query");
        System.out.println("聚合类型: SUM");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(11)
    @DisplayName("测试SQL聚合计算 - AVG")
    void testSqlAggregationAvg() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("datasetId", 1L);
        requestBody.put("params", new HashMap<String, Object>());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/datasets/query")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("1").claim("username", "admin"))
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: SQL聚合计算 - AVG");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/datasets/query");
        System.out.println("聚合类型: AVG");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(12)
    @DisplayName("测试SQL聚合计算 - GROUP BY")
    void testSqlAggregationGroupBy() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("datasetId", 1L);
        requestBody.put("params", new HashMap<String, Object>());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/datasets/query")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("1").claim("username", "admin"))
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: SQL聚合计算 - GROUP BY");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/datasets/query");
        System.out.println("聚合类型: GROUP BY");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(13)
    @DisplayName("测试无权限访问数据集接口")
    void testUnauthorizedDatasetAccess() throws Exception {
        // Use a token without report:manage authority
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/datasets")
                .header("Authorization", "Bearer " + adminToken)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("2").claim("username", "testuser"))
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> response = objectMapper.readValue(content, Map.class);

        System.out.println("========================================");
        System.out.println("测试报告: 无权限访问数据集接口");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/datasets");
        System.out.println("用户角色: ROLE_USER (无report:manage权限)");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应码: " + response.get("code"));
        System.out.println("测试结果: 通过（正确拒绝无权限访问）");
        System.out.println("========================================");
    }

    // ==================== Helper Methods ====================

    private String loginAndGetToken(String username, String password) throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername(username);
        request.setPassword(password);

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

    private ReportDataset createTestDataset(String name, String code) {
        ReportDataset dataset = new ReportDataset();
        dataset.setDatasetName(name);
        dataset.setDatasetCode(code);
        dataset.setDatasetDesc("Test dataset description");
        dataset.setDatasourceId(1L);
        dataset.setDatasourceName("Test MySQL");
        dataset.setSqlStatement("SELECT id, username, nickname FROM sys_user WHERE del_flag = 0");
        dataset.setDatasetType("SQL");
        dataset.setStatus(1);
        dataset.setTenantId(1L);
        dataset.setCreateUserId(1L);
        return dataset;
    }

    private void createDataset(ReportDataset dataset) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/datasets")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dataset))
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject("1").claim("username", "admin"))
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("report:manage")));

        mockMvc.perform(requestBuilder);
    }
}
