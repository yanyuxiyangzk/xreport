package com.xreport.service.report.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xreport.common.exception.BusinessException;
import com.xreport.common.util.IdWorker;
import com.xreport.common.util.JdbcUtils;
import com.xreport.mapper.report.ReportDatasourceMapper;
import com.xreport.pojo.dto.DatasourceDTO;
import com.xreport.pojo.entity.reportdatasource.ReportDatasource;
import com.xreport.service.report.IReportDatasourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportDatasourceServiceImpl implements IReportDatasourceService {

    private static final Logger log = LoggerFactory.getLogger(ReportDatasourceServiceImpl.class);

    private final ReportDatasourceMapper datasourceMapper;

    @Autowired
    private RestTemplate restTemplate;

    public ReportDatasourceServiceImpl(ReportDatasourceMapper datasourceMapper) {
        this.datasourceMapper = datasourceMapper;
    }

    @Override
    public PageInfo<ReportDatasource> pageQuery(DatasourceDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<ReportDatasource> wrapper = new LambdaQueryWrapper<>();
        if (dto.getDatasourceName() != null && !dto.getDatasourceName().isEmpty()) {
            wrapper.like(ReportDatasource::getDatasourceName, dto.getDatasourceName());
        }
        if (dto.getDatasourceType() != null && !dto.getDatasourceType().isEmpty()) {
            wrapper.eq(ReportDatasource::getDatasourceType, dto.getDatasourceType());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(ReportDatasource::getStatus, dto.getStatus());
        }
        wrapper.eq(ReportDatasource::getDelFlag, 0).orderByDesc(ReportDatasource::getCreateTime);
        List<ReportDatasource> list = datasourceMapper.selectList(wrapper);
        return new PageInfo<>(list);
    }

    @Override
    public List<ReportDatasource> listEnabled() {
        LambdaQueryWrapper<ReportDatasource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportDatasource::getDelFlag, 0)
               .eq(ReportDatasource::getStatus, 1)
               .orderByDesc(ReportDatasource::getCreateTime);
        return datasourceMapper.selectList(wrapper);
    }

    @Override
    public ReportDatasource getById(Long id) {
        ReportDatasource datasource = datasourceMapper.selectById(id);
        if (datasource == null || datasource.getDelFlag() == 1) {
            throw new BusinessException("数据源不存在");
        }
        return datasource;
    }

    @Override
    @Transactional
    public void add(ReportDatasource datasource) {
        datasource.setId(IdWorker.nextId());
        datasource.setDelFlag(0);
        datasource.setStatus(1);
        datasource.setTenantId(1L);
        datasource.setCreateTime(java.time.LocalDateTime.now());
        datasource.setUpdateTime(java.time.LocalDateTime.now());
        datasourceMapper.insert(datasource);
    }

    @Override
    @Transactional
    public void update(ReportDatasource datasource) {
        ReportDatasource existing = getById(datasource.getId());
        existing.setDatasourceName(datasource.getDatasourceName());
        existing.setDatasourceType(datasource.getDatasourceType());
        existing.setJdbcUrl(datasource.getJdbcUrl());
        existing.setUsername(datasource.getUsername());
        existing.setPassword(datasource.getPassword());
        existing.setApiUrl(datasource.getApiUrl());
        existing.setApiMethod(datasource.getApiMethod());
        existing.setApiHeaders(datasource.getApiHeaders());
        existing.setApiBody(datasource.getApiBody());
        existing.setUpdateTime(java.time.LocalDateTime.now());
        datasourceMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ReportDatasource datasource = getById(id);
        datasource.setDelFlag(1);
        datasource.setUpdateTime(java.time.LocalDateTime.now());
        datasourceMapper.updateById(datasource);
    }

    @Override
    public boolean testJdbcConnection(String jdbcUrl, String username, String password) {
        Connection conn = null;
        try {
            conn = JdbcUtils.getConnection(jdbcUrl, username, password);
            return conn != null;
        } catch (Exception e) {
            log.error("JDBC连接测试失败: {}", jdbcUrl, e);
            return false;
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

    @Override
    public Map<String, Object> testApiConnection(String apiUrl, String method, Map<String, String> headers, String body) {
        Map<String, Object> result = new HashMap<>();
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            if (headers != null) {
                headers.forEach(httpHeaders::set);
            }
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);
            ResponseEntity<String> response;

            if ("GET".equalsIgnoreCase(method)) {
                response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
            } else if ("POST".equalsIgnoreCase(method)) {
                response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
            } else if ("PUT".equalsIgnoreCase(method)) {
                response = restTemplate.exchange(apiUrl, HttpMethod.PUT, entity, String.class);
            } else if ("DELETE".equalsIgnoreCase(method)) {
                response = restTemplate.exchange(apiUrl, HttpMethod.DELETE, entity, String.class);
            } else {
                result.put("success", false);
                result.put("message", "不支持的HTTP方法: " + method);
                return result;
            }

            result.put("success", true);
            result.put("statusCode", response.getStatusCode().value());
            result.put("body", response.getBody());
            result.put("message", "API连接成功");
        } catch (Exception e) {
            log.error("API连接测试失败: {}", apiUrl, e);
            result.put("success", false);
            result.put("message", "API连接失败: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> executeJdbcQuery(Long datasourceId, String sql) {
        ReportDatasource datasource = getById(datasourceId);
        if (!"jdbc".equalsIgnoreCase(datasource.getDatasourceType())) {
            throw new BusinessException("该数据源不是JDBC类型");
        }
        Connection conn = null;
        try {
            conn = JdbcUtils.getConnection(datasource.getJdbcUrl(), datasource.getUsername(), datasource.getPassword());
            return JdbcUtils.executeQuery(conn, sql, null);
        } catch (Exception e) {
            log.error("执行JDBC查询失败: datasourceId={}, sql={}", datasourceId, sql, e);
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

    @Override
    public Map<String, Object> executeApiQuery(Long datasourceId, String apiUrl, String method, Map<String, String> headers, String body) {
        ReportDatasource datasource = getById(datasourceId);
        if (!"api".equalsIgnoreCase(datasource.getDatasourceType())) {
            throw new BusinessException("该数据源不是API类型");
        }
        return testApiConnection(apiUrl, method, headers, body);
    }
}