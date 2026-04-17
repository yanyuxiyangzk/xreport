package com.xreport.service.dataset;

import com.xreport.common.exception.BusinessException;
import com.xreport.pojo.entity.reportdatasource.ReportDatasource;
import com.xreport.service.dataset.impl.DatasetQueryEngine;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * DatasetQueryEngine 单元测试
 */
@ExtendWith(MockitoExtension.class)
class DatasetQueryEngineTest {

    @Mock
    private RestTemplate restTemplate;

    private DatasetQueryEngine queryEngine;

    private ReportDatasource jdbcDatasource;

    private ReportDatasource apiDatasource;

    @BeforeEach
    void setUp() throws Exception {
        queryEngine = new DatasetQueryEngine();
        // Inject mock RestTemplate using reflection (since it's @Autowired)
        var field = DatasetQueryEngine.class.getDeclaredField("restTemplate");
        field.setAccessible(true);
        field.set(queryEngine, restTemplate);

        jdbcDatasource = new ReportDatasource();
        jdbcDatasource.setId(1L);
        jdbcDatasource.setDatasourceName("JDBC数据源");
        jdbcDatasource.setDatasourceType("jdbc");
        jdbcDatasource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        jdbcDatasource.setUsername("root");
        jdbcDatasource.setPassword("password");

        apiDatasource = new ReportDatasource();
        apiDatasource.setId(2L);
        apiDatasource.setDatasourceName("API数据源");
        apiDatasource.setDatasourceType("api");
        apiDatasource.setApiUrl("http://api.example.com/data");
        apiDatasource.setApiMethod("GET");
    }

    // ========== execute() Tests ==========

    @Test
    @DisplayName("执行查询 - 数据源为null应抛出异常")
    void execute_WithNullDatasource_ShouldThrowException() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            queryEngine.execute(null, "SELECT * FROM users", null);
        });

        assertEquals("数据源不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("执行查询 - SQL为空应抛出异常")
    void execute_WithEmptySql_ShouldThrowException() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            queryEngine.execute(jdbcDatasource, "", null);
        });

        assertEquals("SQL语句不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("执行查询 - 不支持的数据源类型应抛出异常")
    void execute_WithUnsupportedDatasourceType_ShouldThrowException() {
        // Arrange
        jdbcDatasource.setDatasourceType("unknown");

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            queryEngine.execute(jdbcDatasource, "SELECT * FROM users", null);
        });

        assertEquals("不支持的数据源类型: unknown", exception.getMessage());
    }

    @Test
    @DisplayName("执行JDBC查询 - 成功")
    void execute_JdbcQuery_Success() throws Exception {
        // Arrange
        String sql = "SELECT * FROM users";
        Map<String, Object> params = new HashMap<>();

        List<Map<String, Object>> expectedResults = Arrays.asList(
                Map.of("id", 1, "name", "user1"),
                Map.of("id", 2, "name", "user2")
        );

        Connection mockConnection = mock(Connection.class);

        try (MockedStatic<com.xreport.common.util.JdbcUtils> jdbcUtilsMock =
                     mockStatic(com.xreport.common.util.JdbcUtils.class)) {

            jdbcUtilsMock.when(() -> com.xreport.common.util.JdbcUtils.getConnection(
                    anyString(), anyString(), anyString())).thenReturn(mockConnection);
            jdbcUtilsMock.when(() -> com.xreport.common.util.JdbcUtils.executeQuery(
                    any(Connection.class), anyString(), any())).thenReturn(expectedResults);

            // Act
            List<Map<String, Object>> result = queryEngine.execute(jdbcDatasource, sql, params);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            jdbcUtilsMock.verify(() -> com.xreport.common.util.JdbcUtils.getConnection(
                    eq("jdbc:mysql://localhost:3306/test"), eq("root"), eq("password")));
        }
    }

    @Test
    @DisplayName("执行API查询 - GET请求成功")
    void execute_ApiQuery_GetSuccess() {
        // Arrange
        ResponseEntity<String> mockResponse = ResponseEntity.ok("{\"status\":\"success\"}");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), eq(String.class)))
                .thenReturn(mockResponse);

        // Act - 必须传非空sql参数，因为execute()方法会验证sql不能为空
        List<Map<String, Object>> result = queryEngine.execute(apiDatasource, "dummy_sql", null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("{\"status\":\"success\"}", result.get(0).get("data"));
        assertEquals(200, result.get(0).get("statusCode"));
    }

    @Test
    @DisplayName("执行API查询 - POST请求成功")
    void execute_ApiQuery_PostSuccess() {
        // Arrange
        apiDatasource.setApiMethod("POST");
        apiDatasource.setApiBody("{\"name\":\"test\"}");

        ResponseEntity<String> mockResponse = ResponseEntity.ok("{\"id\":1}");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), eq(String.class)))
                .thenReturn(mockResponse);

        // Act - 必须传非空sql参数
        List<Map<String, Object>> result = queryEngine.execute(apiDatasource, "dummy_sql", null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("执行API查询 - 请求失败")
    void execute_ApiQuery_Failure() {
        // Arrange
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection timeout"));

        // Act & Assert - 必须传非空sql参数
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            queryEngine.execute(apiDatasource, "dummy_sql", null);
        });

        assertTrue(exception.getMessage().contains("执行API查询失败"));
    }

    // ========== isSqlSafe() Tests ==========

    @Test
    @DisplayName("SQL安全检查 - 合法SELECT语句应通过")
    void isSqlSafe_LegalSelectStatement_ShouldReturnTrue() {
        // Arrange
        String sql = "SELECT id, name FROM users WHERE status = 1";

        // Act
        boolean result = queryEngine.isSqlSafe(sql);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("SQL安全检查 - 包含DELETE关键字应拒绝")
    void isSqlSafe_WithDeleteKeyword_ShouldReturnFalse() {
        // Arrange
        String sql = "SELECT * FROM users; DELETE FROM users WHERE 1=1";

        // Act
        boolean result = queryEngine.isSqlSafe(sql);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("SQL安全检查 - 包含DROP关键字应拒绝")
    void isSqlSafe_WithDropKeyword_ShouldReturnFalse() {
        // Arrange
        String sql = "SELECT * FROM users; DROP TABLE users";

        // Act
        boolean result = queryEngine.isSqlSafe(sql);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("SQL安全检查 - 包含TRUNCATE关键字应拒绝")
    void isSqlSafe_WithTruncateKeyword_ShouldReturnFalse() {
        // Arrange
        String sql = "SELECT * FROM users; TRUNCATE TABLE users";

        // Act
        boolean result = queryEngine.isSqlSafe(sql);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("SQL安全检查 - 包含INSERT关键字应拒绝")
    void isSqlSafe_WithInsertKeyword_ShouldReturnFalse() {
        // Arrange
        String sql = "SELECT * FROM users; INSERT INTO users VALUES (1, 'test')";

        // Act
        boolean result = queryEngine.isSqlSafe(sql);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("SQL安全检查 - 包含UPDATE关键字应拒绝")
    void isSqlSafe_WithUpdateKeyword_ShouldReturnFalse() {
        // Arrange
        String sql = "SELECT * FROM users; UPDATE users SET name = 'hacked'";

        // Act
        boolean result = queryEngine.isSqlSafe(sql);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("SQL安全检查 - 包含ALTER关键字应拒绝")
    void isSqlSafe_WithAlterKeyword_ShouldReturnFalse() {
        // Arrange
        String sql = "SELECT * FROM users; ALTER TABLE users ADD COLUMN hacked VARCHAR(100)";

        // Act
        boolean result = queryEngine.isSqlSafe(sql);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("SQL安全检查 - 包含CREATE关键字应拒绝")
    void isSqlSafe_WithCreateKeyword_ShouldReturnFalse() {
        // Arrange
        String sql = "SELECT * FROM users; CREATE TABLE hacked (id INT)";

        // Act
        boolean result = queryEngine.isSqlSafe(sql);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("SQL安全检查 - 包含GRANT关键字应拒绝")
    void isSqlSafe_WithGrantKeyword_ShouldReturnFalse() {
        // Arrange
        String sql = "SELECT * FROM users; GRANT ALL ON users TO hacker";

        // Act
        boolean result = queryEngine.isSqlSafe(sql);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("SQL安全检查 - 包含REVOKE关键字应拒绝")
    void isSqlSafe_WithRevokeKeyword_ShouldReturnFalse() {
        // Arrange
        String sql = "SELECT * FROM users; REVOKE ALL ON users FROM hacker";

        // Act
        boolean result = queryEngine.isSqlSafe(sql);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("SQL安全检查 - 多语句中非SELECT应拒绝")
    void isSqlSafe_MultipleStatementsWithNonSelect_ShouldReturnFalse() {
        // Arrange
        String sql = "SELECT * FROM users; SELECT * FROM orders";

        // Act
        boolean result = queryEngine.isSqlSafe(sql);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("SQL安全检查 - 空SQL应返回false")
    void isSqlSafe_WithEmptySql_ShouldReturnFalse() {
        // Act
        boolean result = queryEngine.isSqlSafe("");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("SQL安全检查 - null应返回false")
    void isSqlSafe_WithNullSql_ShouldReturnFalse() {
        // Act
        boolean result = queryEngine.isSqlSafe(null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("SQL安全检查 - WITH子句应通过")
    void isSqlSafe_WithClause_ShouldReturnTrue() {
        // Arrange
        String sql = "WITH temp AS (SELECT * FROM users) SELECT * FROM temp";

        // Act
        boolean result = queryEngine.isSqlSafe(sql);

        // Assert
        assertTrue(result);
    }

    // ========== buildAggQuery() Tests ==========

    @Test
    @DisplayName("构建聚合查询 - 无参数应返回原SQL")
    void buildAggQuery_WithNoParams_ShouldReturnOriginalSql() {
        // Arrange
        String sql = "SELECT * FROM users";

        // Act
        String result = queryEngine.buildAggQuery(sql, null);

        // Assert
        assertEquals(sql, result);
    }

    @Test
    @DisplayName("构建聚合查询 - 空参数应返回原SQL")
    void buildAggQuery_WithEmptyParams_ShouldReturnOriginalSql() {
        // Arrange
        String sql = "SELECT * FROM users";

        // Act
        String result = queryEngine.buildAggQuery(sql, new HashMap<>());

        // Assert
        assertEquals(sql, result);
    }

    @Test
    @DisplayName("构建聚合查询 - 添加GROUP BY")
    void buildAggQuery_WithGroupFields_ShouldAppendGroupBy() {
        // Arrange
        String sql = "SELECT status, COUNT(*) FROM orders";
        Map<String, Object> params = new HashMap<>();
        params.put("groupFields", "status");

        // Act
        String result = queryEngine.buildAggQuery(sql, params);

        // Assert
        assertTrue(result.contains("GROUP BY status"));
    }

    @Test
    @DisplayName("构建聚合查询 - 添加ORDER BY")
    void buildAggQuery_WithOrderFields_ShouldAppendOrderBy() {
        // Arrange
        String sql = "SELECT status, COUNT(*) FROM orders";
        Map<String, Object> params = new HashMap<>();
        params.put("orderFields", "status DESC");

        // Act
        String result = queryEngine.buildAggQuery(sql, params);

        // Assert
        assertTrue(result.contains("ORDER BY status DESC"));
    }

    @Test
    @DisplayName("构建聚合查询 - 添加LIMIT")
    void buildAggQuery_WithLimit_ShouldAppendLimit() {
        // Arrange
        String sql = "SELECT * FROM users";
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 50);

        // Act
        String result = queryEngine.buildAggQuery(sql, params);

        // Assert
        assertTrue(result.contains("LIMIT 50"));
    }

    @Test
    @DisplayName("构建聚合查询 - 默认LIMIT为1000")
    void buildAggQuery_WithoutExplicitLimit_ShouldUseDefault1000() {
        // Arrange
        String sql = "SELECT * FROM users";
        // 必须传非空params（不能是空HashMap），且不包含limit/groupFields/orderFields
        // 才能触发默认LIMIT=1000的逻辑
        Map<String, Object> params = new HashMap<>();
        params.put("dummyParam", "value");  // 非空但不影响limit的参数

        // Act
        String result = queryEngine.buildAggQuery(sql, params);

        // Assert
        assertTrue(result.contains("LIMIT 1000"));
    }

    @Test
    @DisplayName("构建聚合查询 - 已有LIMIT不应追加")
    void buildAggQuery_WithExistingLimit_ShouldNotAppendAnother() {
        // Arrange
        String sql = "SELECT * FROM users LIMIT 50";
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 100);

        // Act
        String result = queryEngine.buildAggQuery(sql, params);

        // Assert
        assertTrue(result.contains("LIMIT 50"));
        // Should not have LIMIT 100
        assertEquals(1, result.split("LIMIT").length - 1);
    }

    @Test
    @DisplayName("构建聚合查询 - 已有ORDER BY不应追加")
    void buildAggQuery_WithExistingOrderBy_ShouldNotAppendAnother() {
        // Arrange
        String sql = "SELECT * FROM users ORDER BY id DESC";
        Map<String, Object> params = new HashMap<>();
        params.put("orderFields", "name ASC");

        // Act
        String result = queryEngine.buildAggQuery(sql, params);

        // Assert
        assertTrue(result.contains("ORDER BY id DESC"));
        assertFalse(result.contains("ORDER BY name ASC"));
    }

    @Test
    @DisplayName("构建聚合查询 - null值groupFields应忽略")
    void buildAggQuery_WithNullGroupFields_ShouldIgnore() {
        // Arrange
        String sql = "SELECT * FROM users";
        Map<String, Object> params = new HashMap<>();
        params.put("groupFields", null);

        // Act
        String result = queryEngine.buildAggQuery(sql, params);

        // Assert
        assertFalse(result.contains("GROUP BY"));
    }

    @Test
    @DisplayName("构建聚合查询 - 字符串格式limit应转换")
    void buildAggQuery_WithStringLimit_ShouldConvert() {
        // Arrange
        String sql = "SELECT * FROM users";
        Map<String, Object> params = new HashMap<>();
        params.put("limit", "200");

        // Act
        String result = queryEngine.buildAggQuery(sql, params);

        // Assert
        assertTrue(result.contains("LIMIT 200"));
    }
}