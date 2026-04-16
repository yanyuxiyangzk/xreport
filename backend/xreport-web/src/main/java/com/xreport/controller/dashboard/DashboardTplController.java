package com.xreport.controller.dashboard;

import com.github.pagehelper.PageInfo;
import com.xreport.common.result.Result;
import com.xreport.pojo.dto.DashboardTplDto;
import com.xreport.pojo.entity.dashboard.DashboardTpl;
import com.xreport.service.dashboard.IDashboardTplService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard/tpl")
@PreAuthorize("hasAuthority('report:manage')")
public class DashboardTplController {

    private final IDashboardTplService tplService;

    public DashboardTplController(IDashboardTplService tplService) {
        this.tplService = tplService;
    }

    @GetMapping
    public Result<PageInfo<DashboardTpl>> pageList(DashboardTplDto dto) {
        return Result.ok(tplService.pageQuery(dto));
    }

    @GetMapping("/all")
    public Result<List<DashboardTpl>> list(DashboardTplDto dto) {
        return Result.ok(tplService.list(dto));
    }

    @GetMapping("/{id}")
    public Result<DashboardTpl> getById(@PathVariable Long id) {
        return Result.ok(tplService.getById(id));
    }

    @GetMapping("/code/{tplCode}")
    public Result<DashboardTpl> getByCode(@PathVariable String tplCode) {
        return Result.ok(tplService.getByCode(tplCode));
    }

    @PostMapping
    public Result<Void> add(@RequestBody DashboardTpl tpl) {
        tplService.add(tpl);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DashboardTpl tpl) {
        tpl.setId(id);
        tplService.update(tpl);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tplService.delete(id);
        return Result.ok();
    }
}