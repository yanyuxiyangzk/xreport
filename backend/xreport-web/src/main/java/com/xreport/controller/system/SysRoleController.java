package com.xreport.controller.system;

import com.github.pagehelper.PageInfo;
import com.xreport.common.result.Result;
import com.xreport.pojo.dto.SysRoleDto;
import com.xreport.pojo.entity.SysRole;
import com.xreport.service.system.ISysRoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/roles")
@PreAuthorize("hasAuthority('system:manage')")
public class SysRoleController {

    private final ISysRoleService roleService;

    public SysRoleController(ISysRoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public Result<PageInfo<SysRole>> list(SysRoleDto dto) {
        return Result.ok(roleService.pageQuery(dto));
    }

    @GetMapping("/{id}")
    public Result<SysRole> getById(@PathVariable Long id) {
        return Result.ok(roleService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody SysRole role) {
        roleService.add(role);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        roleService.update(role);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.ok();
    }

    @GetMapping("/{id}/menus")
    public Result<List<Long>> getRoleMenus(@PathVariable Long id) {
        return Result.ok(roleService.getMenuIdsByRoleId(id));
    }

    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(id, menuIds);
        return Result.ok();
    }
}
