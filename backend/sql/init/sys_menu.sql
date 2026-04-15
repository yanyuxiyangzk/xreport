-- 系统菜单表初始化数据
-- menu_type: M-目录 C-菜单 B-按钮
-- visible: 0-显示 1-隐藏
-- is_frame: 0-否 1-是

INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, icon, is_frame, is_cache, sort_order, visible, perms, status, del_flag, tenant_id, create_time, update_time) VALUES
(1, 0, 'M', '系统管理', '/system', 'system/index', 'setting', 0, 0, 1, '0', '', '0', 0, 1, NOW(), NOW()),
(2, 1, 'C', '用户管理', '/system/user', 'system/user/index', 'user', 0, 0, 1, '0', 'system:user:list', '0', 0, 1, NOW(), NOW()),
(3, 1, 'C', '角色管理', '/system/role', 'system/role/index', 'peoples', 0, 0, 2, '0', 'system:role:list', '0', 0, 1, NOW(), NOW()),
(4, 1, 'C', '部门管理', '/system/dept', 'system/dept/index', 'tree', 0, 0, 3, '0', 'system:dept:list', '0', 0, 1, NOW(), NOW()),
(5, 1, 'C', '菜单管理', '/system/menu', 'system/menu/index', 'menu', 0, 0, 4, '0', 'system:menu:list', '0', 0, 1, NOW(), NOW()),
(6, 0, 'M', '报表管理', '/report', 'report/index', 'chart', 0, 0, 2, '0', '', '0', 0, 1, NOW(), NOW()),
(7, 6, 'C', '报表设计', '/report/design', 'report/design/index', 'edit', 0, 0, 1, '0', 'report:design:list', '0', 0, 1, NOW(), NOW());