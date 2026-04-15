-- 系统角色表初始化数据

INSERT INTO sys_role (id, role_name, role_key, role_sort, status, del_flag, tenant_id, create_time, update_time) VALUES
(1, '超级管理员', 'admin', 1, 1, 0, 1, NOW(), NOW()),
(2, '普通用户', 'user', 2, 1, 0, 1, NOW(), NOW());