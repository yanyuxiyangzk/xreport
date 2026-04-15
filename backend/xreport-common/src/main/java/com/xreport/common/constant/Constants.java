package com.xreport.common.constant;

/**
 * 通用常量
 */
public class Constants {

    /**
     * 租户ID（超级管理员）
     */
    public static final Long TENANT_ADMIN = 1L;

    /**
     * 超级管理员用户ID
     */
    public static final Long SUPER_ADMIN_USER_ID = 1L;

    /**
     * 正常状态
     */
    public static final Integer STATUS_NORMAL = 1;

    /**
     * 停用状态
     */
    public static final Integer STATUS_DISABLED = 2;

    /**
     * 删除标志（存在）
     */
    public static final Integer DEL_FLAG_EXISTS = 1;

    /**
     * 删除标志（已删除）
     */
    public static final Integer DEL_FLAG_DELETED = 0;

    /**
     * 菜单类型：目录
     */
    public static final Integer MENU_TYPE_DIR = 0;

    /**
     * 菜单类型：菜单
     */
    public static final Integer MENU_TYPE_MENU = 1;

    /**
     * 菜单类型：按钮
     */
    public static final Integer MENU_TYPE_BUTTON = 2;

    /**
     * JWT Token 过期时间（24小时）
     */
    public static final Long JWT_EXPIRATION = 24 * 60 * 60 * 1000L;

    /**
     * JWT Token 签名密钥
     */
    public static final String JWT_SECRET = "xreport-secret-key-2026-very-long-secret-for-jwt-signing";
}
