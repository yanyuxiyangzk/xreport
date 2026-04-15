package com.xreport.controller.system;

import com.xreport.common.result.Result;
import com.xreport.pojo.entity.SysMenu;
import com.xreport.service.system.ISysMenuService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/menus")
@PreAuthorize("hasAuthority('system:manage')")
public class SysMenuController {

    private final ISysMenuService menuService;

    public SysMenuController(ISysMenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public Result<List<SysMenu>> getTree() {
        return Result.ok(menuService.getTree());
    }

    @GetMapping("/{id}")
    public Result<SysMenu> getById(@PathVariable Long id) {
        return Result.ok(menuService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody SysMenu menu) {
        menuService.add(menu);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysMenu menu) {
        menu.setId(id);
        menuService.update(menu);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return Result.ok();
    }
}
