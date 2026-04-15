package com.xreport.service.report.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.report.LuckysheetReportCellMapper;
import com.xreport.mapper.report.ReportTplMapper;
import com.xreport.mapper.report.ReportTplSheetMapper;
import com.xreport.pojo.dto.FullTemplateResponse;
import com.xreport.pojo.dto.FullTemplateSaveRequest;
import com.xreport.pojo.dto.ReportTplDto;
import com.xreport.pojo.entity.LuckysheetReportCell;
import com.xreport.pojo.entity.ReportTpl;
import com.xreport.pojo.entity.ReportTplSheet;
import com.xreport.service.report.ILuckysheetReportCellService;
import com.xreport.service.report.IReportTplService;
import com.xreport.service.report.IReportTplSheetService;
import com.xreport.common.util.IdWorker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportTplServiceImpl implements IReportTplService {

    private final ReportTplMapper tplMapper;
    private final ReportTplSheetMapper sheetMapper;
    private final LuckysheetReportCellMapper cellMapper;
    private final IReportTplSheetService sheetService;
    private final ILuckysheetReportCellService cellService;

    public ReportTplServiceImpl(ReportTplMapper tplMapper,
                                  ReportTplSheetMapper sheetMapper,
                                  LuckysheetReportCellMapper cellMapper,
                                  IReportTplSheetService sheetService,
                                  ILuckysheetReportCellService cellService) {
        this.tplMapper = tplMapper;
        this.sheetMapper = sheetMapper;
        this.cellMapper = cellMapper;
        this.sheetService = sheetService;
        this.cellService = cellService;
    }

    @Override
    public PageInfo<ReportTpl> pageQuery(ReportTplDto dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<ReportTpl> wrapper = new LambdaQueryWrapper<>();
        if (dto.getTplName() != null && !dto.getTplName().isEmpty()) {
            wrapper.like(ReportTpl::getTplName, dto.getTplName());
        }
        if (dto.getTplType() != null) {
            wrapper.eq(ReportTpl::getTplType, dto.getTplType());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(ReportTpl::getStatus, dto.getStatus());
        }
        wrapper.eq(ReportTpl::getDelFlag, 0).orderByDesc(ReportTpl::getCreateTime);
        List<ReportTpl> list = tplMapper.selectList(wrapper);
        return new PageInfo<>(list);
    }

    @Override
    public List<ReportTpl> list(ReportTplDto dto) {
        LambdaQueryWrapper<ReportTpl> wrapper = new LambdaQueryWrapper<>();
        if (dto.getTplName() != null && !dto.getTplName().isEmpty()) {
            wrapper.like(ReportTpl::getTplName, dto.getTplName());
        }
        if (dto.getTplType() != null) {
            wrapper.eq(ReportTpl::getTplType, dto.getTplType());
        }
        wrapper.eq(ReportTpl::getDelFlag, 0).eq(ReportTpl::getStatus, 1).orderByDesc(ReportTpl::getCreateTime);
        return tplMapper.selectList(wrapper);
    }

    @Override
    public ReportTpl getById(Long id) {
        ReportTpl tpl = tplMapper.selectById(id);
        if (tpl == null || tpl.getDelFlag() == 1) {
            throw new BusinessException("报表模板不存在");
        }
        return tpl;
    }

    @Override
    @Transactional
    public void add(ReportTpl tpl) {
        tpl.setId(IdWorker.nextId());
        tpl.setDelFlag(0);
        tpl.setStatus(1);
        tpl.setTenantId(1L);
        tpl.setCreateTime(LocalDateTime.now());
        tpl.setUpdateTime(LocalDateTime.now());
        tplMapper.insert(tpl);
    }

    @Override
    @Transactional
    public void update(ReportTpl tpl) {
        ReportTpl existing = getById(tpl.getId());
        existing.setTplName(tpl.getTplName());
        existing.setTplType(tpl.getTplType());
        existing.setSearchFormType(tpl.getSearchFormType());
        existing.setCdnHost(tpl.getCdnHost());
        existing.setWaterMarkType(tpl.getWaterMarkType());
        existing.setThumbnail(tpl.getThumbnail());
        existing.setUpdateTime(LocalDateTime.now());
        tplMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ReportTpl tpl = getById(id);
        tpl.setDelFlag(1);
        tpl.setUpdateTime(LocalDateTime.now());
        tplMapper.updateById(tpl);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        ReportTpl tpl = getById(id);
        tpl.setStatus(status);
        tpl.setUpdateTime(LocalDateTime.now());
        tplMapper.updateById(tpl);
    }

    @Override
    @Transactional
    public void publish(Long id) {
        ReportTpl tpl = getById(id);
        tpl.setStatus(2);
        tpl.setUpdateTime(LocalDateTime.now());
        tplMapper.updateById(tpl);
    }

    @Override
    @Transactional
    public Long saveFullTemplate(FullTemplateSaveRequest request) {
        Long tplId = request.getId();

        if (tplId == null) {
            // Create new template
            ReportTpl tpl = new ReportTpl();
            tplId = IdWorker.nextId();
            tpl.setId(tplId);
            tpl.setTplName(request.getTplName());
            tpl.setTplType(1); // 1 = Excel报表
            tpl.setDelFlag(0);
            tpl.setStatus(1);
            tpl.setTenantId(1L);
            tpl.setCreateTime(LocalDateTime.now());
            tpl.setUpdateTime(LocalDateTime.now());
            tplMapper.insert(tpl);
        } else {
            // Update existing template
            ReportTpl existing = getById(tplId);
            existing.setTplName(request.getTplName());
            existing.setUpdateTime(LocalDateTime.now());
            tplMapper.updateById(existing);

            // Delete existing sheets and cell data
            List<ReportTplSheet> existingSheets = sheetService.getByTplId(tplId);
            for (ReportTplSheet sheet : existingSheets) {
                cellService.deleteBySheetId(tplId, sheet.getId());
            }
            sheetService.deleteByTplId(tplId);
        }

        // Save sheets and cell data
        if (request.getSheets() != null) {
            for (FullTemplateSaveRequest.SheetData sheetData : request.getSheets()) {
                ReportTplSheet sheet = new ReportTplSheet();
                Long sheetId = IdWorker.nextId();
                sheet.setId(sheetId);
                sheet.setTplId(tplId);
                sheet.setSheetName(sheetData.getSheetName());
                sheet.setSheetOrder(sheetData.getSheetIndex());
                sheet.setSort(sheetData.getSheetIndex());
                sheet.setDelFlag(0);
                sheet.setTenantId(1L);
                sheet.setCreateTime(LocalDateTime.now());
                sheet.setUpdateTime(LocalDateTime.now());
                sheetService.add(sheet);

                // Save cell data
                if (sheetData.getCells() != null) {
                    String cellDataJson = JSONUtil.toJsonStr(sheetData.getCells());
                    cellService.batchSaveCellData(tplId, sheetId, cellDataJson);
                }
            }
        }

        return tplId;
    }

    @Override
    public FullTemplateResponse loadFullTemplate(Long tplId) {
        ReportTpl tpl = getById(tplId);

        FullTemplateResponse response = new FullTemplateResponse();
        response.setId(tpl.getId());
        response.setTplName(tpl.getTplName());
        response.setTplType(tpl.getTplType() != null ? String.valueOf(tpl.getTplType()) : "luckysheet");
        response.setStatus(tpl.getStatus());

        List<ReportTplSheet> sheets = sheetService.getByTplId(tplId);
        List<FullTemplateResponse.SheetData> sheetDataList = new ArrayList<>();

        for (ReportTplSheet sheet : sheets) {
            FullTemplateResponse.SheetData sheetData = new FullTemplateResponse.SheetData();
            sheetData.setId(sheet.getId());
            sheetData.setSheetName(sheet.getSheetName());
            sheetData.setSheetIndex(sheet.getSheetOrder());

            // Get cell data
            LuckysheetReportCell cell = cellService.getBySheetId(tplId, sheet.getId());
            if (cell != null && cell.getCellData() != null) {
                cn.hutool.json.JSONArray jsonArray = JSONUtil.parseArray(cell.getCellData());
                List<Object> cells = JSONUtil.toList(jsonArray, Object.class);
                sheetData.setCells(cells);
            } else {
                sheetData.setCells(new ArrayList<>());
            }

            sheetDataList.add(sheetData);
        }

        response.setSheets(sheetDataList);
        return response;
    }
}
