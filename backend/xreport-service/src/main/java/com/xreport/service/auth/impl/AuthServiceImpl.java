package com.xreport.service.auth.impl;

import com.xreport.common.exception.BusinessException;
import com.xreport.pojo.dto.UserLoginRequest;
import com.xreport.pojo.dto.UserLoginResponse;
import com.xreport.pojo.entity.SysUser;
import com.xreport.service.auth.IAuthService;
import com.xreport.service.system.ISysUserService;
import com.xreport.common.util.JwtUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {

    private static final String JWT_SECRET = "xreport-jwt-secret-key-must-be-at-least-512-bits-long-for-hs512";
    private static final long EXPIRE_TIME = 86400000L;

    private final ISysUserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(ISysUserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        SysUser user = userService.getByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(401, "账号已被禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), JWT_SECRET, EXPIRE_TIME);
        long expireTime = System.currentTimeMillis() + EXPIRE_TIME;

        return new UserLoginResponse(token, user.getUsername(), user.getNickname(), user.getId(), expireTime);
    }

    @Override
    public void logout() {
        // JWT stateless，logout 由客户端删除 token
    }
}
