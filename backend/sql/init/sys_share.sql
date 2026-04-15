-- 分享表
CREATE TABLE IF NOT EXISTS sys_share (
    share_token VARCHAR(64) PRIMARY KEY COMMENT '分享Token（UUID）',
    share_type VARCHAR(20) NOT NULL COMMENT '分享类型: report-报表, dashboard-大屏',
    target_id BIGINT NOT NULL COMMENT '关联的资源ID',
    target_name VARCHAR(200) COMMENT '资源名称（冗余存储）',
    share_user_id BIGINT NOT NULL COMMENT '分享人ID',
    access_limit INT DEFAULT 0 COMMENT '访问次数限制，0表示不限制',
    access_count INT DEFAULT 0 COMMENT '已访问次数',
    expire_time DATETIME COMMENT '过期时间，NULL表示永不过期',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标记：0正常 1删除',
    INDEX idx_share_type (share_type),
    INDEX idx_target_id (target_id),
    INDEX idx_share_user_id (share_user_id)
) COMMENT='分享表';
