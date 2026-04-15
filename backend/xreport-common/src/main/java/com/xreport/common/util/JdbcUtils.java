package com.xreport.common.util;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多数据库工具类
 * 初期只支持 MySQL，预留扩展接口
 */
public class JdbcUtils {
    private static final Logger log = LoggerFactory.getLogger(JdbcUtils.class);

    private static final Map<String, DataSource> DATA_SOURCE_MAP = new HashMap<>();

    /**
     * 获取数据库连接
     *
     * @param jdbcUrl  JDBC URL
     * @param username 用户名
     * @param password 密码
     * @return 数据库连接
     */
    public static Connection getConnection(String jdbcUrl, String username, String password) {
        String key = jdbcUrl + ":" + username;
        DataSource dataSource = DATA_SOURCE_MAP.get(key);
        if (dataSource == null) {
            dataSource = createDataSource(jdbcUrl, username, password);
            DATA_SOURCE_MAP.put(key, dataSource);
        }
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            log.error("获取数据库连接失败", e);
            throw new RuntimeException("获取数据库连接失败", e);
        }
    }

    /**
     * 创建数据源
     */
    private static DataSource createDataSource(String jdbcUrl, String username, String password) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(20);
        dataSource.setMaxWait(60000);
        return dataSource;
    }

    /**
     * 执行查询
     *
     * @param conn   数据库连接
     * @param sql    SQL语句
     * @param params 参数列表
     * @return 查询结果列表
     */
    public static List<Map<String, Object>> executeQuery(Connection conn, String sql, List<Object> params) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            // 设置参数
            if (params != null && !params.isEmpty()) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
            }
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                resultList.add(row);
            }
        } catch (SQLException e) {
            log.error("执行查询失败: {}", sql, e);
            throw new RuntimeException("执行查询失败", e);
        } finally {
            close(conn, ps, rs);
        }
        return resultList;
    }

    /**
     * 关闭资源
     */
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            log.error("关闭ResultSet失败", e);
        }
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            log.error("关闭PreparedStatement失败", e);
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            log.error("关闭Connection失败", e);
        }
    }

    /**
     * 释放所有数据源
     */
    public static void closeAll() {
        for (DataSource ds : DATA_SOURCE_MAP.values()) {
            if (ds instanceof DruidDataSource) {
                ((DruidDataSource) ds).close();
            }
        }
        DATA_SOURCE_MAP.clear();
    }
}
