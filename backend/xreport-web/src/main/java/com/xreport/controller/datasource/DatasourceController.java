package com.xreport.controller.datasource;

import com.github.pagehelper.PageInfo;
import com.xreport.common.result.Result;
import com.xreport.pojo.dto.DatasourceDTO;
import com.xreport.pojo.entity.reportdatasource.ReportDatasource;
import com.xreport.service.report.IReportDatasourceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据源管理控制器
 */
@RestController
@RequestMapping("/api/datasources")
@PreAuthorize("hasAuthority('datasource:manage')")
public class DatasourceController {

    private final IReportDatasourceService datasourceService;

    public DatasourceController(IReportDatasourceService datasourceService) {
        this.datasourceService = datasourceService;
    }

    /**
     * 分页查询数据源
     */
    @GetMapping
    public Result<PageInfo<ReportDatasource>> pageList(DatasourceDTO dto) {
        return Result.ok(datasourceService.pageQuery(dto));
    }

    /**
     * 获取数据源详情
     */
    @GetMapping("/{id}")
    public Result<ReportDatasource> getById(@PathVariable Long id) {
        return Result.ok(datasourceService.getById(id));
    }

    /**
     * 创建数据源
     */
    @PostMapping
    public Result<Void> add(@RequestBody ReportDatasource datasource) {
        datasourceService.add(datasource);
        return Result.ok();
    }

    /**
     * 更新数据源
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ReportDatasource datasource) {
        datasource.setId(id);
        datasourceService.update(datasource);
        return Result.ok();
    }

    /**
     * 删除数据源
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        datasourceService.delete(id);
        return Result.ok();
    }

    /**
     * 获取启用的数据源列表
     */
    @GetMapping("/enabled")
    public Result<List<ReportDatasource>> listEnabled() {
        return Result.ok(datasourceService.listEnabled());
    }

    /**
     * 测试JDBC连接
     */
    @PostMapping("/test/jdbc")
    public Result<Map<String, Object>> testJdbcConnection(@RequestBody Map<String, String> params) {
        String jdbcUrl = params.get("jdbcUrl");
        String username = params.get("username");
        String password = params.get("password");
        boolean success = datasourceService.testJdbcConnection(jdbcUrl, username, password);
        if (success) {
            return Result.ok(Map.of("success", true, "message", "JDBC连接成功"));
        } else {
            return Result.fail("JDBC连接失败");
        }
    }

    /**
     * 测试API连接
     */
    @PostMapping("/test/api")
    public Result<Map<String, Object>> testApiConnection(@RequestBody Map<String, Object> params) {
        String apiUrl = (String) params.get("apiUrl");
        String method = (String) params.get("method");
        @SuppressWarnings("unchecked")
        Map<String, String> headers = (Map<String, String>) params.get("headers");
        String body = (String) params.get("body");
        Map<String, Object> result = datasourceService.testApiConnection(apiUrl, method, headers, body);
        return Result.ok(result);
    }

    /**
     * 执行JDBC查询
     */
    @PostMapping("/query/jdbc")
    public Result<List<Map<String, Object>>> executeJdbcQuery(@RequestBody Map<String, String> params) {
        Long datasourceId = Long.parseLong(params.get("datasourceId"));
        String sql = params.get("sql");
        List<Map<String, Object>> result = datasourceService.executeJdbcQuery(datasourceId, sql);
        return Result.ok(result);
    }

    /**
     * 执行API查询
     */
    @PostMapping("/query/api")
    public Result<Map<String, Object>> executeApiQuery(@RequestBody Map<String, Object> params) {
        Long datasourceId = Long.parseLong(params.get("datasourceId").toString());
        String apiUrl = (String) params.get("apiUrl");
        String method = (String) params.get("method");
        @SuppressWarnings("unchecked")
        Map<String, String> headers = (Map<String, String>) params.get("headers");
        String body = (String) params.get("body");
        Map<String, Object> result = datasourceService.executeApiQuery(datasourceId, apiUrl, method, headers, body);
        return Result.ok(result);
    }
}