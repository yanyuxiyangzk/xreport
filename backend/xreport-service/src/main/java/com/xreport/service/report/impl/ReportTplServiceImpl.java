package com.xreport.service.report.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.report.ReportTplMapper;
import com.xreport.pojo.dto.ReportTplDto;
import com.xreport.pojo.entity.ReportTpl;
import com.xreport.service.report.IReportTplService;
import com.xreport.common.util.IdWorker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportTplServiceImpl implements IReportTplService {

    private final ReportTplMapper tplMapper;

    public ReportTplServiceImpl(ReportTplMapper tplMapper) {
        this.tplMapper = tplMapper;
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
}
