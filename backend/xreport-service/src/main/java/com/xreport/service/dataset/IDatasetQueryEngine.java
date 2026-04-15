package com.xreport.service.dataset;

import com.xreport.pojo.entity.reportdatasource.ReportDatasource;

import java.util.List;
import java.util.Map;

/**
 * 数据集查询引擎接口
 * 负责解析数据集配置，执行查询，返回统一格式结果
 */
public interface IDatasetQueryEngine {

    /**
     * 执行查询
     * @param datasource 数据源
     * @param sql SQL语句或API配置
     * @param params 查询参数
     * @return 查询结果
     */
    List<Map<String, Object>> execute(ReportDatasource datasource, String sql, Map<String, Object> params);

    /**
     * SQL注入检测
     * @param sql SQL语句
     * @return 是否安全
     */
    boolean isSqlSafe(String sql);

    /**
     * 解析并增强SQL（支持聚合计算）
     * @param sql 原始SQL
     * @param params 参数
     * @return 增强后的SQL
     */
    String buildAggQuery(String sql, Map<String, Object> params);
}