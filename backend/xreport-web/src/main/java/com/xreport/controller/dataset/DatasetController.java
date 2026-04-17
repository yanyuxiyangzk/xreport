package com.xreport.controller.dataset;

import com.github.pagehelper.PageInfo;
import com.xreport.common.result.Result;
import com.xreport.pojo.dto.DatasetDTO;
import com.xreport.pojo.entity.reportdataset.ReportDataset;
import com.xreport.service.dataset.IDatasetService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据集管理控制器
 * 提供数据集的CRUD操作和SQL查询执行功能
 */
@RestController
@RequestMapping("/api/datasets")
@PreAuthorize("hasAuthority('report:manage')")
public class DatasetController {

    private final IDatasetService datasetService;

    public DatasetController(IDatasetService datasetService) {
        this.datasetService = datasetService;
    }

    /**
     * 分页查询数据集
     */
    @GetMapping
    public Result<PageInfo<ReportDataset>> pageList(DatasetDTO dto) {
        return Result.ok(datasetService.pageQuery(dto));
    }

    /**
     * 获取数据集详情
     */
    @GetMapping("/{id}")
    public Result<ReportDataset> getById(@PathVariable Long id) {
        return Result.ok(datasetService.getById(id));
    }

    /**
     * 创建数据集
     */
    @PostMapping
    public Result<Void> add(@RequestBody ReportDataset dataset) {
        // 设置创建用户ID（从JWT获取）
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        dataset.setCreateUserId(userId);
        datasetService.add(dataset);
        return Result.ok();
    }

    /**
     * 更新数据集
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ReportDataset dataset) {
        dataset.setId(id);
        datasetService.update(dataset);
        return Result.ok();
    }

    /**
     * 删除数据集
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        datasetService.delete(id);
        return Result.ok();
    }

    /**
     * 获取启用的数据集列表
     */
    @GetMapping("/enabled")
    public Result<List<ReportDataset>> listEnabled() {
        return Result.ok(datasetService.listEnabled());
    }

    /**
     * 执行数据集查询（支持SQL聚合计算）
     */
    @PostMapping("/query")
    public Result<List<Map<String, Object>>> executeQuery(@RequestBody Map<String, Object> params) {
        Long datasetId = Long.parseLong(String.valueOf(params.get("datasetId")));
        @SuppressWarnings("unchecked")
        Map<String, Object> queryParams = (Map<String, Object>) params.get("params");
        List<Map<String, Object>> result = datasetService.executeQuery(datasetId, queryParams);
        return Result.ok(result);
    }

    /**
     * 预览数据集查询结果（限制100条）
     */
    @PostMapping("/preview")
    public Result<List<Map<String, Object>>> previewQuery(@RequestBody Map<String, Object> params) {
        Long datasetId = Long.parseLong(String.valueOf(params.get("datasetId")));
        @SuppressWarnings("unchecked")
        Map<String, Object> queryParams = (Map<String, Object>) params.get("params");
        List<Map<String, Object>> result = datasetService.previewQuery(datasetId, queryParams);
        return Result.ok(result);
    }
}