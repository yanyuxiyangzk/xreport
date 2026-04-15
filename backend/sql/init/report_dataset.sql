-- 报表数据集表
CREATE TABLE IF NOT EXISTS report_dataset (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    dataset_name VARCHAR(100) NOT NULL COMMENT '数据集名称',
    dataset_code VARCHAR(100) NOT NULL COMMENT '数据集编码',
    dataset_desc VARCHAR(500) COMMENT '数据集描述',
    datasource_id BIGINT NOT NULL COMMENT '关联数据源ID',
    sql_statement TEXT COMMENT 'SQL查询语句',
    agg_type VARCHAR(20) DEFAULT 'NONE' COMMENT '聚合类型：NONE-不聚合, GROUP_BY-分组聚合',
    group_fields VARCHAR(500) COMMENT '分组字段（逗号分隔）',
    agg_fields TEXT COMMENT '聚合字段配置（JSON格式）',
    order_fields VARCHAR(500) COMMENT '排序字段',
    dataset_type VARCHAR(20) DEFAULT 'JDBC' COMMENT '数据集类型：JDBC, API',
    merchant_id BIGINT COMMENT '租户ID',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用，0禁用',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag TINYINT DEFAULT 1 COMMENT '删除标记：1存在，0删除',
    UNIQUE KEY uk_dataset_code (dataset_code)
) COMMENT='报表数据集表';
