# XReport 部署指南

## 目录
- [部署前准备](#部署前准备)
- [快速部署](#快速部署)
- [Docker 部署详解](#docker-部署详解)
- [docker-compose 详解](#docker-compose-详解)
- [常见问题解答](#常见问题解答)
- [回滚步骤](#回滚步骤)

---

## 部署前准备

### 环境要求

| 要求 | 最低版本 | 推荐版本 |
|------|---------|---------|
| Docker | 20.10+ | 24.0+ |
| docker-compose | 2.0+ | 2.20+ |
| 内存 | 4GB | 8GB+ |
| 磁盘 | 10GB | 20GB+ |

### 检查 Docker 安装

```bash
# Linux/macOS
docker --version
docker-compose --version

# Windows
docker --version
docker-compose --version
```

### 端口检查

确保以下端口未被占用：
- `8080` - XReport 后端服务
- `3306` - MySQL 数据库

---

## 快速部署

### Linux/macOS

```bash
cd backend
chmod +x scripts/deploy.sh
./scripts/deploy.sh
```

### Windows

```cmd
cd backend
scripts\deploy.bat
```

### 验证部署

部署成功后访问：
- XReport: http://localhost:8080
- 默认账号: `admin` / `admin123`

---

## Docker 部署详解

### 构建镜像

```bash
# 构建 XReport 镜像
docker build -t xreport:latest .

# 查看镜像
docker images | grep xreport
```

### 运行容器

```bash
# 运行 XReport（使用外部 MySQL）
docker run -d \
  --name xreport \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e MYSQL_HOST=mysql \
  -e MYSQL_PORT=3306 \
  -e MYSQL_DATABASE=xreport \
  -e MYSQL_USERNAME=xreport \
  -e MYSQL_PASSWORD=xreport123 \
  xreport:latest
```

### Dockerfile 多阶段构建

```dockerfile
# 第一阶段：构建
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# 第二阶段：运行
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Xms512m", "-Xmx1024m", "/app/app.jar"]
```

---

## docker-compose 详解

### 启动服务

```bash
# 前台启动（查看日志）
docker-compose up

# 后台启动
docker-compose up -d

# 查看状态
docker-compose ps
```

### 服务组件

| 服务 | 端口 | 说明 |
|------|------|------|
| xreport | 8080 | Spring Boot 应用 |
| mysql | 3306 | MySQL 8.0 数据库 |

### 数据卷

```yaml
volumes:
  mysql_data:  # MySQL 数据持久化
```

### 健康检查

MySQL 健康检查配置：
```yaml
healthcheck:
  test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
  interval: 10s
  timeout: 5s
  retries: 5
```

### 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| SPRING_PROFILES_ACTIVE | docker | Spring Profile |
| MYSQL_HOST | mysql | MySQL 主机 |
| MYSQL_PORT | 3306 | MySQL 端口 |
| MYSQL_DATABASE | xreport | 数据库名 |
| MYSQL_USERNAME | xreport | 数据库用户名 |
| MYSQL_PASSWORD | xreport123 | 数据库密码 |

---

## 常见问题解答

### Q1: Docker 构建失败

**问题**: `mvn package` 失败

**解决**:
```bash
# 清理 Maven 缓存后重试
docker builder prune
docker-compose build --no-cache
```

### Q2: MySQL 连接超时

**问题**: XReport 启动时报 `Communications link failure`

**解决**:
```bash
# 检查 MySQL 是否正常运行
docker-compose logs mysql

# 手动连接测试
docker-compose exec mysql mysql -u xreport -p

# 重启 MySQL
docker-compose restart mysql
```

### Q3: 端口被占用

**问题**: `Bind for 0.0.0.0:8080 failed: port is already allocated`

**解决**:
```bash
# 查找占用端口的进程
# Linux/macOS
lsof -i :8080

# Windows
netstat -ano | findstr :8080

# 停止占用进程或修改 docker-compose.yml 中的端口映射
```

### Q4: 容器内存不足

**问题**: Java 堆内存溢出 `OutOfMemoryError`

**解决**: 调整 JVM 内存参数：
```yaml
# 在 docker-compose.yml 中添加
environment:
  - JAVA_OPTS=-Xms512m -Xmx1024m
```

### Q5: 如何查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 只看 XReport 日志
docker-compose logs -f xreport

# 只看 MySQL 日志
docker-compose logs -f mysql

# 导出日志到文件
docker-compose logs > xreport.log
```

### Q6: 如何进入容器调试

```bash
# 进入 XReport 容器
docker-compose exec xreport sh

# 进入 MySQL 容器
docker-compose exec mysql mysql -u root -p
```

---

## 回滚步骤

### 方案一：使用之前版本的镜像

```bash
# 1. 停止当前服务
docker-compose down

# 2. 拉取之前版本的镜像
docker pull xreport:previous-version

# 3. 修改 docker-compose.yml 中的镜像版本
#    image: xreport:previous-version

# 4. 重新启动
docker-compose up -d
```

### 方案二：从头重建

```bash
# 1. 停止并删除服务
docker-compose down -v

# 2. 删除镜像
docker rmi xreport:latest

# 3. 重新构建
docker-compose build --no-cache

# 4. 启动服务
docker-compose up -d
```

### 方案三：数据回滚（MySQL）

```bash
# 1. 备份当前数据
docker-compose exec mysql mysqldump -u root -p xreport > backup.sql

# 2. 恢复数据
docker-compose exec -T mysql mysql -u root -p xreport < backup.sql
```

---

## 其他命令

```bash
# 停止服务（保留数据）
docker-compose down

# 停止服务并删除数据
docker-compose down -v

# 重启服务
docker-compose restart

# 重新构建（不缓存）
docker-compose build --no-cache

# 进入容器
docker-compose exec xreport sh
```

---

*最后更新: 2026-04-15*