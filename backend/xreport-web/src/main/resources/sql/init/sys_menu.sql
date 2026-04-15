INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, sort_order, visible, perms, status, del_flag, tenant_id) VALUES
(1, 0, 'M', '系统管理', '/system', 'system/index', 1, '0', '', '0', 0, 1),
(2, 1, 'C', '用户管理', '/system/user', 'system/user/index', 1, '0', 'system:user:list', '0', 0, 1),
(3, 1, 'C', '角色管理', '/system/role', 'system/role/index', 2, '0', 'system:role:list', '0', 0, 1),
(4, 1, 'C', '部门管理', '/system/dept', 'system/dept/index', 3, '0', 'system:dept:list', '0', 0, 1),
(5, 1, 'C', '菜单管理', '/system/menu', 'system/menu/index', 4, '0', 'system:menu:list', '0', 0, 1),
(6, 0, 'M', '报表管理', '/report', 'report/index', 2, '0', '', '0', 0, 1),
(7, 6, 'C', '报表设计', '/report/design', 'report/design/index', 1, '0', 'report:design:list', '0', 0, 1);
