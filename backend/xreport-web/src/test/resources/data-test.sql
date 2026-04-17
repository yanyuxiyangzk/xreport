-- H2 Test Data Initialization
-- Password is BCrypt encoded 'admin123'
-- Default admin user: admin / admin123

INSERT INTO sys_user (id, username, password, nickname, email, phone, status, del_flag, tenant_id, create_time)
VALUES (1, 'admin', '$2a$10$ftOp2n0D1EKQaaZT7L8IR.PAnXY.FoiMjCiSfAFikdf7BgEOcsPnO', 'Administrator', 'admin@example.com', '13800138000', 1, 0, 1, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE username=username;

INSERT INTO sys_user (id, username, password, nickname, email, phone, status, del_flag, tenant_id, create_time)
VALUES (2, 'testuser', '$2a$10$ftOp2n0D1EKQaaZT7L8IR.PAnXY.FoiMjCiSfAFikdf7BgEOcsPnO', 'Test User', 'test@example.com', '13900139000', 1, 0, 1, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE username=username;

-- Insert test roles
INSERT INTO sys_role (id, role_name, role_code, description, status, del_flag, tenant_id)
VALUES (1, 'Administrator', 'admin', 'System Administrator', 1, 0, 1)
ON DUPLICATE KEY UPDATE role_name=role_name;

INSERT INTO sys_role (id, role_name, role_code, description, status, del_flag, tenant_id)
VALUES (2, 'User', 'user', 'Regular User', 1, 0, 1)
ON DUPLICATE KEY UPDATE role_name=role_name;

-- Insert menus with proper permissions
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, del_flag, tenant_id)
VALUES (1, 'System', 0, 1, 'system', NULL, 'M', 1, 1, NULL, 0, 1)
ON DUPLICATE KEY UPDATE menu_name=menu_name;

INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, del_flag, tenant_id)
VALUES (2, 'User Management', 1, 1, 'user', 'system/user', 'C', 1, 1, 'system:user:list', 0, 1)
ON DUPLICATE KEY UPDATE menu_name=menu_name;

INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, del_flag, tenant_id)
VALUES (3, 'Report Management', 0, 2, 'report', NULL, 'M', 1, 1, NULL, 0, 1)
ON DUPLICATE KEY UPDATE menu_name=menu_name;

INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, del_flag, tenant_id)
VALUES (4, 'Report Designer', 3, 1, 'designer', 'report/designer', 'C', 1, 1, 'report:manage', 0, 1)
ON DUPLICATE KEY UPDATE menu_name=menu_name;

INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, del_flag, tenant_id)
VALUES (5, 'Datasource', 3, 2, 'datasource', 'report/datasource', 'C', 1, 1, 'datasource:manage', 0, 1)
ON DUPLICATE KEY UPDATE menu_name=menu_name;

INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, del_flag, tenant_id)
VALUES (6, 'Dataset', 3, 3, 'dataset', 'report/dataset', 'C', 1, 1, 'report:manage', 0, 1)
ON DUPLICATE KEY UPDATE menu_name=menu_name;

-- Assign admin role to admin user
INSERT INTO sys_user_role (user_id, role_id, del_flag, tenant_id)
VALUES (1, 1, 0, 1)
ON DUPLICATE KEY UPDATE user_id=user_id;

-- Assign user role to test user
INSERT INTO sys_user_role (user_id, role_id, del_flag, tenant_id)
VALUES (2, 2, 0, 1)
ON DUPLICATE KEY UPDATE user_id=user_id;

-- Assign permissions to admin role
INSERT INTO sys_role_menu (role_id, menu_id, del_flag, tenant_id)
VALUES (1, 1, 0, 1)
ON DUPLICATE KEY UPDATE role_id=role_id;

INSERT INTO sys_role_menu (role_id, menu_id, del_flag, tenant_id)
VALUES (1, 2, 0, 1)
ON DUPLICATE KEY UPDATE role_id=role_id;

INSERT INTO sys_role_menu (role_id, menu_id, del_flag, tenant_id)
VALUES (1, 3, 0, 1)
ON DUPLICATE KEY UPDATE role_id=role_id;

INSERT INTO sys_role_menu (role_id, menu_id, del_flag, tenant_id)
VALUES (1, 4, 0, 1)
ON DUPLICATE KEY UPDATE role_id=role_id;

INSERT INTO sys_role_menu (role_id, menu_id, del_flag, tenant_id)
VALUES (1, 5, 0, 1)
ON DUPLICATE KEY UPDATE role_id=role_id;

INSERT INTO sys_role_menu (role_id, menu_id, del_flag, tenant_id)
VALUES (1, 6, 0, 1)
ON DUPLICATE KEY UPDATE role_id=role_id;

-- Insert test department
INSERT INTO sys_dept (id, parent_id, dept_name, leader, phone, email, status, del_flag, tenant_id)
VALUES (1, 0, 'Headquarters', 'Admin', '13800138000', 'admin@company.com', 1, 0, 1)
ON DUPLICATE KEY UPDATE dept_name=dept_name;

-- Insert test datasource
INSERT INTO report_datasource (id, datasource_name, datasource_type, jdbc_url, username, password, status, del_flag, tenant_id, create_user_id)
VALUES (1, 'Test MySQL', 'mysql', 'jdbc:mysql://localhost:3306/testdb', 'root', 'test123', 1, 0, 1, 1)
ON DUPLICATE KEY UPDATE datasource_name=datasource_name;

INSERT INTO report_datasource (id, datasource_name, datasource_type, api_url, api_method, status, del_flag, tenant_id, create_user_id)
VALUES (2, 'Test API', 'api', 'https://api.example.com/data', 'GET', 1, 0, 1, 1)
ON DUPLICATE KEY UPDATE datasource_name=datasource_name;

-- Insert test dataset
INSERT INTO report_dataset (id, dataset_name, dataset_code, dataset_desc, datasource_id, datasource_name, sql_statement, dataset_type, status, del_flag, tenant_id, create_user_id)
VALUES (1, 'Test Dataset', 'test_ds', 'Test dataset for integration testing', 1, 'Test MySQL', 'SELECT * FROM sys_user WHERE del_flag = 0', 'SQL', 1, 0, 1, 1)
ON DUPLICATE KEY UPDATE dataset_name=dataset_name;

-- Insert test report template
INSERT INTO report_tpl (id, tpl_name, tpl_type, is_example, search_form_type, status, del_flag, tenant_id, create_user_id)
VALUES (1, 'Test Report', 1, 0, 1, 1, 0, 1, 1)
ON DUPLICATE KEY UPDATE tpl_name=tpl_name;

INSERT INTO report_tpl (id, tpl_name, tpl_type, is_example, search_form_type, status, del_flag, tenant_id, create_user_id)
VALUES (2, 'Example Report', 1, 1, 1, 1, 0, 1, 1)
ON DUPLICATE KEY UPDATE tpl_name=tpl_name;

-- Insert test report sheet
INSERT INTO report_tpl_sheet (id, tpl_id, sheet_name, sheet_index, del_flag, tenant_id, create_user_id)
VALUES (1, 1, 'Sheet1', 0, 0, 1, 1)
ON DUPLICATE KEY UPDATE sheet_name=sheet_name;

INSERT INTO report_tpl_sheet (id, tpl_id, sheet_name, sheet_index, del_flag, tenant_id, create_user_id)
VALUES (2, 2, 'Data', 0, 0, 1, 1)
ON DUPLICATE KEY UPDATE sheet_name=sheet_name;
