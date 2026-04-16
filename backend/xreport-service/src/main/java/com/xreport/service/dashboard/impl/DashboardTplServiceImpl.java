package com.xreport.service.dashboard.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xreport.common.exception.BusinessException;
import com.xreport.common.util.IdWorker;
import com.xreport.mapper.dashboard.DashboardTplMapper;
import com.xreport.pojo.dto.DashboardTplDto;
import com.xreport.pojo.entity.dashboard.DashboardTpl;
import com.xreport.service.dashboard.IDashboardTplService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardTplServiceImpl implements IDashboardTplService {

    private final DashboardTplMapper tplMapper;

    public DashboardTplServiceImpl(DashboardTplMapper tplMapper) {
        this.tplMapper = tplMapper;
    }

    @Override
    public PageInfo<DashboardTpl> pageQuery(DashboardTplDto dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<DashboardTpl> wrapper = new LambdaQueryWrapper<>();
        if (dto.getTplName() != null && !dto.getTplName().isEmpty()) {
            wrapper.like(DashboardTpl::getTplName, dto.getTplName());
        }
        if (dto.getTplCode() != null && !dto.getTplCode().isEmpty()) {
            wrapper.eq(DashboardTpl::getTplCode, dto.getTplCode());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(DashboardTpl::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(DashboardTpl::getCreateTime);
        List<DashboardTpl> list = tplMapper.selectList(wrapper);
        return new PageInfo<>(list);
    }

    @Override
    public List<DashboardTpl> list(DashboardTplDto dto) {
        LambdaQueryWrapper<DashboardTpl> wrapper = new LambdaQueryWrapper<>();
        if (dto.getTplName() != null && !dto.getTplName().isEmpty()) {
            wrapper.like(DashboardTpl::getTplName, dto.getTplName());
        }
        if (dto.getTplCode() != null && !dto.getTplCode().isEmpty()) {
            wrapper.eq(DashboardTpl::getTplCode, dto.getTplCode());
        }
        wrapper.eq(DashboardTpl::getStatus, 1).orderByDesc(DashboardTpl::getCreateTime);
        return tplMapper.selectList(wrapper);
    }

    @Override
    public DashboardTpl getById(Long id) {
        DashboardTpl tpl = tplMapper.selectById(id);
        if (tpl == null) {
            throw new BusinessException("大屏模板不存在");
        }
        return tpl;
    }

    @Override
    public DashboardTpl getByCode(String tplCode) {
        LambdaQueryWrapper<DashboardTpl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DashboardTpl::getTplCode, tplCode);
        DashboardTpl tpl = tplMapper.selectOne(wrapper);
        if (tpl == null) {
            throw new BusinessException("大屏模板不存在");
        }
        return tpl;
    }

    @Override
    @Transactional
    public void add(DashboardTpl tpl) {
        tpl.setId(IdWorker.nextId());
        tpl.setStatus(1);
        tpl.setCreateTime(LocalDateTime.now());
        tpl.setUpdateTime(LocalDateTime.now());
        tplMapper.insert(tpl);
    }

    @Override
    @Transactional
    public void update(DashboardTpl tpl) {
        DashboardTpl existing = getById(tpl.getId());
        existing.setTplName(tpl.getTplName());
        existing.setTplCode(tpl.getTplCode());
        existing.setDescription(tpl.getDescription());
        existing.setConfig(tpl.getConfig());
        existing.setPreviewUrl(tpl.getPreviewUrl());
        existing.setStatus(tpl.getStatus());
        existing.setUpdateTime(LocalDateTime.now());
        tplMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        DashboardTpl tpl = getById(id);
        tpl.setStatus(0);
        tpl.setUpdateTime(LocalDateTime.now());
        tplMapper.updateById(tpl);
    }
}