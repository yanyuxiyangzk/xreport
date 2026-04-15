package com.xreport.service.report.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.report.ReportTplSheetMapper;
import com.xreport.pojo.entity.ReportTplSheet;
import com.xreport.service.report.IReportTplSheetService;
import com.xreport.common.util.IdWorker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportTplSheetServiceImpl implements IReportTplSheetService {

    private final ReportTplSheetMapper sheetMapper;

    public ReportTplSheetServiceImpl(ReportTplSheetMapper sheetMapper) {
        this.sheetMapper = sheetMapper;
    }

    @Override
    public List<ReportTplSheet> getByTplId(Long tplId) {
        LambdaQueryWrapper<ReportTplSheet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportTplSheet::getTplId, tplId)
               .eq(ReportTplSheet::getDelFlag, 0)
               .orderByAsc(ReportTplSheet::getSheetOrder);
        return sheetMapper.selectList(wrapper);
    }

    @Override
    public ReportTplSheet getById(Long id) {
        ReportTplSheet sheet = sheetMapper.selectById(id);
        if (sheet == null || sheet.getDelFlag() == 1) {
            throw new BusinessException("Sheet不存在");
        }
        return sheet;
    }

    @Override
    @Transactional
    public void add(ReportTplSheet sheet) {
        sheet.setId(IdWorker.nextId());
        sheet.setDelFlag(0);
        sheet.setTenantId(1L);
        sheet.setCreateTime(LocalDateTime.now());
        sheet.setUpdateTime(LocalDateTime.now());
        sheetMapper.insert(sheet);
    }

    @Override
    @Transactional
    public void update(ReportTplSheet sheet) {
        ReportTplSheet existing = getById(sheet.getId());
        existing.setSheetName(sheet.getSheetName());
        existing.setSheetOrder(sheet.getSheetOrder());
        existing.setIsLoop(sheet.getIsLoop());
        existing.setLoopSettings(sheet.getLoopSettings());
        existing.setDatasourceId(sheet.getDatasourceId());
        existing.setSqlStr(sheet.getSqlStr());
        existing.setParams(sheet.getParams());
        existing.setSort(sheet.getSort());
        existing.setUpdateTime(LocalDateTime.now());
        sheetMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ReportTplSheet sheet = getById(id);
        sheet.setDelFlag(1);
        sheet.setUpdateTime(LocalDateTime.now());
        sheetMapper.updateById(sheet);
    }

    @Override
    @Transactional
    public void deleteByTplId(Long tplId) {
        LambdaQueryWrapper<ReportTplSheet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportTplSheet::getTplId, tplId).eq(ReportTplSheet::getDelFlag, 0);
        List<ReportTplSheet> sheets = sheetMapper.selectList(wrapper);
        for (ReportTplSheet sheet : sheets) {
            sheet.setDelFlag(1);
            sheet.setUpdateTime(LocalDateTime.now());
            sheetMapper.updateById(sheet);
        }
    }
}
