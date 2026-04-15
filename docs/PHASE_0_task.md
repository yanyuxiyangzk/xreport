# XReport Phase 0: 项目骨架

## 任务目标
创建 XReport 后端 Spring Boot 3 Maven 多模块项目骨架。

## 输出路径
D:\project\aicoding\XReport\backend\

## 具体要求

### 1. 根 pom.xml（backend/pom.xml）
- groupId: com.xreport
- artifactId: xreport
- version: 1.0.0-SNAPSHOT
- packaging: pom
- Java 版本: 17
- Spring Boot 版本: 3.2.6
- 父项目管理以下模块：
  - xreport-common
  - xreport-pojo
  - xreport-mapper
  - xreport-service
  - xreport-web

### 2. xreport-common 模块（backend/xreport-common/pom.xml + 源码）

**pom.xml**:
- packaging: jar
- 依赖: spring-boot-starter (内置)、hutool-all 5.8.28、commons-lang3 3.14.0、lombok

**源码结构**:
```
src/main/java/com/xreport/common/
├── XReportApplication.java          # 启动类（非 web 模块，标记 @Configuration）
├── result/Result.java               # 统一响应封装（code/message/data）
├── result/ResultCode.java           # 响应码枚举（SUCCESS/FAIL/UNAUTHORIZED等）
├── exception/BusinessException.java # 业务异常（运行时异常）
├── exception/GlobalExceptionHandler.java  # 全局异常处理（@RestControllerAdvice）
├── constant/Constants.java          # 通用常量
└── util/
    ├── JdbcUtils.java               # 多数据库工具类（参考 SpringReport 设计）
    ├── PasswordEncoder.java         # BCrypt 密码加密工具
    └── IdWorker.java                # Snowflake ID 生成（MyBatis-Plus 内置的 IdWorker）
```

**Result.java 要求**:
```java
// 泛型统一响应
public class Result<T> {
    private int code;
    private String message;
    private T data;
    // 静态方法: ok(data), fail(message), ok()
}
```

**ResultCode.java 要求**:
```java
// 枚举: SUCCESS(200), FAIL(500), UNAUTHORIZED(401), FORBIDDEN(403)
```

**BusinessException 要求**:
```java
// 运行时异常，支持 (String message) 和 (ResultCode code) 构造
```

**GlobalExceptionHandler 要求**:
```java
// @RestControllerAdvice
// 处理 BusinessException → Result.fail()
// 处理 MethodArgumentNotValidException → 字段校验错误
// 处理 Exception → 500 系统错误
```

**JdbcUtils.java 要求**:
```java
// 多数据库工具类，初期只支持 MySQL
// public static Connection getConnection(String jdbcUrl, String username, String password)
// public static List<Map<String, Object>> executeQuery(Connection conn, String sql, List<Object> params)
// public static void close(Connection, PreparedStatement, ResultSet)
// 使用 Druid 连接池
```

### 3. xreport-pojo 模块（backend/xreport-pojo/pom.xml + 源码）

**pom.xml**: 依赖 xreport-common

**源码结构**:
```
src/main/java/com/xreport/pojo/
├── entity/                          # MyBatis-Plus 实体（对应数据库表）
│   └── sysuser/SysUser.java         # 用户表实体（标注 @TableName("sys_user")）
│   └── sysrole/SysRole.java         # 角色表实体
│   └── sysdept/SysDept.java         # 部门表实体
│   └── sysmenu/SysMenu.java         # 菜单表实体
│   └── sysmerchant/SysMerchant.java  # 租户商户实体
│   └── reportdatasource/ReportDatasource.java  # 数据源实体
│   └── reporttpl/ReportTpl.java     # 报表模板实体
│   └── reporttplsheet/ReportTplSheet.java  # 报表Sheet实体
│   └── luckysheetcell/LuckysheetCell.java  # Luckysheet单元格实体
├── dto/                             # 数据传输对象
│   └── LoginRequest.java            # 登录请求（username/password）
│   └── LoginResponse.java           # 登录响应（token/userInfo）
└── vo/                              # 视图对象
    └── UserVO.java                  # 用户视图（脱敏）
```

**实体规范**:
- 使用 @TableName("表名") 指定数据库表名
- 使用 @TableId(type = IdType.ASSIGN_ID) 的 Snowflake ID
- 使用 @TableField("db_column") 处理字段名映射
- 使用 MyBatis-Plus 的 @TableLogic 做逻辑删除
- 日期字段用 LocalDateTime

**SysUser 字段（参考 SpringReport sys_user）**:
```java
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private Long deptId;
    private Long merchantId;     // 租户ID
    private Integer status;      // 1正常 2停用
    private String email;
    private String phone;
    private Integer sex;
    private String avatar;
    private Long createBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;
    private Integer delFlag;     // 0删除 1存在
}
```

**SysRole、SysDept、SysMenu 参考 SpringReport sys_* 表设计**:
- 保持与 SpringReport 相同的字段和表结构
- 确保重建后的 XReport 能直接使用 SpringReport 的 SQL 脚本

### 4. xreport-mapper 模块（backend/xreport-mapper/pom.xml + 源码）

**pom.xml**: 依赖 xreport-pojo
- 额外依赖: mybatis-plus-boot-starter 3.5.6、mysql-connector-j 8.0.33、druid-spring-boot-starter 1.2.23

**源码结构**:
```
src/main/java/com/xreport/mapper/
├── sysuser/SysUserMapper.java      # extends BaseMapper<SysUser>
├── sysrole/SysRoleMapper.java
├── sysdept/SysDeptMapper.java
├── sysmenu/SysMenuMapper.java
├── sysmerchant/SysMerchantMapper.java
├── reportdatasource/ReportDatasourceMapper.java
├── reporttpl/ReportTplMapper.java
├── reporttplsheet/ReportTplSheetMapper.java
└── luckysheetcell/LuckysheetCellMapper.java
```

**application.yml**:
```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/xreport?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root123
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.xreport.pojo.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: assign_id
      logic-delete-field: delFlag
      logic-delete-value: 0
      logic-not-delete-value: 1
```

### 5. xreport-service 模块（backend/xreport-service/pom.xml + 源码）

**pom.xml**: 依赖 xreport-mapper、xreport-common
- 额外依赖: spring-boot-starter-web

**源码结构**:
```
src/main/java/com/xreport/service/
├── sysuser/
│   ├── ISysUserService.java         # 接口
│   └── impl/SysUserServiceImpl.java # extends ServiceImpl<SysUserMapper, SysUser>
├── sysrole/
├── sysdept/
├── sysmenu/
├── sysmerchant/
├── reportdatasource/
├── reporttpl/
├── reporttplsheet/
└── luckysheetcell/
```

**实现要求**:
- 所有 ServiceImpl 继承 MyBatis-Plus 的 ServiceImpl
- ServiceImpl 中注入其他 Service（使用 @Lazy 避免循环依赖）
- 业务逻辑暂时只实现最简单的 CRUD
- 复杂业务逻辑在后续 Phase 完成

### 6. xreport-web 模块（backend/xreport-web/pom.xml + 源码）

**pom.xml**: 依赖 xreport-service
- 额外依赖:
  - spring-boot-starter-web
  - spring-boot-starter-security
  - spring-boot-starter-redis
  - spring-boot-starter-actuator
  - jjwt-api 0.12.6 / jjwt-impl 0.12.6 / jjwt-jackson 0.12.6
  - hutool-all 5.8.28

**源码结构**:
```
src/main/java/com/xreport/
├── XReportApplication.java           # Spring Boot 启动类
├── config/
│   ├── SecurityConfig.java          # Spring Security 6 配置
│   ├── RedisConfig.java             # Redis 配置
│   └── WebConfig.java               # Web MVC 配置
├── controller/
│   ├── sysuser/SysUserController.java
│   ├── sysrole/SysRoleController.java
│   ├── sysdept/SysDeptController.java
│   ├── sysmenu/SysMenuController.java
│   ├── sysmerchant/SysMerchantController.java
│   └── reportdatasource/ReportDatasourceController.java
├── security/
│   ├── JwtTokenService.java        # JWT Token 生成和验证
│   ├── JwtAuthenticationFilter.java # JWT 认证过滤器
│   └── UserDetailsServiceImpl.java  # Spring Security UserDetailsService
└── interceptor/
    └── LogInterceptor.java          # 操作日志拦截器
```

**SecurityConfig.java 要求**:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/captcha").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

**JwtTokenService.java 要求**:
```java
// 生成 JWT Token（userId, username, merchantId）
// 验证 JWT Token
// 从 Token 中获取用户信息
// Token 过期时间: 24小时
// 签名算法: HS512
// secret: "xreport-secret-key-2026-very-long-secret-for-jwt-signing"
```

**Controller 规范**:
```java
@RestController
@RequestMapping("/api/sys/user")
public class SysUserController {
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest request) { ... }
    
    @PostMapping("/register")
    public Result<Void> register(@RequestBody SysUser user) { ... }
    
    @GetMapping("/list")
    public Result<List<SysUser>> list(SysUser query) { ... }
    
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) { ... }
    
    @PostMapping
    public Result<Void> save(@RequestBody SysUser user) { ... }
    
    @PutMapping
    public Result<Void> update(@RequestBody SysUser user) { ... }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { ... }
}
```

### 7. SQL 初始化脚本（backend/sql/）

创建 MySQL 建表脚本：backend/sql/xreport_init.sql

基于 SpringReport sys_* 表结构创建 XReport 的系统表：

```sql
-- 租户商户表
CREATE TABLE sys_merchant (
    id BIGINT PRIMARY KEY COMMENT '主键',
    merchant_name VARCHAR(100) NOT NULL COMMENT '商户名称',
    merchant_code VARCHAR(50) UNIQUE NOT NULL COMMENT '商户编码',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 2停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag TINYINT DEFAULT 1
) COMMENT='商户表';

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    nickname VARCHAR(50),
    dept_id BIGINT,
    merchant_id BIGINT,
    status TINYINT DEFAULT 1,
    email VARCHAR(100),
    phone VARCHAR(20),
    sex TINYINT DEFAULT 0,
    avatar VARCHAR(255),
    create_by BIGINT,
    create_time DATETIME,
    update_by BIGINT,
    update_time DATETIME,
    del_flag TINYINT DEFAULT 1
) COMMENT='用户表';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    role_key VARCHAR(50) NOT NULL,
    role_sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME,
    del_flag TINYINT DEFAULT 1
) COMMENT='角色表';

-- 部门表
CREATE TABLE sys_dept (
    id BIGINT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    dept_name VARCHAR(50) NOT NULL,
    sort INT DEFAULT 0,
    leader VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    status TINYINT DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME,
    del_flag TINYINT DEFAULT 1
) COMMENT='部门表';

-- 菜单表
CREATE TABLE sys_menu (
    id BIGINT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    path VARCHAR(200),
    name VARCHAR(50),
    component VARCHAR(255),
    menu_type TINYINT COMMENT '1菜单 2按钮',
    perms VARCHAR(100) COMMENT '权限标识',
    icon VARCHAR(50),
    sort INT DEFAULT 0,
    visible TINYINT DEFAULT 1,
    status TINYINT DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME,
    del_flag TINYINT DEFAULT 1
) COMMENT='菜单表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id)
) COMMENT='用户角色关联';

-- 角色菜单关联表
CREATE TABLE sys_role_menu (
    role_id BIGINT,
    menu_id BIGINT,
    PRIMARY KEY (role_id, menu_id)
) COMMENT='角色菜单关联';
```

### 8. README.md（backend/README.md）

创建项目 README，包含：
- 项目简介
- 技术栈
- Maven 模块说明
- 环境要求（JDK 17+, Maven 3.8+, MySQL 8.0+）
- 快速开始步骤
- Phase 规划

## 验证标准

完成后验证以下内容：
1. `mvn clean compile` 在 backend/ 目录成功
2. 所有 Java 文件无语法错误
3. Spring Boot 启动类能正常启动
4. SecurityConfig 配置了 /api/auth/login 免认证
5. JWT Token 能正常生成和验证

## 注意事项

- **必须基于 SpringReport 的 sys_* 表结构**：保持字段名和 SpringReport 一致，这样可以直接使用 SpringReport 的 SQL 迁移脚本
- **多数据库预留**：JdbcUtils 初期只实现 MySQL，但要预留扩展接口
- **Phase 0 只做骨架**：不实现具体业务逻辑，CRUD 也尽量简单
- **Phase 0 不做前端**：前端在 Phase 1 完成系统管理 CRUD 后再做
