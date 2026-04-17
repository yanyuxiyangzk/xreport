package com.xreport.service.report;

import com.github.pagehelper.PageInfo;
import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.report.ReportDatasourceMapper;
import com.xreport.pojo.dto.DatasourceDTO;
import com.xreport.pojo.entity.reportdatasource.ReportDatasource;
import com.xreport.service.report.impl.ReportDatasourceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * ReportDatasourceService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ReportDatasourceServiceTest {

    @Mock
    private ReportDatasourceMapper datasourceMapper;

    @Mock
    private RestTemplate restTemplate;

    private ReportDatasourceServiceImpl datasourceService;

    private ReportDatasource testDatasource;

    @BeforeEach
    void setUp() throws Exception {
        datasourceService = new ReportDatasourceServiceImpl(datasourceMapper);
        // Inject mock RestTemplate using reflection (since it's @Autowired)
        var field = ReportDatasourceServiceImpl.class.getDeclaredField("restTemplate");
        field.setAccessible(true);
        field.set(datasourceService, restTemplate);

        testDatasource = new ReportDatasource();
        testDatasource.setId(1L);
        testDatasource.setDatasourceName("测试数据源");
        testDatasource.setDatasourceType("jdbc");
        testDatasource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        testDatasource.setUsername("root");
        testDatasource.setPassword("password");
        testDatasource.setApiUrl("http://api.example.com/data");
        testDatasource.setApiMethod("GET");
        testDatasource.setStatus(1);
        testDatasource.setDelFlag(0);
        testDatasource.setTenantId(1L);
        testDatasource.setCreateTime(LocalDateTime.now());
        testDatasource.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("分页查询数据源列表")
    void pageQuery_ShouldReturnPagedDatasources() {
        // Arrange
        DatasourceDTO dto = new DatasourceDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);

        List<ReportDatasource> datasources = Arrays.asList(testDatasource);
        when(datasourceMapper.selectList(any())).thenReturn(datasources);

        // Act
        PageInfo<ReportDatasource> result = datasourceService.pageQuery(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals("测试数据源", result.getList().get(0).getDatasourceName());
        verify(datasourceMapper).selectList(any());
    }

    @Test
    @DisplayName("分页查询数据源列表 - 按名称筛选")
    void pageQuery_WithNameFilter_ShouldReturnFilteredDatasources() {
        // Arrange
        DatasourceDTO dto = new DatasourceDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setDatasourceName("测试");

        List<ReportDatasource> datasources = Arrays.asList(testDatasource);
        when(datasourceMapper.selectList(any())).thenReturn(datasources);

        // Act
        PageInfo<ReportDatasource> result = datasourceService.pageQuery(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSize());
        verify(datasourceMapper).selectList(any());
    }

    @Test
    @DisplayName("获取数据源详情 - 成功")
    void getById_WhenDatasourceExists_ShouldReturnDatasource() {
        // Arrange
        when(datasourceMapper.selectById(1L)).thenReturn(testDatasource);

        // Act
        ReportDatasource result = datasourceService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试数据源", result.getDatasourceName());
        verify(datasourceMapper).selectById(1L);
    }

    @Test
    @DisplayName("获取数据源详情 - 数据源不存在")
    void getById_WhenDatasourceNotExists_ShouldThrowException() {
        // Arrange
        when(datasourceMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            datasourceService.getById(999L);
        });

        assertEquals("数据源不存在", exception.getMessage());
    }

    @Test
    @DisplayName("获取数据源详情 - 数据源已删除")
    void getById_WhenDatasourceDeleted_ShouldThrowException() {
        // Arrange
        testDatasource.setDelFlag(1);
        when(datasourceMapper.selectById(1L)).thenReturn(testDatasource);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            datasourceService.getById(1L);
        });

        assertEquals("数据源不存在", exception.getMessage());
    }

    @Test
    @DisplayName("新增数据源")
    void add_ShouldInsertDatasource() {
        // Arrange
        ReportDatasource newDatasource = new ReportDatasource();
        newDatasource.setDatasourceName("新数据源");
        newDatasource.setDatasourceType("jdbc");

        try (MockedStatic<com.xreport.common.util.IdWorker> idWorkerMock =
                     mockStatic(com.xreport.common.util.IdWorker.class)) {
            idWorkerMock.when(com.xreport.common.util.IdWorker::nextId).thenReturn(100L);

            // Act
            datasourceService.add(newDatasource);

            // Assert
            assertEquals(100L, newDatasource.getId());
            assertEquals(0, newDatasource.getDelFlag());
            assertEquals(1, newDatasource.getStatus());
            assertEquals(1L, newDatasource.getTenantId());
            verify(datasourceMapper).insert(newDatasource);
        }
    }

    @Test
    @DisplayName("更新数据源")
    void update_ShouldUpdateDatasourceFields() {
        // Arrange
        ReportDatasource updateDatasource = new ReportDatasource();
        updateDatasource.setId(1L);
        updateDatasource.setDatasourceName("更新后的数据源");
        updateDatasource.setDatasourceType("api");

        when(datasourceMapper.selectById(1L)).thenReturn(testDatasource);

        // Act
        datasourceService.update(updateDatasource);

        // Assert
        assertEquals("更新后的数据源", testDatasource.getDatasourceName());
        assertEquals("api", testDatasource.getDatasourceType());
        verify(datasourceMapper).updateById(testDatasource);
    }

    @Test
    @DisplayName("删除数据源 - 软删除")
    void delete_ShouldSetDelFlagToOne() {
        // Arrange
        when(datasourceMapper.selectById(1L)).thenReturn(testDatasource);

        // Act
        datasourceService.delete(1L);

        // Assert
        assertEquals(1, testDatasource.getDelFlag());
        verify(datasourceMapper).updateById(testDatasource);
    }

    @Test
    @DisplayName("获取启用的数据源列表")
    void listEnabled_ShouldReturnActiveDatasources() {
        // Arrange
        List<ReportDatasource> datasources = Arrays.asList(testDatasource);
        when(datasourceMapper.selectList(any())).thenReturn(datasources);

        // Act
        List<ReportDatasource> result = datasourceService.listEnabled();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(datasourceMapper).selectList(any());
    }

    @Test
    @DisplayName("测试JDBC连接 - 连接成功")
    void testJdbcConnection_Success() throws Exception {
        // Arrange
        String jdbcUrl = "jdbc:mysql://localhost:3306/test";
        String username = "root";
        String password = "password";

        Connection mockConnection = mock(Connection.class);

        try (MockedStatic<com.xreport.common.util.JdbcUtils> jdbcUtilsMock =
                     mockStatic(com.xreport.common.util.JdbcUtils.class)) {

            jdbcUtilsMock.when(() -> com.xreport.common.util.JdbcUtils.getConnection(jdbcUrl, username, password))
                    .thenReturn(mockConnection);

            // Act
            boolean result = datasourceService.testJdbcConnection(jdbcUrl, username, password);

            // Assert
            assertTrue(result);
            jdbcUtilsMock.verify(() -> com.xreport.common.util.JdbcUtils.getConnection(jdbcUrl, username, password));
        }
    }

    @Test
    @DisplayName("测试JDBC连接 - 连接失败")
    void testJdbcConnection_Failure() throws Exception {
        // Arrange
        String jdbcUrl = "jdbc:mysql://localhost:3306/test";
        String username = "root";
        String password = "wrongpassword";

        try (MockedStatic<com.xreport.common.util.JdbcUtils> jdbcUtilsMock =
                     mockStatic(com.xreport.common.util.JdbcUtils.class)) {

            jdbcUtilsMock.when(() -> com.xreport.common.util.JdbcUtils.getConnection(jdbcUrl, username, password))
                    .thenThrow(new RuntimeException("Connection failed"));

            // Act
            boolean result = datasourceService.testJdbcConnection(jdbcUrl, username, password);

            // Assert
            assertFalse(result);
        }
    }

    @Test
    @DisplayName("测试API连接 - GET请求成功")
    void testApiConnection_GetSuccess() {
        // Arrange
        String apiUrl = "http://api.example.com/data";
        String method = "GET";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token");

        ResponseEntity<String> mockResponse = ResponseEntity.ok("{\"status\":\"success\"}");

        when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(mockResponse);

        // Act
        Map<String, Object> result = datasourceService.testApiConnection(apiUrl, method, headers, null);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals(200, result.get("statusCode"));
        assertEquals("{\"status\":\"success\"}", result.get("body"));
        assertEquals("API连接成功", result.get("message"));
    }

    @Test
    @DisplayName("测试API连接 - POST请求成功")
    void testApiConnection_PostSuccess() {
        // Arrange
        String apiUrl = "http://api.example.com/data";
        String method = "POST";
        String body = "{\"name\":\"test\"}";

        ResponseEntity<String> mockResponse = ResponseEntity.ok("{\"id\":1}");

        when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(mockResponse);

        // Act
        Map<String, Object> result = datasourceService.testApiConnection(apiUrl, method, null, body);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals(200, result.get("statusCode"));
    }

    @Test
    @DisplayName("测试API连接 - 不支持的HTTP方法")
    void testApiConnection_UnsupportedMethod() {
        // Arrange
        String apiUrl = "http://api.example.com/data";
        String method = "PATCH";
        Map<String, String> headers = new HashMap<>();

        // Act
        Map<String, Object> result = datasourceService.testApiConnection(apiUrl, method, headers, null);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertEquals("不支持的HTTP方法: PATCH", result.get("message"));
    }

    @Test
    @DisplayName("测试API连接 - API请求失败")
    void testApiConnection_ApiFailure() {
        // Arrange
        String apiUrl = "http://api.example.com/data";
        String method = "GET";

        when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection timeout"));

        // Act
        Map<String, Object> result = datasourceService.testApiConnection(apiUrl, method, null, null);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("message")).contains("API连接失败"));
    }

    @Test
    @DisplayName("执行JDBC查询 - 成功")
    void executeJdbcQuery_Success() throws Exception {
        // Arrange
        String sql = "SELECT * FROM users";

        when(datasourceMapper.selectById(1L)).thenReturn(testDatasource);

        List<Map<String, Object>> expectedResults = Arrays.asList(
                Map.of("id", 1, "name", "user1"),
                Map.of("id", 2, "name", "user2")
        );

        try (MockedStatic<com.xreport.common.util.JdbcUtils> jdbcUtilsMock =
                     mockStatic(com.xreport.common.util.JdbcUtils.class)) {

            Connection mockConnection = mock(Connection.class);
            jdbcUtilsMock.when(() -> com.xreport.common.util.JdbcUtils.getConnection(
                    anyString(), anyString(), anyString())).thenReturn(mockConnection);
            jdbcUtilsMock.when(() -> com.xreport.common.util.JdbcUtils.executeQuery(
                    any(Connection.class), anyString(), any())).thenReturn(expectedResults);

            // Act
            List<Map<String, Object>> result = datasourceService.executeJdbcQuery(1L, sql);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
        }
    }

    @Test
    @DisplayName("执行JDBC查询 - 非JDBC类型数据源")
    void executeJdbcQuery_NotJdbcDatasource_ShouldThrowException() {
        // Arrange
        testDatasource.setDatasourceType("api");
        when(datasourceMapper.selectById(1L)).thenReturn(testDatasource);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            datasourceService.executeJdbcQuery(1L, "SELECT * FROM users");
        });

        assertEquals("该数据源不是JDBC类型", exception.getMessage());
    }

    @Test
    @DisplayName("执行API查询 - 成功")
    void executeApiQuery_Success() {
        // Arrange
        testDatasource.setDatasourceType("api");
        when(datasourceMapper.selectById(1L)).thenReturn(testDatasource);

        Map<String, Object> expectedResult = Map.of("success", true, "data", "result");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\"data\":\"result\"}"));

        // Act - testApiConnection is called internally by executeApiQuery
        Map<String, Object> result = datasourceService.executeApiQuery(
                1L, "http://api.example.com", "GET", null, null);

        // Assert
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
    }

    @Test
    @DisplayName("执行API查询 - 非API类型数据源")
    void executeApiQuery_NotApiDatasource_ShouldThrowException() {
        // Arrange
        // testDatasource is jdbc by default
        when(datasourceMapper.selectById(1L)).thenReturn(testDatasource);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            datasourceService.executeApiQuery(1L, "http://api.example.com", "GET", null, null);
        });

        assertEquals("该数据源不是API类型", exception.getMessage());
    }
}