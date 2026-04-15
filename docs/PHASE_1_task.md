# XReport Phase 1 任务：系统管理（用户/角色/部门/菜单 + JWT认证）

## 目标
完成 XReport 后端 Spring Boot 3 系统的管理模块，包括：
- 用户管理（SysUser）
- 角色管理（SysRole）
- 部门管理（SysDept）
- 菜单管理（SysMenu）
- 用户-角色关联（SysUserRole）
- 角色-菜单关联（SysRoleMenu）
- JWT 认证（登录/Token刷新）
- Spring Security 权限控制

## 技术栈
- Spring Boot 3.2.6 + JDK 17
- MyBatis-Plus 3.5.7（Spring Boot 3版）
- Spring Security + JWT（jjwt 0.12.6）
- Druid 连接池
- 统一响应 Result<T>

## 编码规范
- 所有 Java 文件：UTF-8，无 Lombok（手写getter/setter/构造器）
- 所有中文注释：UTF-8 编码
- 包名：com.xreport
- 模块划分：pojo/dto（数据传对象）/entity（实体）/mapper/ service/impl/controller

---

## 一、数据库表设计（参考 SpringReport sys_* 表）

### sys_user 用户表
```sql
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(200) NOT NULL COMMENT '密码(Bcrypt)',
  nickname VARCHAR(50) COMMENT '昵称',
  email VARCHAR(100) COMMENT '邮箱',
  phone VARCHAR(20) COMMENT '手机号',
  avatar VARCHAR(500) COMMENT '头像URL',
  status TINYINT DEFAULT 1 COMMENT '状态(0禁用1正常)',
  del_flag TINYINT DEFAULT 0 COMMENT '删除标记(0正常1删除)',
  tenant_id BIGINT DEFAULT 1 COMMENT '租户ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';
```

### sys_role 角色表
```sql
CREATE TABLE sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
  role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
  role_key VARCHAR(100) NOT NULL COMMENT '角色标识',
  role_sort INT DEFAULT 0 COMMENT '显示顺序',
  status TINYINT DEFAULT 1 COMMENT '状态(0禁用1正常)',
  del_flag TINYINT DEFAULT 0 COMMENT '删除标记',
  tenant_id BIGINT DEFAULT 1 COMMENT '租户ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_role_key (role_key)
) COMMENT '角色表';
```

### sys_dept 部门表
```sql
CREATE TABLE sys_dept (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
  parent_id BIGINT DEFAULT 0 COMMENT '父部门ID(0为根)',
  ancestors VARCHAR(500) COMMENT '祖级列表(逗号分隔)',
  dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
  dept_code VARCHAR(50) COMMENT '部门编码',
  sort_order INT DEFAULT 0 COMMENT '排序',
  leader_user_id BIGINT COMMENT '负责人用户ID',
  phone VARCHAR(20) COMMENT '联系电话',
  email VARCHAR(100) COMMENT '邮箱',
  status TINYINT DEFAULT 1 COMMENT '状态(0禁用1正常)',
  del_flag TINYINT DEFAULT 0 COMMENT '删除标记',
  tenant_id BIGINT DEFAULT 1 COMMENT '租户ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '部门表';
```

### sys_menu 菜单表
```sql
CREATE TABLE sys_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
  parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
  menu_type CHAR(1) DEFAULT 'C' COMMENT '菜单类型(M目录/C菜单/B按钮)',
  menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
  path VARCHAR(200) COMMENT '路由地址',
  component VARCHAR(255) COMMENT '组件路径',
  menu_icon VARCHAR(100) COMMENT '菜单图标',
  is_frame INT DEFAULT 1 COMMENT '是否外链(0是1否)',
  is_cache INT DEFAULT 0 COMMENT '是否缓存(0缓存1不缓存)',
  sort_order INT DEFAULT 0 COMMENT '显示顺序',
  visible CHAR(1) DEFAULT '0' COMMENT '菜单状态(0显示1隐藏)',
  perms VARCHAR(100) COMMENT '权限标识',
  icon VARCHAR(100) COMMENT '图标',
  status CHAR(1) DEFAULT '0' COMMENT '状态(0正常1停用)',
  del_flag TINYINT DEFAULT 0 COMMENT '删除标记',
  tenant_id BIGINT DEFAULT 1 COMMENT '租户ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '菜单表';
```

### sys_user_role 用户角色关联表
```sql
CREATE TABLE sys_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  tenant_id BIGINT DEFAULT 1 COMMENT '租户ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '用户角色关联表';
```

### sys_role_menu 角色菜单关联表
```sql
CREATE TABLE sys_role_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  menu_id BIGINT NOT NULL COMMENT '菜单ID',
  tenant_id BIGINT DEFAULT 1 COMMENT '租户ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '角色菜单关联表';
```

---

## 二、Pojo/Dto 设计

### UserLoginRequest（登录请求）
```java
public class UserLoginRequest {
    private String username;   // required
    private String password;  // required
    private String captcha;   // optional, 验证码
    private String captchaKey; // optional, 验证码key
}
```

### UserLoginResponse（登录响应）
```java
public class UserLoginResponse {
    private String token;
    private String username;
    private String nickname;
    private Long userId;
    private Long expireTime; // 过期时间戳
}
```

### SysUserDto（用户查询DTO）
```java
public class SysUserDto {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private Integer status;
    private Long deptId;
    private String deptName;
    private List<Long> roleIds;
    private String createTime;
    private Integer pageNum;
    private Integer pageSize;
}
```

---

## 三、核心接口设计

### 认证接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/logout | 登出 |
| GET  | /api/auth/userinfo | 获取当前用户信息 |

### 用户管理接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET    | /api/system/users | 分页查询用户 |
| GET    | /api/system/users/{id} | 获取用户详情 |
| POST   | /api/system/users | 新增用户 |
| PUT    | /api/system/users/{id} | 修改用户 |
| DELETE | /api/system/users/{id} | 删除用户 |
| PUT    | /api/system/users/{id}/reset-pwd | 重置密码 |
| PUT    | /api/system/users/{id}/status | 修改状态 |
| PUT    | /api/system/users/{id}/roles | 分配角色 |

### 角色管理接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET    | /api/system/roles | 分页查询角色 |
| GET    | /api/system/roles/{id} | 获取角色详情 |
| POST   | /api/system/roles | 新增角色 |
| PUT    | /api/system/roles/{id} | 修改角色 |
| DELETE | /api/system/roles/{id} | 删除角色 |
| GET    | /api/system/roles/{id}/menus | 获取角色菜单ID列表 |
| PUT    | /api/system/roles/{id}/menus | 分配菜单权限 |

### 部门管理接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET    | /api/system/depts | 获取部门树 |
| GET    | /api/system/depts/{id} | 获取部门详情 |
| POST   | /api/system/depts | 新增部门 |
| PUT    | /api/system/depts/{id} | 修改部门 |
| DELETE | /api/system/depts/{id} | 删除部门 |

### 菜单管理接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET    | /api/system/menus | 获取菜单树 |
| GET    | /api/system/menus/{id} | 获取菜单详情 |
| POST   | /api/system/menus | 新增菜单 |
| PUT    | /api/system/menus/{id} | 修改菜单 |
| DELETE | /api/system/menus/{id} | 删除菜单 |

---

## 四、JWT 实现要点

### Token 结构
- Header: `{ "alg": "HS512", "typ": "JWT" }`
- Payload: `{ "sub": userId, "username": xxx, "exp": expireTime, "iat": issuedAt }`
- 签名密钥：从配置文件读取 `jwt.secret`
- 过期时间：从配置文件读取 `jwt.expire-time`（默认86400秒=24小时）

### 工具类 JwtUtils
```java
public class JwtUtils {
    // 生成Token
    public static String generateToken(Long userId, String username, String secret, long expireTime)
    // 验证Token并返回userId
    public static Long getUserIdFromToken(String token, String secret)
    // 判断Token是否过期
    public static boolean isTokenExpired(String token, String secret)
}
```

### Spring Security 配置要点
- 配置 `SecurityFilterChain`，放行 `/api/auth/**`
- JWT 认证过滤器：从 Header `Authorization: Bearer xxx` 提取 Token 并验证
- 验证通过后，将用户信息存入 `SecurityContextHolder`
- 所有接口默认需要认证

### 密码加密
- 使用 Spring Security 的 `BCryptPasswordEncoder`
- 注册时：BCrypt 加密
- 登录时：BCrypt 匹配验证

---

## 五、Controller 层实现要点

### 统一分页查询模式
```java
// GET /api/system/users?pageNum=1&pageSize=10&username=xxx&status=1
public Result<PageResult<SysUserDto>> list(SysUserDto dto)
// 使用 MyBatis-Plus QueryWrapper 构建查询条件
```

### 树形结构查询（部门/菜单）
```java
// GET /api/system/depts - 返回树形结构
public Result<List<SysDeptDto>> getDeptTree()
// 使用 parentId 递归构建树
```

---

## 六、文件清单（需要创建的文件）

### xreport-pojo 模块
```
com.xreport.pojo.entity.SysUser
com.xreport.pojo.entity.SysRole
com.xreport.pojo.entity.SysDept
com.xreport.pojo.entity.SysMenu
com.xreport.pojo.entity.SysUserRole
com.xreport.pojo.entity.SysRoleMenu
com.xreport.pojo.dto.UserLoginRequest
com.xreport.pojo.dto.UserLoginResponse
com.xreport.pojo.dto.SysUserDto
com.xreport.pojo.dto.SysRoleDto
com.xreport.pojo.dto.SysDeptDto
com.xreport.pojo.dto.SysMenuDto
com.xreport.pojo.vo.LoginUserVo
```

### xreport-mapper 模块
```
com.xreport.mapper.SysUserMapper + SysUserMapper.xml
com.xreport.mapper.SysRoleMapper + SysRoleMapper.xml
com.xreport.mapper.SysDeptMapper + SysDeptMapper.xml
com.xreport.mapper.SysMenuMapper + SysMenuMapper.xml
com.xreport.mapper.SysUserRoleMapper
com.xreport.mapper.SysRoleMenuMapper
```

### xreport-service 模块
```
com.xreport.service.system.ISysUserService
com.xreport.service.system.impl.SysUserServiceImpl
com.xreport.service.system.ISysRoleService
com.xreport.service.system.impl.SysRoleServiceImpl
com.xreport.service.system.ISysDeptService
com.xreport.service.system.impl.SysDeptServiceImpl
com.xreport.service.system.ISysMenuService
com.xreport.service.system.impl.SysMenuServiceImpl
com.xreport.service.auth.IAuthService
com.xreport.service.auth.impl.AuthServiceImpl
```

### xreport-web 模块
```
com.xreport.config.SecurityConfig
com.xreport.config.JwtAuthenticationFilter
com.xreport.controller.auth.AuthController
com.xreport.controller.system.SysUserController
com.xreport.controller.system.SysRoleController
com.xreport.controller.system.SysDeptController
com.xreport.controller.system.SysMenuController
com.xreport.util.JwtUtils
com.xreport.util.PasswordEncoder (BCrypt wrapper)
```

### resources
```
sql/init/sys_user.sql        (初始用户数据：admin/admin123)
sql/init/sys_role.sql
sql/init/sys_dept.sql
sql/init/sys_menu.sql
application.yml              (完整配置，含JWT/数据库配置)
```

---

## 七、SecurityConfig 要点
- BCryptPasswordEncoder Bean
- SecurityFilterChain：放行 `/api/auth/**`，其他全部需要认证
- `AuthenticationManager` Bean
- CORS 配置：允许所有来源（开发环境）

## 八、密码要求
- BCrypt 加密，强度 10
- 初始管理员：admin / admin123

---

## 执行要求
1. 在 `D:\project\aicoding\XReport\backend` 目录下执行
2. 所有代码使用 UTF-8 编码
3. 不要使用 Lombok，手写 getter/setter/constructor
4. 代码要完整可运行，不能有 TODO
5. 生成完成后执行 `mvn clean compile -f D:\project\aicoding\XReport\backend\pom.xml` 验证编译
6. 所有新增文件路径都记录下来供我确认
