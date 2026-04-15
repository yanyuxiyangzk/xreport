CREATE TABLE IF NOT EXISTS report_tpl_sheet (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Sheet ID',
  tpl_id BIGINT NOT NULL COMMENT '模板ID',
  sheet_name VARCHAR(100) NOT NULL COMMENT 'Sheet名称',
  sheet_order INT DEFAULT 0 COMMENT 'Sheet顺序',
  is_loop TINYINT DEFAULT 2 COMMENT '是否循环：1=是,2=否',
  loop_settings TEXT COMMENT '循环设置JSON',
  datasource_id BIGINT COMMENT '数据源ID',
  sql_str TEXT COMMENT 'SQL查询语句',
  params TEXT COMMENT '参数配置JSON',
  sort INT DEFAULT 0 COMMENT '排序',
  del_flag TINYINT DEFAULT 0 COMMENT '删除标记',
  tenant_id BIGINT DEFAULT 1 COMMENT '租户ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '报表Sheet表';

INSERT INTO report_tpl_sheet (id, tpl_id, sheet_name, sheet_order, is_loop, del_flag, tenant_id) VALUES
(1, 1, '销售汇总', 1, 2, 0, 1),
(2, 1, '明细数据', 2, 2, 0, 1),
(3, 2, '主数据', 1, 2, 0, 1);
