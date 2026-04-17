package com.xreport.mapper.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.pojo.entity.reportdataset.ReportDataset;
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
 * ReportDatasetMapper 单元测试
 *
 * 注意: 由于Mapper使用MyBatis-Plus的BaseMapper，这些测试主要验证：
 * 1. Mapper方法被正确调用
 * 2. 查询条件被正确构建
 */
@ExtendWith(MockitoExtension.class)
class ReportDatasetMapperTest {

    @Mock
    private ReportDatasetMapper datasetMapper;

    private ReportDataset testDataset;

    @BeforeEach
    void setUp() {
        testDataset = new ReportDataset();
        testDataset.setId(1L);
        testDataset.setDatasetName("测试数据集");
        testDataset.setDatasetCode("TEST_DATASET");
        testDataset.setDatasetDesc("这是一个测试数据集");
        testDataset.setDatasetType("jdbc");
        testDataset.setDatasourceId(100L);
        testDataset.setDatasourceName("测试数据源");
        testDataset.setSqlStatement("SELECT * FROM users");
        testDataset.setStatus(1);
        testDataset.setDelFlag(0);
        testDataset.setTenantId(1L);
        testDataset.setCreateTime(LocalDateTime.now());
        testDataset.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("selectById - 应该返回数据集")
    void selectById_ShouldReturnDataset() {
        // Arrange
        when(datasetMapper.selectById(1L)).thenReturn(testDataset);

        // Act
        ReportDataset result = datasetMapper.selectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试数据集", result.getDatasetName());
        assertEquals("TEST_DATASET", result.getDatasetCode());
        verify(datasetMapper).selectById(1L);
    }

    @Test
    @DisplayName("selectById - 数据集不存在应返回null")
    void selectById_WhenNotExists_ShouldReturnNull() {
        // Arrange
        when(datasetMapper.selectById(999L)).thenReturn(null);

        // Act
        ReportDataset result = datasetMapper.selectById(999L);

        // Assert
        assertNull(result);
        verify(datasetMapper).selectById(999L);
    }

    @Test
    @DisplayName("insert - 应该插入数据集并返回影响行数")
    void insert_ShouldInsertAndReturnAffectedRows() {
        // Arrange
        ReportDataset newDataset = new ReportDataset();
        newDataset.setDatasetName("新数据集");
        newDataset.setDatasetCode("NEW_DATASET");
        newDataset.setDatasetType("jdbc");

        when(datasetMapper.insert(any(ReportDataset.class))).thenReturn(1);

        // Act
        int result = datasetMapper.insert(newDataset);

        // Assert
        assertEquals(1, result);
        verify(datasetMapper).insert(any(ReportDataset.class));
    }

    @Test
    @DisplayName("updateById - 应该更新数据集")
    void updateById_ShouldUpdateDataset() {
        // Arrange
        testDataset.setDatasetName("更新后的数据集");
        testDataset.setDatasetDesc("更新后的描述");
        testDataset.setSqlStatement("SELECT * FROM orders");

        when(datasetMapper.updateById(any(ReportDataset.class))).thenReturn(1);

        // Act
        int result = datasetMapper.updateById(testDataset);

        // Assert
        assertEquals(1, result);
        ArgumentCaptor<ReportDataset> captor = ArgumentCaptor.forClass(ReportDataset.class);
        verify(datasetMapper).updateById(captor.capture());
        assertEquals("更新后的数据集", captor.getValue().getDatasetName());
        assertEquals("SELECT * FROM orders", captor.getValue().getSqlStatement());
    }

    @Test
    @DisplayName("deleteById - 应该删除数据集")
    void deleteById_ShouldDeleteDataset() {
        // Arrange
        when(datasetMapper.deleteById(1L)).thenReturn(1);

        // Act
        int result = datasetMapper.deleteById(1L);

        // Assert
        assertEquals(1, result);
        verify(datasetMapper).deleteById(1L);
    }

    @Test
    @DisplayName("selectList - 使用Wrapper查询应该返回数据集列表")
    void selectList_WithWrapper_ShouldReturnDatasets() {
        // Arrange
        List<ReportDataset> datasets = List.of(testDataset);
        when(datasetMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasets);

        // Act
        LambdaQueryWrapper<ReportDataset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportDataset::getDelFlag, 0);
        List<ReportDataset> result = datasetMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(datasetMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectList - 按名称模糊查询应该返回匹配的数据集")
    void selectList_WithNameLike_ShouldReturnMatchedDatasets() {
        // Arrange
        List<ReportDataset> datasets = List.of(testDataset);
        when(datasetMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasets);

        // Act
        LambdaQueryWrapper<ReportDataset> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ReportDataset::getDatasetName, "测试");
        wrapper.eq(ReportDataset::getDelFlag, 0);
        List<ReportDataset> result = datasetMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(datasetMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectList - 按数据集代码查询应该返回匹配的数据集")
    void selectList_WithDatasetCode_ShouldReturnMatchedDatasets() {
        // Arrange
        List<ReportDataset> datasets = List.of(testDataset);
        when(datasetMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasets);

        // Act
        LambdaQueryWrapper<ReportDataset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportDataset::getDatasetCode, "TEST_DATASET");
        wrapper.eq(ReportDataset::getDelFlag, 0);
        List<ReportDataset> result = datasetMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TEST_DATASET", result.get(0).getDatasetCode());
    }

    @Test
    @DisplayName("selectList - 按数据源ID查询应该返回匹配的数据集")
    void selectList_WithDatasourceId_ShouldReturnMatchedDatasets() {
        // Arrange
        List<ReportDataset> datasets = List.of(testDataset);
        when(datasetMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasets);

        // Act
        LambdaQueryWrapper<ReportDataset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportDataset::getDatasourceId, 100L);
        wrapper.eq(ReportDataset::getDelFlag, 0);
        List<ReportDataset> result = datasetMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getDatasourceId());
    }

    @Test
    @DisplayName("selectList - 按数据集类型查询应该返回匹配的数据集")
    void selectList_WithDatasetType_ShouldReturnMatchedDatasets() {
        // Arrange
        List<ReportDataset> datasets = List.of(testDataset);
        when(datasetMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasets);

        // Act
        LambdaQueryWrapper<ReportDataset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportDataset::getDatasetType, "jdbc");
        wrapper.eq(ReportDataset::getDelFlag, 0);
        List<ReportDataset> result = datasetMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("jdbc", result.get(0).getDatasetType());
    }

    @Test
    @DisplayName("selectList - 按状态查询应该返回匹配的数据集")
    void selectList_WithStatus_ShouldReturnMatchedDatasets() {
        // Arrange
        List<ReportDataset> datasets = List.of(testDataset);
        when(datasetMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasets);

        // Act
        LambdaQueryWrapper<ReportDataset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportDataset::getStatus, 1);
        wrapper.eq(ReportDataset::getDelFlag, 0);
        List<ReportDataset> result = datasetMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getStatus());
    }

    @Test
    @DisplayName("selectList - 查询启用的数据集应该返回status=1且delFlag=0的数据集")
    void selectList_EnabledDatasets_ShouldReturnActiveOnes() {
        // Arrange
        List<ReportDataset> datasets = List.of(testDataset);
        when(datasetMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasets);

        // Act
        LambdaQueryWrapper<ReportDataset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportDataset::getDelFlag, 0)
               .eq(ReportDataset::getStatus, 1)
               .orderByDesc(ReportDataset::getCreateTime);
        List<ReportDataset> result = datasetMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getDelFlag());
        assertEquals(1, result.get(0).getStatus());
    }

    @Test
    @DisplayName("selectList - 空Wrapper应该返回所有数据集")
    void selectList_WithEmptyWrapper_ShouldReturnAllDatasets() {
        // Arrange
        List<ReportDataset> datasets = List.of(testDataset);
        when(datasetMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasets);

        // Act
        LambdaQueryWrapper<ReportDataset> wrapper = new LambdaQueryWrapper<>();
        List<ReportDataset> result = datasetMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("selectList - 多条件组合查询应该正确筛选")
    void selectList_WithMultipleConditions_ShouldFilterCorrectly() {
        // Arrange
        List<ReportDataset> datasets = List.of(testDataset);
        when(datasetMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(datasets);

        // Act
        LambdaQueryWrapper<ReportDataset> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ReportDataset::getDatasetName, "测试")
               .eq(ReportDataset::getDatasetType, "jdbc")
               .eq(ReportDataset::getStatus, 1)
               .eq(ReportDataset::getDelFlag, 0)
               .orderByDesc(ReportDataset::getCreateTime);
        List<ReportDataset> result = datasetMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(datasetMapper).selectList(any(LambdaQueryWrapper.class));
    }
}