package com.xreport.service.report.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.mapper.report.LuckysheetReportCellMapper;
import com.xreport.pojo.entity.LuckysheetReportCell;
import com.xreport.service.report.ILuckysheetReportCellService;
import com.xreport.common.util.IdWorker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LuckysheetReportCellServiceImpl implements ILuckysheetReportCellService {

    private final LuckysheetReportCellMapper cellMapper;

    public LuckysheetReportCellServiceImpl(LuckysheetReportCellMapper cellMapper) {
        this.cellMapper = cellMapper;
    }

    @Override
    public LuckysheetReportCell getBySheetId(Long tplId, Long sheetId) {
        LambdaQueryWrapper<LuckysheetReportCell> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LuckysheetReportCell::getTplId, tplId)
               .eq(LuckysheetReportCell::getSheetId, sheetId)
               .eq(LuckysheetReportCell::getDelFlag, 0);
        return cellMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public void saveCellData(Long tplId, Long sheetId, String cellData) {
        LambdaQueryWrapper<LuckysheetReportCell> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LuckysheetReportCell::getTplId, tplId)
               .eq(LuckysheetReportCell::getSheetId, sheetId)
               .eq(LuckysheetReportCell::getDelFlag, 0);
        LuckysheetReportCell existing = cellMapper.selectOne(wrapper);

        if (existing != null) {
            existing.setCellData(cellData);
            existing.setVer(existing.getVer() + 1);
            existing.setUpdateTime(LocalDateTime.now());
            cellMapper.updateById(existing);
        } else {
            LuckysheetReportCell cell = new LuckysheetReportCell();
            cell.setId(IdWorker.nextId());
            cell.setTplId(tplId);
            cell.setSheetId(sheetId);
            cell.setCellData(cellData);
            cell.setVer(1);
            cell.setDelFlag(0);
            cell.setTenantId(1L);
            cell.setCreateTime(LocalDateTime.now());
            cell.setUpdateTime(LocalDateTime.now());
            cellMapper.insert(cell);
        }
    }

    @Override
    @Transactional
    public void batchSaveCellData(Long tplId, Long sheetId, String cellData) {
        saveCellData(tplId, sheetId, cellData);
    }

    @Override
    @Transactional
    public void deleteBySheetId(Long tplId, Long sheetId) {
        LambdaQueryWrapper<LuckysheetReportCell> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LuckysheetReportCell::getTplId, tplId)
               .eq(LuckysheetReportCell::getSheetId, sheetId)
               .eq(LuckysheetReportCell::getDelFlag, 0);
        LuckysheetReportCell existing = cellMapper.selectOne(wrapper);
        if (existing != null) {
            existing.setDelFlag(1);
            existing.setUpdateTime(LocalDateTime.now());
            cellMapper.updateById(existing);
        }
    }
}
