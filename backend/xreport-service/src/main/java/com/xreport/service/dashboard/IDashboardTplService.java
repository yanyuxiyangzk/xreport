package com.xreport.service.dashboard;

import com.github.pagehelper.PageInfo;
import com.xreport.pojo.dto.DashboardTplDto;
import com.xreport.pojo.entity.dashboard.DashboardTpl;

import java.util.List;

public interface IDashboardTplService {
    PageInfo<DashboardTpl> pageQuery(DashboardTplDto dto);
    List<DashboardTpl> list(DashboardTplDto dto);
    DashboardTpl getById(Long id);
    DashboardTpl getByCode(String tplCode);
    void add(DashboardTpl tpl);
    void update(DashboardTpl tpl);
    void delete(Long id);
}