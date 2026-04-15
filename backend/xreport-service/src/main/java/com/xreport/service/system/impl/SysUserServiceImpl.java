package com.xreport.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.SysUserMapper;
import com.xreport.mapper.SysUserRoleMapper;
import com.xreport.pojo.dto.SysUserDto;
import com.xreport.pojo.entity.SysUser;
import com.xreport.pojo.entity.SysUserRole;
import com.xreport.service.system.ISysUserService;
import com.xreport.common.util.IdWorker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public SysUserServiceImpl(SysUserMapper userMapper, SysUserRoleMapper userRoleMapper,
                               BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username).eq(SysUser::getDelFlag, 0);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public PageInfo<SysUser> pageQuery(SysUserDto dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            wrapper.like(SysUser::getUsername, dto.getUsername());
        }
        if (dto.getNickname() != null && !dto.getNickname().isEmpty()) {
            wrapper.like(SysUser::getNickname, dto.getNickname());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, dto.getStatus());
        }
        wrapper.eq(SysUser::getDelFlag, 0).orderByDesc(SysUser::getCreateTime);
        List<SysUser> list = userMapper.selectList(wrapper);
        return new PageInfo<>(list);
    }

    @Override
    public SysUser getById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null || user.getDelFlag() == 1) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    @Transactional
    public void add(SysUser user, List<Long> roleIds) {
        SysUser exist = getByUsername(user.getUsername());
        if (exist != null) {
            throw new BusinessException("用户名已存在");
        }
        user.setId(IdWorker.nextId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDelFlag(0);
        user.setStatus(1);
        user.setTenantId(1L);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);

        if (roleIds != null && !roleIds.isEmpty()) {
            saveUserRoles(user.getId(), roleIds);
        }
    }

    @Override
    @Transactional
    public void update(SysUser user, List<Long> roleIds) {
        SysUser existing = getById(user.getId());
        existing.setNickname(user.getNickname());
        existing.setEmail(user.getEmail());
        existing.setPhone(user.getPhone());
        existing.setAvatar(user.getAvatar());
        existing.setStatus(user.getStatus());
        existing.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(existing);

        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, user.getId());
        userRoleMapper.delete(wrapper);

        if (roleIds != null && !roleIds.isEmpty()) {
            saveUserRoles(user.getId(), roleIds);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SysUser user = getById(id);
        user.setDelFlag(1);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        SysUser user = getById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser user = getById(id);
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);

        if (roleIds != null && !roleIds.isEmpty()) {
            saveUserRoles(userId, roleIds);
        }
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        List<SysUserRole> list = new ArrayList<>();
        for (Long roleId : roleIds) {
            SysUserRole ur = new SysUserRole();
            ur.setId(IdWorker.nextId());
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            ur.setTenantId(1L);
            ur.setCreateTime(LocalDateTime.now());
            list.add(ur);
        }
        for (SysUserRole ur : list) { userRoleMapper.insert(ur); }
    }
}
