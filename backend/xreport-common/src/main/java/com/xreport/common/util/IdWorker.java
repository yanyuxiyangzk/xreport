package com.xreport.common.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * Snowflake ID 生成工具
 * 基于 Hutool 的 Snowflake 算法
 */
public class IdWorker {

    private static final Snowflake snowflake = IdUtil.getSnowflake(1, 1);

    public static Long nextId() {
        return snowflake.nextId();
    }

    public static String nextIdStr() {
        return snowflake.nextIdStr();
    }
}
