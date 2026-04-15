package com.xreport.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.SysDeptMapper;
import com.xreport.pojo.entity.SysDept;
import com.xreport.service.system.ISysDeptService;
import com.xreport.common.util.IdWorker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysDeptServiceImpl implements ISysDeptService {

    private final SysDeptMapper deptMapper;

    public SysDeptServiceImpl(SysDeptMapper deptMapper) {
        this.deptMapper = deptMapper;
    }

    @Override
    public List<SysDept> getTree() {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getDelFlag, 0).orderByAsc(SysDept::getSortOrder);
        List<SysDept> all = deptMapper.selectList(wrapper);
        return buildTree(all, 0L);
    }

    private List<SysDept> buildTree(List<SysDept> all, Long parentId) {
        List<SysDept> children = new ArrayList<>();
        for (SysDept dept : all) {
            if (dept.getParentId().equals(parentId)) {
                children.add(dept);
            }
        }
        return children;
    }

    @Override
    public SysDept getById(Long id) {
        SysDept dept = deptMapper.selectById(id);
        if (dept == null || dept.getDelFlag() == 1) {
            throw new BusinessException("部门不存在");
        }
        return dept;
    }

    @Override
    public void add(SysDept dept) {
        dept.setId(IdWorker.nextId());
        dept.setDelFlag(0);
        dept.setTenantId(1L);
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        if (dept.getParentId() != null && dept.getParentId() != 0) {
            SysDept parent = getById(dept.getParentId());
            dept.setAncestors(parent.getAncestors() + "," + parent.getId());
        } else {
            dept.setAncestors("0");
        }
        deptMapper.insert(dept);
    }

    @Override
    public void update(SysDept dept) {
        SysDept existing = getById(dept.getId());
        existing.setDeptName(dept.getDeptName());
        existing.setDeptCode(dept.getDeptCode());
        existing.setParentId(dept.getParentId());
        existing.setSortOrder(dept.getSortOrder());
        existing.setLeaderUserId(dept.getLeaderUserId());
        existing.setPhone(dept.getPhone());
        existing.setEmail(dept.getEmail());
        existing.setStatus(dept.getStatus());
        existing.setUpdateTime(LocalDateTime.now());

        if (dept.getParentId() != null && dept.getParentId() != 0) {
            SysDept parent = getById(dept.getParentId());
            existing.setAncestors(parent.getAncestors() + "," + parent.getId());
        } else {
            existing.setAncestors("0");
        }

        deptMapper.updateById(existing);
    }

    @Override
    public void delete(Long id) {
        SysDept dept = getById(id);
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, id).eq(SysDept::getDelFlag, 0);
        long count = deptMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("存在下级部门，无法删除");
        }
        dept.setDelFlag(1);
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.updateById(dept);
    }
}
