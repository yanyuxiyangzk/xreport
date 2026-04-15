package com.xreport.pojo.vo;

import java.time.LocalDateTime;

public class LoginUserVo {
    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private LocalDateTime createTime;

    public LoginUserVo() {}

    public LoginUserVo(Long userId, String username, String nickname, String email, LocalDateTime createTime) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.createTime = createTime;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
