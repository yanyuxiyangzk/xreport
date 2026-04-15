package com.xreport.service.report;

import com.github.pagehelper.PageInfo;
import com.xreport.pojo.dto.FullTemplateResponse;
import com.xreport.pojo.dto.FullTemplateSaveRequest;
import com.xreport.pojo.dto.ReportTplDto;
import com.xreport.pojo.entity.ReportTpl;
import java.util.List;

public interface IReportTplService {
    PageInfo<ReportTpl> pageQuery(ReportTplDto dto);
    List<ReportTpl> list(ReportTplDto dto);
    ReportTpl getById(Long id);
    void add(ReportTpl tpl);
    void update(ReportTpl tpl);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
    void publish(Long id);
    Long saveFullTemplate(FullTemplateSaveRequest request);
    FullTemplateResponse loadFullTemplate(Long tplId);
}
