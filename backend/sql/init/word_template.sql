-- Word模板表
CREATE TABLE IF NOT EXISTS `word_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tpl_name` VARCHAR(255) NOT NULL COMMENT '模板名称',
    `tpl_path` VARCHAR(500) NOT NULL COMMENT '模板文件路径',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '模板描述',
    `status` INT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `del_flag` INT DEFAULT 0 COMMENT '删除标志: 0-正常 1-删除',
    `tenant_id` BIGINT DEFAULT 1 COMMENT '租户ID',
    `create_user_id` BIGINT DEFAULT 1 COMMENT '创建用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Word模板表';