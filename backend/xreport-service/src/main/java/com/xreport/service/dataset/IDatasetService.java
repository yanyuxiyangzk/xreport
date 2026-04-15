package com.xreport.service.dataset;

import com.github.pagehelper.PageInfo;
import com.xreport.pojo.dto.DatasetDTO;
import com.xreport.pojo.entity.reportdataset.ReportDataset;

import java.util.List;
import java.util.Map;

/**
 * 数据集服务接口
 */
public interface IDatasetService {

    /**
     * 分页查询数据集
     */
    PageInfo<ReportDataset> pageQuery(DatasetDTO dto);

    /**
     * 获取数据集详情
     */
    ReportDataset getById(Long id);

    /**
     * 创建数据集
     */
    void add(ReportDataset dataset);

    /**
     * 更新数据集
     */
    void update(ReportDataset dataset);

    /**
     * 删除数据集
     */
    void delete(Long id);

    /**
     * 获取启用的数据集列表
     */
    List<ReportDataset> listEnabled();

    /**
     * 执行数据集查询（支持SQL聚合计算）
     */
    List<Map<String, Object>> executeQuery(Long datasetId, Map<String, Object> params);

    /**
     * 预览数据集查询结果（限制100条）
     */
    List<Map<String, Object>> previewQuery(Long datasetId, Map<String, Object> params);
}