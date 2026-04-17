-- H2 Database Schema for Testing
-- Create sys_user table
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(255),
    status INT DEFAULT 1,
    del_flag INT DEFAULT 0,
    tenant_id BIGINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create sys_role table
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    status INT DEFAULT 1,
    del_flag INT DEFAULT 0,
    tenant_id BIGINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create sys_menu table
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    menu_name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    order_num INT DEFAULT 0,
    path VARCHAR(255),
    component VARCHAR(255),
    menu_type VARCHAR(10),
    visible INT DEFAULT 1,
    status INT DEFAULT 1,
    perms VARCHAR(255),
    icon VARCHAR(100),
    del_flag INT DEFAULT 0,
    tenant_id BIGINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create sys_dept table
CREATE TABLE IF NOT EXISTS sys_dept (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT 0,
    dept_name VARCHAR(50) NOT NULL,
    leader VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    status INT DEFAULT 1,
    del_flag INT DEFAULT 0,
    tenant_id BIGINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create sys_user_role table
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    del_flag INT DEFAULT 0,
    tenant_id BIGINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create sys_role_menu table
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    del_flag INT DEFAULT 0,
    tenant_id BIGINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create report_datasource table
CREATE TABLE IF NOT EXISTS report_datasource (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    datasource_name VARCHAR(100) NOT NULL,
    datasource_type VARCHAR(50),
    jdbc_url VARCHAR(500),
    username VARCHAR(100),
    password VARCHAR(255),
    api_url VARCHAR(500),
    api_method VARCHAR(20),
    api_headers TEXT,
    api_body TEXT,
    merchant_id BIGINT DEFAULT 1,
    status INT DEFAULT 1,
    del_flag INT DEFAULT 0,
    tenant_id BIGINT DEFAULT 1,
    create_user_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create report_dataset table
CREATE TABLE IF NOT EXISTS report_dataset (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dataset_name VARCHAR(100) NOT NULL,
    dataset_code VARCHAR(100) NOT NULL UNIQUE,
    dataset_desc VARCHAR(500),
    datasource_id BIGINT,
    datasource_name VARCHAR(100),
    sql_statement TEXT,
    dataset_type VARCHAR(50),
    merchant_id BIGINT DEFAULT 1,
    status INT DEFAULT 1,
    del_flag INT DEFAULT 0,
    tenant_id BIGINT DEFAULT 1,
    create_user_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create report_tpl table
CREATE TABLE IF NOT EXISTS report_tpl (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tpl_name VARCHAR(100) NOT NULL,
    tpl_type INT DEFAULT 1,
    is_example INT DEFAULT 0,
    search_form_type INT DEFAULT 1,
    cdn_host VARCHAR(255),
    water_mark_type INT DEFAULT 0,
    permission_type INT DEFAULT 1,
    share_token VARCHAR(100),
    share_expire_time TIMESTAMP,
    thumbnail VARCHAR(500),
    status INT DEFAULT 1,
    del_flag INT DEFAULT 0,
    tenant_id BIGINT DEFAULT 1,
    create_user_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create report_tpl_sheet table
CREATE TABLE IF NOT EXISTS report_tpl_sheet (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tpl_id BIGINT NOT NULL,
    sheet_name VARCHAR(100),
    sheet_index INT DEFAULT 0,
    sheet_order INT DEFAULT 0,
    is_loop INT DEFAULT 0,
    loop_settings TEXT,
    datasource_id BIGINT,
    sql_str TEXT,
    params TEXT,
    sort INT DEFAULT 0,
    sheet_data TEXT,
    del_flag INT DEFAULT 0,
    tenant_id BIGINT DEFAULT 1,
    create_user_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create luckysheet_report_cell table
CREATE TABLE IF NOT EXISTS luckysheet_report_cell (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tpl_id BIGINT NOT NULL,
    sheet_index INT DEFAULT 0,
    cell_data TEXT,
    del_flag INT DEFAULT 0,
    tenant_id BIGINT DEFAULT 1,
    create_user_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create index for better query performance
CREATE INDEX IF NOT EXISTS idx_user_username ON sys_user(username);
CREATE INDEX IF NOT EXISTS idx_dataset_code ON report_dataset(dataset_code);
CREATE INDEX IF NOT EXISTS idx_tpl_status ON report_tpl(status);
