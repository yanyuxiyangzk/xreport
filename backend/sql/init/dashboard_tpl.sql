-- 大屏模板表
CREATE TABLE IF NOT EXISTS dashboard_tpl (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    tpl_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    tpl_code VARCHAR(50) NOT NULL COMMENT '模板编码',
    description VARCHAR(500) COMMENT '描述',
    config JSON COMMENT '配置JSON（包含组件配置、布局、样式等）',
    preview_url VARCHAR(500) COMMENT '预览URL',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0停用',
    create_user BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_tpl_code (tpl_code)
) COMMENT='大屏模板表';

-- 初始化示例大屏模板
INSERT INTO dashboard_tpl (id, tpl_name, tpl_code, description, config, preview_url, status, create_user, create_time)
VALUES (1, '示例大屏', 'demo_dashboard', '这是一个示例大屏模板',
    '{"screenWidth":1920,"screenHeight":1080,"bgColor":"#0a0a0a","widgets":[{"id":"widget1","type":"chart","name":"销售图表","x":100,"y":100,"width":400,"height":300,"zIndex":1,"chartType":"line","chartConfig":{"title":{"text":"月度销售额"},"legend":{"data":["销售额"]},"xAxis":{"type":"category","data":["1月","2月","3月","4月","5月"]},"yAxis":{"type":"value"},"series":[{"name":"销售额","type":"line","data":[120,200,150,300,250]}]}}]}',
    '/preview/demo_dashboard.png', 1, 1, NOW());