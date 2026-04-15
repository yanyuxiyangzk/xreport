package com.xreport.pojo.dto;

import java.util.List;
import java.util.Map;

/**
 * 聚合计算请求DTO
 */
public class AggregationRequest {

    /**
     * 数据集ID
     */
    private Long datasetId;

    /**
     * SQL查询语句（可选，如果传了则覆盖数据集配置的SQL）
     */
    private String sql;

    /**
     * 分组字段列表
     */
    private List<String> groupFields;

    /**
     * 聚合字段配置
     * 例如: [{"field": "amount", "agg": "SUM", "alias": "total_amount"}, ...]
     */
    private List<AggregationField> aggFields;

    /**
     * 排序字段
     */
    private List<OrderField> orderFields;

    /**
     * 过滤条件
     */
    private Map<String, Object> filters;

    /**
     * 聚合字段定义
     */
    public static class AggregationField {
        private String field;
        private String agg;  // SUM, AVG, COUNT, MAX, MIN
        private String alias;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getAgg() {
            return agg;
        }

        public void setAgg(String agg) {
            this.agg = agg;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }
    }

    /**
     * 排序字段定义
     */
    public static class OrderField {
        private String field;
        private String direction; // ASC, DESC

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
    }

    // Getters and Setters
    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<String> getGroupFields() {
        return groupFields;
    }

    public void setGroupFields(List<String> groupFields) {
        this.groupFields = groupFields;
    }

    public List<AggregationField> getAggFields() {
        return aggFields;
    }

    public void setAggFields(List<AggregationField> aggFields) {
        this.aggFields = aggFields;
    }

    public List<OrderField> getOrderFields() {
        return orderFields;
    }

    public void setOrderFields(List<OrderField> orderFields) {
        this.orderFields = orderFields;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }
}