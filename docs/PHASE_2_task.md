# XReport Phase 2 任务：报表设计器核心（Luckysheet 集成 + 模板 CRUD）

## 目标
完成 XReport 后端 Spring Boot 3 报表设计器核心模块，包括：
- 报表模板管理（ReportTpl）
- 报表 Sheet 管理（ReportTplSheet）
- Luckysheet 单元格数据存储（LuckysheetReportCell）
- 报表模板的 CRUD + 列表查询
- Luckysheet JSON 数据的保存/加载 API

## 技术栈
- Spring Boot 3.2.6 + JDK 17
- MyBatis-Plus 3.5.7（Spring Boot 3版）
- Spring Security + JWT（已配置）
- Druid 连接池
- 统一响应 Result<T>
- 编码：UTF-8，无 Lombok

## Phase 1 完成状态
- 用户/角色/部门/菜单 CRUD + JWT 认证 ✅
- BUILD SUCCESS ✅

## 一、数据库表设计（参考 SpringReport 表结构）

### report_tpl 报表模板表
```sql
CREATE TABLE IF NOT EXISTS report_tpl (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
  tpl_name VARCHAR(100) NOT NULL COMMENT '模板名称',
  tpl_type TINYINT DEFAULT 1 COMMENT '模板类型：1=Excel报表,2=Word模板,3=大屏',
  is_example TINYINT DEFAULT 2 COMMENT '是否示例：1=是,2=否',
  search_form_type TINYINT DEFAULT 1 COMMENT '查询组件位置：1=顶部,2=侧边',
  cdn_host VARCHAR(200) COMMENT 'CDN域名',
  water_mark_type TINYINT DEFAULT 0 COMMENT '水印类型',
  permission_type TINYINT DEFAULT 1 COMMENT '权限类型',
  share_token VARCHAR(64) COMMENT '分享Token',
  share_expire_time DATETIME COMMENT '分享过期时间',
  thumbnail VARCHAR(500) COMMENT '缩略图URL',
  status TINYINT DEFAULT 1 COMMENT '状态：0=禁用,1=正常',
  del_flag TINYINT DEFAULT 0 COMMENT '删除标记',
  tenant_id BIGINT DEFAULT 1 COMMENT '租户ID',
  create_user_id BIGINT COMMENT '创建用户ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '报表模板表';
```

### report_tpl_sheet 报表 Sheet 表
```sql
CREATE TABLE IF NOT EXISTS report_tpl_sheet (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Sheet ID',
  tpl_id BIGINT NOT NULL COMMENT '模板ID',
  sheet_name VARCHAR(100) NOT NULL COMMENT 'Sheet名称',
  sheet_order INT DEFAULT 0 COMMENT 'Sheet顺序',
  is_loop TINYINT DEFAULT 2 COMMENT '是否循环：1=是,2=否',
  loop_settings TEXT COMMENT '循环设置JSON',
  datasource_id BIGINT COMMENT '数据源ID',
  sql_str TEXT COMMENT 'SQL查询语句',
  params TEXT COMMENT '参数配置JSON',
  sort INT DEFAULT 0 COMMENT '排序',
  del_flag TINYINT DEFAULT 0 COMMENT '删除标记',
  tenant_id BIGINT DEFAULT 1 COMMENT '租户ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '报表Sheet表';
```

### luckysheet_report_cell 报表单元格数据表
```sql
CREATE TABLE IF NOT EXISTS luckysheet_report_cell (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  tpl_id BIGINT NOT NULL COMMENT '模板ID',
  sheet_id BIGINT NOT NULL COMMENT 'Sheet ID',
  cell_data TEXT NOT NULL COMMENT 'Luckysheet单元格JSON数据',
  ver INT DEFAULT 1 COMMENT '版本号',
  del_flag TINYINT DEFAULT 0 COMMENT '删除标记',
  tenant_id BIGINT DEFAULT 1 COMMENT '租户ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT 'Luckysheet报表单元格数据表';
```

## 二、实体类设计（xreport-pojo 模块）

### ReportTpl.java
```java
package com.xreport.pojo.entity;
// 字段：id(Long), tplName(String), tplType(Integer), isExample(Integer),
//       searchFormType(Integer), cdnHost(String), waterMarkType(Integer),
//       permissionType(Integer), shareToken(String), shareExpireTime(LocalDateTime),
//       thumbnail(String), status(Integer), delFlag(Integer), tenantId(Long),
//       createUserId(Long), createTime(LocalDateTime), updateTime(LocalDateTime)
```

### ReportTplSheet.java
```java
package com.xreport.pojo.entity;
// 字段：id(Long), tplId(Long), sheetName(String), sheetOrder(Integer),
//       isLoop(Integer), loopSettings(String), datasourceId(Long),
//       sqlStr(String), params(String), sort(Integer),
//       delFlag(Integer), tenantId(Long), createTime(LocalDateTime), updateTime(LocalDateTime)
```

### LuckysheetReportCell.java
```java
package com.xreport.pojo.entity;
// 字段：id(Long), tplId(Long), sheetId(Long), cellData(String),
//       ver(Integer), delFlag(Integer), tenantId(Long),
//       createTime(LocalDateTime), updateTime(LocalDateTime)
```

## 三、DTO 设计

### ReportTplDto.java（查询DTO）
```java
package com.xreport.pojo.dto;
public class ReportTplDto {
    private String tplName;
    private Integer tplType;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    // getters/setters
}
```

### LuckysheetSaveRequest.java（保存单元格请求）
```java
package com.xreport.pojo.dto;
public class LuckysheetSaveRequest {
    private Long tplId;
    private Long sheetId;
    private String cellData; // Luckysheet JSON
    // getters/setters
}
```

## 四、Mapper（xreport-mapper 模块）

- ReportTplMapper.java：extends BaseMapper<ReportTpl>
- ReportTplSheetMapper.java：extends BaseMapper<ReportTplSheet>
- LuckysheetReportCellMapper.java：extends BaseMapper<LuckysheetReportCell>

## 五、Service 接口

### IReportTplService.java
```java
package com.xreport.service.report;
import com.github.pagehelper.PageInfo;
import com.xreport.pojo.dto.ReportTplDto;
import com.xreport.pojo.entity.ReportTpl;
import java.util.List;

public interface IReportTplService {
    PageInfo<ReportTpl> pageQuery(ReportTplDto dto);
    List<ReportTpl> list(ReportTplDto dto);
    ReportTpl getById(Long id);
    void add(ReportTpl tpl);
    void update(ReportTpl tpl);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
}
```

### IReportTplSheetService.java
```java
package com.xreport.service.report;
import com.xreport.pojo.entity.ReportTplSheet;
import java.util.List;

public interface IReportTplSheetService {
    List<ReportTplSheet> getByTplId(Long tplId);
    ReportTplSheet getById(Long id);
    void add(ReportTplSheet sheet);
    void update(ReportTplSheet sheet);
    void delete(Long id);
    void deleteByTplId(Long tplId);
}
```

### ILuckysheetReportCellService.java
```java
package com.xreport.service.report;
import com.xreport.pojo.entity.LuckysheetReportCell;

public interface ILuckysheetReportCellService {
    LuckysheetReportCell getBySheetId(Long tplId, Long sheetId);
    void saveCellData(Long tplId, Long sheetId, String cellData);
    void batchSaveCellData(Long tplId, Long sheetId, String cellData);
}
```

## 六、Controller（xreport-web 模块）

### ReportTplController.java
```java
@RestController
@RequestMapping("/api/report/tpl")
public class ReportTplController {
    // GET  /api/report/tpl          - 分页列表
    // GET  /api/report/tpl/{id}    - 详情
    // POST /api/report/tpl          - 新增
    // PUT  /api/report/tpl/{id}    - 修改
    // DELETE /api/report/tpl/{id}  - 删除
    // PUT  /api/report/tpl/{id}/status - 修改状态
}
```

### ReportTplSheetController.java
```java
@RestController
@RequestMapping("/api/report/tpl/{tplId}/sheets")
public class ReportTplSheetController {
    // GET    /api/report/tpl/{tplId}/sheets     - 列表
    // GET    /api/report/tpl/{tplId}/sheets/{id} - 详情
    // POST   /api/report/tpl/{tplId}/sheets     - 新增
    // PUT    /api/report/tpl/{tplId}/sheets/{id} - 修改
    // DELETE /api/report/tpl/{tplId}/sheets/{id} - 删除
}
```

### LuckysheetController.java
```java
@RestController
@RequestMapping("/api/report/luckysheet")
public class LuckysheetController {
    // GET  /api/report/luckysheet/{tplId}/{sheetId} - 加载单元格数据
    // POST /api/report/luckysheet/save - 保存单元格数据
}
```

## 七、SQL 初始化文件（xreport-web/src/main/resources/sql/init/）

### report_tpl.sql
```sql
INSERT INTO report_tpl (id, tpl_name, tpl_type, is_example, status, del_flag, tenant_id, create_user_id) VALUES
(1, '销售报表模板', 1, 1, 1, 0, 1, 1),
(2, '财务报表模板', 1, 2, 1, 0, 1, 1);
```

### report_tpl_sheet.sql
```sql
INSERT INTO report_tpl_sheet (id, tpl_id, sheet_name, sheet_order, is_loop, del_flag, tenant_id) VALUES
(1, 1, 'Sheet1', 1, 2, 0, 1),
(2, 1, 'Sheet2', 2, 2, 0, 1),
(3, 2, '主数据', 1, 2, 0, 1);
```

### luckysheet_report_cell.sql
```sql
-- 初始单元格数据为空（由Luckysheet前端保存）
```

## 八、目录结构

所有新增文件在以下目录创建：
- xreport-pojo: `com.xreport.pojo.entity` + `com.xreport.pojo.dto`
- xreport-mapper: `com.xreport.mapper`
- xreport-service: `com.xreport.service.report` + `com.xreport.service.report.impl`
- xreport-web: `com.xreport.controller.report`

## 九、注意事项

1. **Luckysheet JSON 数据**：cellData 字段存储完整的 Luckysheet JSON，包含所有单元格数据、格式、合并信息
2. **一个 Sheet 一条记录**：每个 sheet 的 Luckysheet 数据存储为一条 record
3. **版本控制**：ver 字段用于乐观锁，每次保存+1
4. **权限控制**：创建人才能修改模板（后续 Phase 8 实现）
5. **删除处理**：先删 Sheet 再删 Tpl，或者级联删除
6. **Excel 导出**：Phase 5 才实现，这里只存储 Luckysheet JSON

## 十、执行步骤

1. 创建实体类（3个：ReportTpl, ReportTplSheet, LuckysheetReportCell）
2. 创建 DTO 类（2个：ReportTplDto, LuckysheetSaveRequest）
3. 创建 Mapper 接口（3个）
4. 创建 Service 接口（3个）
5. 创建 Service 实现（3个，注意事务管理）
6. 创建 Controller（3个）
7. 创建 SQL 初始化文件（3个）
8. 运行 `mvn clean compile` 验证编译
9. 运行单元测试（如果有）

## 十一、成功标准

- [ ] mvn clean compile BUILD SUCCESS
- [ ] 所有新增 Controller 路径正确映射
- [ ] Service 实现包含 @Transactional
- [ ] SQL 初始化文件格式正确
- [ ] 知识库已更新（corrections/success）
