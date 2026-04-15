package com.xreport.controller.system;

import com.github.pagehelper.PageInfo;
import com.xreport.common.result.Result;
import com.xreport.pojo.dto.SysUserDto;
import com.xreport.pojo.entity.SysUser;
import com.xreport.service.system.ISysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/users")
@PreAuthorize("hasAuthority('system:manage')")
public class SysUserController {

    private final ISysUserService userService;

    public SysUserController(ISysUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Result<PageInfo<SysUser>> list(SysUserDto dto) {
        return Result.ok(userService.pageQuery(dto));
    }

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Map<String, Object> body) {
        SysUser user = mapToUser(body);
        @SuppressWarnings("unchecked")
        List<Long> roleIds = body.get("roleIds") != null ? (List<Long>) body.get("roleIds") : null;
        userService.add(user, roleIds);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        SysUser user = mapToUser(body);
        user.setId(id);
        @SuppressWarnings("unchecked")
        List<Long> roleIds = body.get("roleIds") != null ? (List<Long>) body.get("roleIds") : null;
        userService.update(user, roleIds);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/reset-pwd")
    public Result<Void> resetPwd(@PathVariable Long id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.ok();
    }

    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.ok();
    }

    private SysUser mapToUser(Map<String, Object> body) {
        SysUser user = new SysUser();
        user.setUsername((String) body.get("username"));
        user.setPassword((String) body.get("password"));
        user.setNickname((String) body.get("nickname"));
        user.setEmail((String) body.get("email"));
        user.setPhone((String) body.get("phone"));
        user.setAvatar((String) body.get("avatar"));
        if (body.get("status") != null) {
            user.setStatus((Integer) body.get("status"));
        }
        return user;
    }
}
