package com.xreport.service.system;

import com.xreport.pojo.entity.SysMenu;
import java.util.List;

public interface ISysMenuService {
    List<SysMenu> getTree();
    SysMenu getById(Long id);
    void add(SysMenu menu);
    void update(SysMenu menu);
    void delete(Long id);
}
