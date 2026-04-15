package com.xreport.service.system;

import com.xreport.pojo.entity.SysDept;
import java.util.List;

public interface ISysDeptService {
    List<SysDept> getTree();
    SysDept getById(Long id);
    void add(SysDept dept);
    void update(SysDept dept);
    void delete(Long id);
}
