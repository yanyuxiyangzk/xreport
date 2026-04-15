package com.xreport.controller.system;

import com.xreport.common.result.Result;
import com.xreport.pojo.entity.SysDept;
import com.xreport.service.system.ISysDeptService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/depts")
@PreAuthorize("hasAuthority('system:manage')")
public class SysDeptController {

    private final ISysDeptService deptService;

    public SysDeptController(ISysDeptService deptService) {
        this.deptService = deptService;
    }

    @GetMapping
    public Result<List<SysDept>> getTree() {
        return Result.ok(deptService.getTree());
    }

    @GetMapping("/{id}")
    public Result<SysDept> getById(@PathVariable Long id) {
        return Result.ok(deptService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody SysDept dept) {
        deptService.add(dept);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysDept dept) {
        dept.setId(id);
        deptService.update(dept);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        deptService.delete(id);
        return Result.ok();
    }
}
