-- 大屏模板表
CREATE TABLE IF NOT EXISTS dashboard_tpl (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    tpl_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    tpl_code VARCHAR(50) NOT NULL COMMENT '模板编码',
    screen_width INT DEFAULT 1920 COMMENT '大屏宽度',
    screen_height INT DEFAULT 1080 COMMENT '大屏高度',
    bg_color VARCHAR(20) DEFAULT '#0a0a0a' COMMENT '背景色',
    widget_config JSON COMMENT '组件配置（JSON格式）',
    merchant_id BIGINT DEFAULT 1 COMMENT '租户ID',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 2停用',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标记：0正常 1删除',
    UNIQUE KEY uk_tpl_code (tpl_code)
) COMMENT='大屏模板表';

-- 初始化示例大屏模板
INSERT INTO dashboard_tpl (id, tpl_name, tpl_code, screen_width, screen_height, bg_color, widget_config, status, create_by, create_time, del_flag)
VALUES (1, '示例大屏', 'demo_dashboard', 1920, 1080, '#0a0a0a',
    '[{"id":"widget1","type":"chart","name":"销售图表","x":100,"y":100,"width":400,"height":300,"zIndex":1,"chartType":"line","chartConfig":{"title":{"text":"月度销售额"},"legend":{"data":["销售额"]},"xAxis":{"type":"category","data":["1月","2月","3月","4月","5月"]},"yAxis":{"type":"value"},"series":[{"name":"销售额","type":"line","data":[120,200,150,300,250]}]}}]',
    1, 1, NOW(), 0);
