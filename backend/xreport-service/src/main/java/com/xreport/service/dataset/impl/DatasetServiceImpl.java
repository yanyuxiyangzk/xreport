package com.xreport.service.dataset.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xreport.common.exception.BusinessException;
import com.xreport.common.util.IdWorker;
import com.xreport.mapper.report.ReportDatasetMapper;
import com.xreport.pojo.dto.DatasetDTO;
import com.xreport.pojo.entity.reportdatasource.ReportDatasource;
import com.xreport.pojo.entity.reportdataset.ReportDataset;
import com.xreport.service.dataset.IDatasetQueryEngine;
import com.xreport.service.dataset.IDatasetService;
import com.xreport.service.report.IReportDatasourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据集服务实现
 */
@Service
public class DatasetServiceImpl implements IDatasetService {

    private static final Logger log = LoggerFactory.getLogger(DatasetServiceImpl.class);

    private final ReportDatasetMapper datasetMapper;
    private final IReportDatasourceService datasourceService;
    private final IDatasetQueryEngine queryEngine;

    @Autowired
    public DatasetServiceImpl(ReportDatasetMapper datasetMapper,
                              IReportDatasourceService datasourceService,
                              IDatasetQueryEngine queryEngine) {
        this.datasetMapper = datasetMapper;
        this.datasourceService = datasourceService;
        this.queryEngine = queryEngine;
    }

    @Override
    public PageInfo<ReportDataset> pageQuery(DatasetDTO dto) {
        PageHelper.startPage(dto.getPageNum() != null ? dto.getPageNum() : 1,
                             dto.getPageSize() != null ? dto.getPageSize() : 10);
        LambdaQueryWrapper<ReportDataset> wrapper = new LambdaQueryWrapper<>();
        if (dto.getDatasetName() != null && !dto.getDatasetName().isEmpty()) {
            wrapper.like(ReportDataset::getDatasetName, dto.getDatasetName());
        }
        if (dto.getDatasetCode() != null && !dto.getDatasetCode().isEmpty()) {
            wrapper.eq(ReportDataset::getDatasetCode, dto.getDatasetCode());
        }
        if (dto.getDatasourceId() != null) {
            wrapper.eq(ReportDataset::getDatasourceId, dto.getDatasourceId());
        }
        if (dto.getDatasetType() != null && !dto.getDatasetType().isEmpty()) {
            wrapper.eq(ReportDataset::getDatasetType, dto.getDatasetType());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(ReportDataset::getStatus, dto.getStatus());
        }
        wrapper.eq(ReportDataset::getDelFlag, 0).orderByDesc(ReportDataset::getCreateTime);
        List<ReportDataset> list = datasetMapper.selectList(wrapper);
        return new PageInfo<>(list);
    }

    @Override
    public ReportDataset getById(Long id) {
        ReportDataset dataset = datasetMapper.selectById(id);
        if (dataset == null || dataset.getDelFlag() == 1) {
            throw new BusinessException("数据集不存在");
        }
        return dataset;
    }

    @Override
    @Transactional
    public void add(ReportDataset dataset) {
        dataset.setId(IdWorker.nextId());
        dataset.setDelFlag(0);
        dataset.setStatus(1);
        dataset.setTenantId(1L);
        dataset.setCreateTime(LocalDateTime.now());
        dataset.setUpdateTime(LocalDateTime.now());
        datasetMapper.insert(dataset);
    }

    @Override
    @Transactional
    public void update(ReportDataset dataset) {
        ReportDataset existing = getById(dataset.getId());
        existing.setDatasetName(dataset.getDatasetName());
        existing.setDatasetCode(dataset.getDatasetCode());
        existing.setDatasetDesc(dataset.getDatasetDesc());
        existing.setDatasourceId(dataset.getDatasourceId());
        existing.setSqlStatement(dataset.getSqlStatement());
        existing.setDatasetType(dataset.getDatasetType());
        existing.setUpdateTime(LocalDateTime.now());
        datasetMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ReportDataset dataset = getById(id);
        dataset.setDelFlag(1);
        dataset.setUpdateTime(LocalDateTime.now());
        datasetMapper.updateById(dataset);
    }

    @Override
    public List<ReportDataset> listEnabled() {
        LambdaQueryWrapper<ReportDataset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportDataset::getDelFlag, 0)
               .eq(ReportDataset::getStatus, 1)
               .orderByDesc(ReportDataset::getCreateTime);
        return datasetMapper.selectList(wrapper);
    }

    @Override
    public List<Map<String, Object>> executeQuery(Long datasetId, Map<String, Object> params) {
        ReportDataset dataset = getById(datasetId);
        ReportDatasource datasource = datasourceService.getById(dataset.getDatasourceId());
        return queryEngine.execute(datasource, dataset.getSqlStatement(), params);
    }

    @Override
    public List<Map<String, Object>> previewQuery(Long datasetId, Map<String, Object> params) {
        ReportDataset dataset = getById(datasetId);
        ReportDatasource datasource = datasourceService.getById(dataset.getDatasourceId());

        // 预览限制100条
        Map<String, Object> previewParams = new HashMap<>(params != null ? params : new HashMap<>());
        previewParams.put("limit", 100);

        return queryEngine.execute(datasource, dataset.getSqlStatement(), previewParams);
    }
}