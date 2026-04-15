-- 报表模板表
CREATE TABLE IF NOT EXISTS report_tpl (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    tpl_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    tpl_code VARCHAR(50) NOT NULL COMMENT '模板编码',
    tpl_type VARCHAR(20) DEFAULT 'luckysheet' COMMENT '模板类型：luckysheet(word)',
    sheet_count INT DEFAULT 1 COMMENT 'Sheet数量',
    preview_url VARCHAR(500) COMMENT '预览图URL',
    merchant_id BIGINT DEFAULT 1 COMMENT '租户ID',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 2停用',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标记：0正常 1删除',
    UNIQUE KEY uk_tpl_code (tpl_code)
) COMMENT='报表模板表';

-- 报表模板Sheet表
CREATE TABLE IF NOT EXISTS report_tpl_sheet (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    tpl_id BIGINT NOT NULL COMMENT '模板ID',
    sheet_name VARCHAR(100) NOT NULL COMMENT 'Sheet名称',
    sheet_index INT DEFAULT 0 COMMENT 'Sheet索引',
    sheet_config JSON COMMENT 'Sheet配置（Luckysheet配置）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标记：0正常 1删除',
    KEY idx_tpl_id (tpl_id)
) COMMENT='报表模板Sheet表';

-- Luckysheet单元格数据表
CREATE TABLE IF NOT EXISTS luckysheet_cell (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    sheet_id BIGINT NOT NULL COMMENT 'Sheet ID',
    row_index INT NOT NULL COMMENT '行索引',
    column_index INT NOT NULL COMMENT '列索引',
    cell_value TEXT COMMENT '单元格值',
    cell_config JSON COMMENT '单元格配置（样式、对齐等）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标记：0正常 1删除',
    KEY idx_sheet_id (sheet_id),
    KEY idx_row_col (sheet_id, row_index, column_index)
) COMMENT='Luckysheet单元格数据表';

-- 初始化示例报表模板
INSERT INTO report_tpl (id, tpl_name, tpl_code, tpl_type, sheet_count, status, create_by, create_time, del_flag)
VALUES (1, '销售报表模板', 'sales_report', 'luckysheet', 1, 1, 1, NOW(), 0);

-- 初始化示例Sheet
INSERT INTO report_tpl_sheet (id, tpl_id, sheet_name, sheet_index, sheet_config, create_time, del_flag)
VALUES (1, 1, '销售数据', 0, '{"name":"销售数据","color":"","config":{"columnlen":{"0":100},"rowlen":{"0":120}}}', NOW(), 0);