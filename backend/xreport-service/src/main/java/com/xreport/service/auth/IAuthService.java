package com.xreport.service.auth;

import com.xreport.pojo.dto.UserLoginRequest;
import com.xreport.pojo.dto.UserLoginResponse;

public interface IAuthService {
    UserLoginResponse login(UserLoginRequest request);
    void logout();
}
