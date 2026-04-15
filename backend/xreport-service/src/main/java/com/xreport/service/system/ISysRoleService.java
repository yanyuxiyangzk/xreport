package com.xreport.service.system;

import com.github.pagehelper.PageInfo;
import com.xreport.pojo.dto.SysRoleDto;
import com.xreport.pojo.entity.SysRole;
import java.util.List;

public interface ISysRoleService {
    PageInfo<SysRole> pageQuery(SysRoleDto dto);
    SysRole getById(Long id);
    void add(SysRole role);
    void update(SysRole role);
    void delete(Long id);
    List<Long> getMenuIdsByRoleId(Long roleId);
    void assignMenus(Long roleId, List<Long> menuIds);
}
