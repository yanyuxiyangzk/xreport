package com.xreport.service.report;

import com.github.pagehelper.PageInfo;
import com.xreport.pojo.dto.DatasourceDTO;
import com.xreport.pojo.entity.reportdatasource.ReportDatasource;

import java.util.List;
import java.util.Map;

public interface IReportDatasourceService {

    /**
     * 分页查询数据源
     */
    PageInfo<ReportDatasource> pageQuery(DatasourceDTO dto);

    /**
     * 获取启用的数据源列表
     */
    List<ReportDatasource> listEnabled();

    /**
     * 根据ID获取数据源详情
     */
    ReportDatasource getById(Long id);

    /**
     * 创建数据源
     */
    void add(ReportDatasource datasource);

    /**
     * 更新数据源
     */
    void update(ReportDatasource datasource);

    /**
     * 删除数据源
     */
    void delete(Long id);

    /**
     * 测试JDBC连接
     */
    boolean testJdbcConnection(String jdbcUrl, String username, String password);

    /**
     * 测试API连接
     */
    Map<String, Object> testApiConnection(String apiUrl, String method, Map<String, String> headers, String body);

    /**
     * 执行JDBC查询
     */
    List<Map<String, Object>> executeJdbcQuery(Long datasourceId, String sql);

    /**
     * 执行API查询
     */
    Map<String, Object> executeApiQuery(Long datasourceId, String apiUrl, String method, Map<String, String> headers, String body);
}