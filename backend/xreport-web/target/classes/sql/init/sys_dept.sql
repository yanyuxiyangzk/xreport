INSERT INTO sys_dept (id, parent_id, ancestors, dept_name, dept_code, sort_order, status, del_flag, tenant_id) VALUES
(1, 0, '0', '总公司', 'HQ', 1, 1, 0, 1),
(2, 1, '0,1', '研发部', 'RD', 1, 1, 0, 1);
