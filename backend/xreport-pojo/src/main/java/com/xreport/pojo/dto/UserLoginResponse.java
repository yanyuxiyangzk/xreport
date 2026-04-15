package com.xreport.pojo.dto;

public class UserLoginResponse {
    private String token;
    private String username;
    private String nickname;
    private Long userId;
    private Long expireTime;

    public UserLoginResponse() {}

    public UserLoginResponse(String token, String username, String nickname, Long userId, Long expireTime) {
        this.token = token;
        this.username = username;
        this.nickname = nickname;
        this.userId = userId;
        this.expireTime = expireTime;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getExpireTime() { return expireTime; }
    public void setExpireTime(Long expireTime) { this.expireTime = expireTime; }
}
