-- 系统用户表初始化数据
-- 密码: admin123 (BCrypt加密)
-- BCrypt加密方法: 在Java中使用BCryptPasswordEncoder加密

INSERT INTO sys_user (id, username, password, nickname, email, phone, avatar, status, del_flag, tenant_id, create_time, update_time) VALUES
(1, 'admin', '$2a$10$3aPH.PEwJJLh.Pop85DjOONdHHiIrFNOvUN8yIhkBnJpSjYTlMSou', '管理员', 'admin@example.com', '13800138000', NULL, 1, 0, 1, NOW(), NOW()),
(2, 'user1', '$2a$10$3aPH.PEwJJLh.Pop85DjOONdHHiIrFNOvUN8yIhkBnJpSjYTlMSou', '测试用户', 'user1@example.com', '13800138001', NULL, 1, 0, 1, NOW(), NOW());