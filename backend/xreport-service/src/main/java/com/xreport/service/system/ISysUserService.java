package com.xreport.service.system;

import com.github.pagehelper.PageInfo;
import com.xreport.pojo.dto.SysUserDto;
import com.xreport.pojo.entity.SysUser;
import java.util.List;

public interface ISysUserService {
    SysUser getByUsername(String username);
    PageInfo<SysUser> pageQuery(SysUserDto dto);
    SysUser getById(Long id);
    void add(SysUser user, List<Long> roleIds);
    void update(SysUser user, List<Long> roleIds);
    void delete(Long id);
    void resetPassword(Long id, String newPassword);
    void updateStatus(Long id, Integer status);
    void assignRoles(Long userId, List<Long> roleIds);
}
