CREATE TABLE IF NOT EXISTS report_tpl (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
  tpl_name VARCHAR(100) NOT NULL COMMENT '模板名称',
  tpl_type TINYINT DEFAULT 1 COMMENT '模板类型：1=Excel报表,2=Word模板,3=大屏',
  is_example TINYINT DEFAULT 2 COMMENT '是否示例：1=是,2=否',
  search_form_type TINYINT DEFAULT 1 COMMENT '查询组件位置：1=顶部,2=侧边',
  cdn_host VARCHAR(200) COMMENT 'CDN域名',
  water_mark_type TINYINT DEFAULT 0 COMMENT '水印类型',
  permission_type TINYINT DEFAULT 1 COMMENT '权限类型',
  share_token VARCHAR(64) COMMENT '分享Token',
  share_expire_time DATETIME COMMENT '分享过期时间',
  thumbnail VARCHAR(500) COMMENT '缩略图URL',
  status TINYINT DEFAULT 1 COMMENT '状态：0=禁用,1=正常',
  del_flag TINYINT DEFAULT 0 COMMENT '删除标记',
  tenant_id BIGINT DEFAULT 1 COMMENT '租户ID',
  create_user_id BIGINT COMMENT '创建用户ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '报表模板表';

INSERT INTO report_tpl (id, tpl_name, tpl_type, is_example, search_form_type, status, del_flag, tenant_id, create_user_id) VALUES
(1, '销售报表模板', 1, 1, 1, 1, 0, 1, 1),
(2, '财务报表模板', 1, 2, 1, 1, 0, 1, 1);
