package com.xreport.service.dataset.impl;

import com.xreport.common.exception.BusinessException;
import com.xreport.common.util.JdbcUtils;
import com.xreport.pojo.entity.reportdatasource.ReportDatasource;
import com.xreport.service.dataset.IDatasetQueryEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 数据集查询引擎实现
 * 支持JDBC和API两种数据源类型
 * 支持SQL聚合计算
 * 内置SQL注入防护
 */
@Component
public class DatasetQueryEngine implements IDatasetQueryEngine {

    private static final Logger log = LoggerFactory.getLogger(DatasetQueryEngine.class);

    /**
     * 禁止执行的SQL命令（SQL注入防护）
     */
    private static final Pattern[] DANGEROUS_PATTERNS = {
        Pattern.compile("\\bDELETE\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bDROP\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bTRUNCATE\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bALTER\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bINSERT\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bUPDATE\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bCREATE\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bGRANT\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bREVOKE\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile(";\\s*\\w+", Pattern.CASE_INSENSITIVE)  // 禁止分号后的额外命令
    };

    /**
     * 支持的聚合函数
     */
    private static final Pattern AGG_PATTERN = Pattern.compile(
        "\\b(COUNT|SUM|AVG|MAX|MIN)\\s*\\(\\s*\\w+\\s*\\)",
        Pattern.CASE_INSENSITIVE
    );

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Map<String, Object>> execute(ReportDatasource datasource, String sql, Map<String, Object> params) {
        if (datasource == null) {
            throw new BusinessException("数据源不能为空");
        }
        if (sql == null || sql.trim().isEmpty()) {
            throw new BusinessException("SQL语句不能为空");
        }

        String datasourceType = datasource.getDatasourceType();
        if ("jdbc".equalsIgnoreCase(datasourceType)) {
            return executeJdbcQuery(datasource, sql, params);
        } else if ("api".equalsIgnoreCase(datasourceType)) {
            return executeApiQuery(datasource, sql, params);
        } else {
            throw new BusinessException("不支持的数据源类型: " + datasourceType);
        }
    }

    /**
     * 执行JDBC查询
     */
    private List<Map<String, Object>> executeJdbcQuery(ReportDatasource datasource, String sql, Map<String, Object> params) {
        // SQL安全检查
        if (!isSqlSafe(sql)) {
            throw new BusinessException("SQL包含危险操作，被禁止执行");
        }

        Connection conn = null;
        try {
            conn = JdbcUtils.getConnection(datasource.getJdbcUrl(), datasource.getUsername(), datasource.getPassword());
            // 增强SQL（支持聚合计算）
            String enhancedSql = buildAggQuery(sql, params);
            return JdbcUtils.executeQuery(conn, enhancedSql, null);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("执行JDBC查询失败: datasourceId={}, sql={}", datasource.getId(), sql, e);
            throw new BusinessException("执行查询失败: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    JdbcUtils.close(conn, null, null);
                } catch (Exception e) {
                    log.error("关闭JDBC连接失败", e);
                }
            }
        }
    }

    /**
     * 执行API查询
     */
    private List<Map<String, Object>> executeApiQuery(ReportDatasource datasource, String sql, Map<String, Object> params) {
        try {
            String apiUrl = datasource.getApiUrl();
            String method = datasource.getApiMethod() != null ? datasource.getApiMethod() : "GET";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 处理headers
            if (datasource.getApiHeaders() != null && !datasource.getApiHeaders().isEmpty()) {
                // headers为JSON格式
                headers.setContentType(MediaType.APPLICATION_JSON);
            }

            // 处理body
            String body = datasource.getApiBody();
            if (params != null && params.containsKey("body")) {
                body = String.valueOf(params.get("body"));
            }

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response;

            HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
            response = restTemplate.exchange(apiUrl, httpMethod, entity, String.class);

            List<Map<String, Object>> result = new ArrayList<>();
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // API返回的是JSON数组字符串，需要解析
                // 这里简化处理，实际项目中应该使用JSON库解析
                Map<String, Object> item = new java.util.HashMap<>();
                item.put("data", response.getBody());
                item.put("statusCode", response.getStatusCode().value());
                result.add(item);
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("执行API查询失败: datasourceId={}", datasource.getId(), e);
            throw new BusinessException("执行API查询失败: " + e.getMessage());
        }
    }

    @Override
    public boolean isSqlSafe(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }

        // 检查危险模式
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(sql).find()) {
                log.warn("SQL注入检测到危险关键字: {}", sql);
                return false;
            }
        }

        // 检查是否有多个SQL语句（分号分隔）
        String[] statements = sql.split(";");
        if (statements.length > 1) {
            for (String stmt : statements) {
                String trimmed = stmt.trim();
                if (!trimmed.isEmpty() && !isSelectStatement(trimmed)) {
                    log.warn("SQL注入检测到非SELECT语句: {}", stmt);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 检查是否为SELECT语句
     */
    private boolean isSelectStatement(String sql) {
        String trimmed = sql.toUpperCase().trim();
        return trimmed.startsWith("SELECT") || trimmed.startsWith("WITH");
    }

    @Override
    public String buildAggQuery(String sql, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return sql;
        }

        StringBuilder builder = new StringBuilder(sql);
        List<String> clauses = new ArrayList<>();

        // 处理GROUP BY
        if (params.containsKey("groupFields")) {
            String groupFields = String.valueOf(params.get("groupFields"));
            if (groupFields != null && !groupFields.isEmpty() && !"null".equals(groupFields)) {
                clauses.add("GROUP BY " + groupFields);
            }
        }

        // 处理ORDER BY
        if (params.containsKey("orderFields")) {
            String orderFields = String.valueOf(params.get("orderFields"));
            if (orderFields != null && !orderFields.isEmpty() && !"null".equals(orderFields)) {
                clauses.add("ORDER BY " + orderFields);
            }
        }

        // 处理LIMIT
        Integer limit = null;
        if (params.containsKey("limit")) {
            Object limitObj = params.get("limit");
            if (limitObj instanceof Integer) {
                limit = (Integer) limitObj;
            } else if (limitObj != null) {
                limit = Integer.parseInt(String.valueOf(limitObj));
            }
        }

        // 默认limit为1000
        if (limit == null) {
            limit = 1000;
        }

        // 检查原始SQL是否已有ORDER BY或LIMIT
        String upperSql = sql.toUpperCase();
        boolean hasOrderBy = upperSql.contains("ORDER BY");
        boolean hasLimit = upperSql.contains("LIMIT");

        // 追加GROUP BY
        if (!clauses.isEmpty() && !hasOrderBy && !hasLimit) {
            builder.append(" ");
            for (int i = 0; i < clauses.size(); i++) {
                builder.append(clauses.get(i));
                if (i < clauses.size() - 1) {
                    builder.append(" ");
                }
            }
        }

        // 追加LIMIT（如果没有的话）
        if (!hasLimit) {
            builder.append(" LIMIT ").append(limit);
        }

        return builder.toString();
    }
}