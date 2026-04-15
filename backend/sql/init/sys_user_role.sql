-- 用户角色关联表初始化数据

INSERT INTO sys_user_role (id, user_id, role_id, tenant_id, create_time) VALUES
(1, 1, 1, 1, NOW()),
(2, 2, 2, 1, NOW());