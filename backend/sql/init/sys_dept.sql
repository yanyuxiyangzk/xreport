-- 系统部门表初始化数据

INSERT INTO sys_dept (id, parent_id, ancestors, dept_name, dept_code, sort_order, leader_user_id, phone, email, status, del_flag, tenant_id, create_time, update_time) VALUES
(1, 0, '0', '总公司', 'HQ', 1, 1, '010-12345678', 'hq@example.com', 1, 0, 1, NOW(), NOW()),
(2, 1, '0,1', '研发部', 'RD', 1, NULL, '010-12345679', 'rd@example.com', 1, 0, 1, NOW(), NOW()),
(3, 1, '0,1', '市场部', 'MARKET', 2, NULL, '010-12345680', 'market@example.com', 1, 0, 1, NOW(), NOW());