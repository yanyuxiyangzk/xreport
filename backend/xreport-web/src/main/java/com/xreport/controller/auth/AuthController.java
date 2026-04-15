package com.xreport.controller.auth;

import com.xreport.common.result.Result;
import com.xreport.pojo.dto.UserLoginRequest;
import com.xreport.pojo.dto.UserLoginResponse;
import com.xreport.pojo.entity.SysUser;
import com.xreport.pojo.vo.LoginUserVo;
import com.xreport.service.auth.IAuthService;
import com.xreport.service.system.ISysUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;
    private final ISysUserService userService;

    public AuthController(IAuthService authService, ISysUserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }

    @GetMapping("/userinfo")
    public Result<LoginUserVo> getUserInfo() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SysUser user = userService.getById(userId);
        LoginUserVo vo = new LoginUserVo(
            user.getId(), user.getUsername(), user.getNickname(), user.getEmail(), user.getCreateTime()
        );
        return Result.ok(vo);
    }
}
