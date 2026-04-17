package com.xreport.mapper.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.pojo.entity.reportdatasource.ReportDatasource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ReportDatasourceMapper 单元测试
 *
 * 注意: 由于Mapper使用MyBatis-Plus的BaseMapper，这些测试主要验证：
 * 1. Mapper方法被正确调用
 * 2. 查询条件被正确构建
 */
@ExtendWith(MockitoExtension.class)
class ReportDatasourceMapperTest {

    @Mock
    private ReportDatasourceMapper datasourceMapper;

    private ReportDatasource testDatasource;

    @BeforeEach
    void setUp() {
        testDatasource = new ReportDatasource();
        testDatasource.setId(1L);
        testDatasource.setDatasourceName("测试数据源");
        testDatasource.setDatasourceType("jdbc");
        testDatasource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        testDatasource.setUsername("root");
        testDatasource.setPassword("password");
        testDatasource.setStatus(1);
        testDatasource.setDelFlag(0);
        testDatasource.setTenantId(1L);
        testDatasource.setCreateTime(LocalDateTime.now());
        testDatasource.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("selectById - 应该返回数据源")
    void selectById_ShouldReturnDatasource() {
        // Arrange
        when(datasourceMapper.selectById(1L)).thenReturn(testDatasource);

        // Act
        ReportDatasource result = datasourceMapper.selectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试数据源", result.getDatasourceName());
        assertEquals("jdbc", result.getDatasourceType());
        verify(datasourceMapper).selectById(1L);
    }

    @Test
    @DisplayName("selectById - 数据源不存在应返回null")
    void selectById_WhenNotExists_ShouldReturnNull() {
        // Arrange
        when(datasourceMapper.selectById(999L)).thenReturn(null);

        // Act
        ReportDatasource result = datasourceMapper.selectById(999L);

        // Assert
        assertNull(result);
        verify(datasourceMapper).selectById(999L);
    }

    @Test
    @DisplayName("insert - 应该插入数据源并返回影响行数")
    void insert_ShouldInsertAndReturnAffectedRows() {
        // Arrange
        ReportDatasource newDatasource = new ReportDatasource();
        newDatasource.setDatasourceName("新数据源");
        newDatasource.setDatasourceType("jdbc");

        when(datasourceMapper.insert(any(ReportDatasource.class))).thenReturn(1);

        // Act
        int result = datasourceMapper.insert(newDatasource);

        // Assert
        assertEquals(1, result);
        verify(datasourceMapper).insert(any(ReportDatasource.class));
    }

    @Test
    @DisplayName("updateById - 应该更新数据源")
    void updateById_ShouldUpdateDatasource() {
        // Arrange
        testDatasource.setDatasourceName("更新后的数据源");
        testDatasource.setJdbcUrl("jdbc:mysql://localhost:3306/updated");

        when(datasourceMapper.updateById(any(ReportDatasource.class))).thenReturn(1);

        // Act
        int result = datasourceMapper.updateById(testDatasource);

        // Assert
        assertEquals(1, result);
        ArgumentCaptor<ReportDatasource> captor = ArgumentCaptor.forClass(ReportDatasource.class);
        verify(datasourceMapper).updateById(captor.capture());
        assertEquals("更新后的数据源", captor.getValue().getDatasourceName());
        assertEquals("jdbc:mysql://localhost:3306/updated", captor.getValue().getJdbcUrl());
    }

    @Test
    @DisplayName("deleteById - 应该删除数据源")
    void deleteById_ShouldDeleteDatasource() {
        // Arrange
        when(datasourceMapper.deleteById(1L)).thenReturn(1);

        // Act
        int result = datasourceMapper.deleteById(1L);

        // Assert
        assertEquals(1, result);
        verify(datasourceMapper).deleteById(1L);
    }

    @Test
    @DisplayName("selectList - 使用Wrapper查询应该返回数据源列表")
    void selectList_WithWrapper_ShouldReturnDatasources() {
        // Arrange
        List<ReportDatasource> datasources = List.of(testDatasource);
        when(datasourceMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasources);

        // Act
        LambdaQueryWrapper<ReportDatasource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportDatasource::getDelFlag, 0);
        List<ReportDatasource> result = datasourceMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(datasourceMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectList - 按名称模糊查询应该返回匹配的数据源")
    void selectList_WithNameLike_ShouldReturnMatchedDatasources() {
        // Arrange
        List<ReportDatasource> datasources = List.of(testDatasource);
        when(datasourceMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasources);

        // Act
        LambdaQueryWrapper<ReportDatasource> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ReportDatasource::getDatasourceName, "测试");
        wrapper.eq(ReportDatasource::getDelFlag, 0);
        List<ReportDatasource> result = datasourceMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(datasourceMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectList - 按数据源类型查询应该返回匹配的数据源")
    void selectList_WithDatasourceType_ShouldReturnMatchedDatasources() {
        // Arrange
        List<ReportDatasource> datasources = List.of(testDatasource);
        when(datasourceMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasources);

        // Act
        LambdaQueryWrapper<ReportDatasource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportDatasource::getDatasourceType, "jdbc");
        wrapper.eq(ReportDatasource::getDelFlag, 0);
        List<ReportDatasource> result = datasourceMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("jdbc", result.get(0).getDatasourceType());
    }

    @Test
    @DisplayName("selectList - 按状态查询应该返回匹配的数据源")
    void selectList_WithStatus_ShouldReturnMatchedDatasources() {
        // Arrange
        List<ReportDatasource> datasources = List.of(testDatasource);
        when(datasourceMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasources);

        // Act
        LambdaQueryWrapper<ReportDatasource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportDatasource::getStatus, 1);
        wrapper.eq(ReportDatasource::getDelFlag, 0);
        List<ReportDatasource> result = datasourceMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getStatus());
    }

    @Test
    @DisplayName("selectList - 查询启用的数据源应该返回status=1且delFlag=0的数据源")
    void selectList_EnabledDatasources_ShouldReturnActiveOnes() {
        // Arrange
        List<ReportDatasource> datasources = List.of(testDatasource);
        when(datasourceMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasources);

        // Act
        LambdaQueryWrapper<ReportDatasource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportDatasource::getDelFlag, 0)
               .eq(ReportDatasource::getStatus, 1)
               .orderByDesc(ReportDatasource::getCreateTime);
        List<ReportDatasource> result = datasourceMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getDelFlag());
        assertEquals(1, result.get(0).getStatus());
    }

    @Test
    @DisplayName("selectList - 空Wrapper应该返回所有数据源")
    void selectList_WithEmptyWrapper_ShouldReturnAllDatasources() {
        // Arrange
        List<ReportDatasource> datasources = List.of(testDatasource);
        when(datasourceMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasources);

        // Act
        LambdaQueryWrapper<ReportDatasource> wrapper = new LambdaQueryWrapper<>();
        List<ReportDatasource> result = datasourceMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}