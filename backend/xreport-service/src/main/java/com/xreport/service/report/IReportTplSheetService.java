package com.xreport.service.report;

import com.xreport.pojo.entity.ReportTplSheet;
import java.util.List;

public interface IReportTplSheetService {
    List<ReportTplSheet> getByTplId(Long tplId);
    ReportTplSheet getById(Long id);
    void add(ReportTplSheet sheet);
    void update(ReportTplSheet sheet);
    void delete(Long id);
    void deleteByTplId(Long tplId);
}
