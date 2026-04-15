# XReport 运维监控指南

## 目录
- [监控指标说明](#监控指标说明)
- [日志查看方法](#日志查看方法)
- [性能调优建议](#性能调优建议)
- [备份策略](#备份策略)

---

## 监控指标说明

### 应用健康检查

XReport 提供以下健康检查端点：

```bash
# Spring Boot Actuator 健康检查
curl http://localhost:8080/actuator/health

# 自定义健康检查接口
curl http://localhost:8080/api/auth/health
```

### Docker 容器状态

```bash
# 查看所有容器状态
docker-compose ps

# 查看容器资源使用
docker stats

# 查看特定容器资源使用
docker stats xreport
```

### 关键监控指标

| 指标 | 正常范围 | 告警阈值 | 说明 |
|------|---------|---------|------|
| CPU 使用率 | < 70% | > 85% | 应用容器 CPU |
| 内存使用率 | < 80% | > 90% | 应用容器内存 |
| JVM 堆内存 | < 85% | > 95% | Java 堆内存 |
| MySQL 连接数 | < 80% | > 90% | 数据库连接池 |
| 响应时间 | < 500ms | > 2000ms | API 响应时间 |
| 磁盘使用率 | < 70% | > 85% | 数据卷磁盘 |

### JVM 监控

查看 JVM 状态：
```bash
# 进入容器
docker-compose exec xreport sh

# 查看 Java 进程
jps -l

# 查看 JVM 内存
jstat -gc <pid>

# 查看 JVM 参数
jinfo -flags <pid>
```

---

## 日志查看方法

### Docker 日志

```bash
# 查看所有服务日志（实时）
docker-compose logs -f

# 只看 XReport 日志
docker-compose logs -f xreport

# 只看 MySQL 日志
docker-compose logs -f mysql

# 查看最近 100 行日志
docker-compose logs --tail 100 xreport

# 导出日志到文件
docker-compose logs xreport > xreport.log
```

### 应用日志级别

默认日志配置（application-docker.yml）：
```yaml
logging:
  level:
    root: INFO
    com.xreport: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 调整日志级别

临时调整日志级别：
```bash
# 进入容器
docker-compose exec xreport sh

# 使用 kill 命令发送信号（需要开启 JMX）
# 或修改 application-docker.yml 后重启
docker-compose restart xreport
```

### MySQL 日志

```bash
# 查看 MySQL 错误日志
docker-compose logs mysql | grep ERROR

# 进入 MySQL 查看慢查询日志
docker-compose exec mysql mysql -u root -p
```

---

## 性能调优建议

### JVM 参数调优

根据服务器内存调整 JVM 参数：

```yaml
# 在 docker-compose.yml 中
environment:
  - JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC
```

| 服务器内存 | -Xms | -Xmx | 适用场景 |
|-----------|------|------|---------|
| 4GB | 512m | 1024m | 开发/测试 |
| 8GB | 1024m | 2048m | 小规模生产 |
| 16GB+ | 2048m | 4096m | 中大规模生产 |

### Druid 连接池调优

修改 `application-docker.yml`：
```yaml
spring:
  datasource:
    druid:
      initial-size: 5        # 初始连接数
      min-idle: 5           # 最小空闲连接
      max-active: 20        # 最大活跃连接
      max-wait: 60000       # 最大等待时间(ms)
```

### MySQL 调优

```sql
-- 查看当前连接数
SHOW STATUS LIKE 'Threads_connected';

-- 查看最大连接数
SHOW VARIABLES LIKE 'max_connections';

-- 查看慢查询
SHOW VARIABLES LIKE 'slow_query_log';
```

### Docker 资源限制

在 docker-compose.yml 中添加资源限制：
```yaml
xreport:
  # ...
  deploy:
    resources:
      limits:
        cpus: '2.0'
        memory: 2G
      reservations:
        cpus: '1.0'
        memory: 1G
```

---

## 备份策略

### 数据库备份

#### 自动备份脚本

创建 `scripts/backup.sh`：
```bash
#!/bin/bash
BACKUP_DIR="./backups"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

# 备份 MySQL
docker-compose exec -T mysql mysqldump -u root -p$MYSQL_ROOT_PASSWORD xreport > $BACKUP_DIR/xreport_$DATE.sql

# 保留最近 30 天的备份
find $BACKUP_DIR -name "*.sql" -mtime +30 -delete

echo "Backup completed: xreport_$DATE.sql"
```

#### 手动备份

```bash
# 备份数据库
docker-compose exec -T mysql mysqldump -u root -proot123 xreport > backup_$(date +%Y%m%d).sql

# 恢复数据库
docker-compose exec -T mysql mysql -u root -proot123 xreport < backup_20260415.sql
```

### 文件备份

```bash
# 备份上传文件（如果存在）
tar -czf files_backup_$(date +%Y%m%d).tar.gz /path/to/uploads

# 备份配置
cp -r config config_backup_$(date +%Y%m%d)
```

### 镜像备份

```bash
# 导出镜像
docker save xreport:latest | gzip > xreport_backup.tar.gz

# 导入镜像
docker load < xreport_backup.tar.gz
```

### 定时备份 (Cron)

```bash
# 编辑 crontab
crontab -e

# 每天凌晨 2 点执行备份
0 2 * * * /path/to/scripts/backup.sh >> /var/log/xreport_backup.log 2>&1
```

---

## 告警配置

### Docker Health Check

docker-compose 已配置健康检查：
```yaml
healthcheck:
  test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
  interval: 10s
  timeout: 5s
  retries: 5
```

### 常用监控命令

```bash
# 检查服务健康状态
docker-compose ps

# 检查容器健康状态
docker inspect --format='{{.State.Health.Status}}' xreport_xreport_1

# 检查资源使用
docker stats --no-stream

# 检查日志中的错误
docker-compose logs --since 1h | grep -i error
```

---

*最后更新: 2026-04-15*