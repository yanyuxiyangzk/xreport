package com.xreport.service.dataset;

import com.github.pagehelper.PageInfo;
import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.report.ReportDatasetMapper;
import com.xreport.pojo.dto.DatasetDTO;
import com.xreport.pojo.entity.reportdataset.ReportDataset;
import com.xreport.pojo.entity.reportdatasource.ReportDatasource;
import com.xreport.service.dataset.impl.DatasetServiceImpl;
import com.xreport.service.report.IReportDatasourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DatasetService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class DatasetServiceTest {

    @Mock
    private ReportDatasetMapper datasetMapper;

    @Mock
    private IReportDatasourceService datasourceService;

    @Mock
    private IDatasetQueryEngine queryEngine;

    private DatasetServiceImpl datasetService;

    private ReportDataset testDataset;

    private ReportDatasource testDatasource;

    @BeforeEach
    void setUp() {
        datasetService = new DatasetServiceImpl(datasetMapper, datasourceService, queryEngine);

        testDataset = new ReportDataset();
        testDataset.setId(1L);
        testDataset.setDatasetName("测试数据集");
        testDataset.setDatasetCode("TEST_DATASET");
        testDataset.setDatasetType("jdbc");
        testDataset.setDatasourceId(100L);
        testDataset.setStatus(1);
        testDataset.setDelFlag(0);
        testDataset.setTenantId(1L);
        testDataset.setCreateTime(LocalDateTime.now());
        testDataset.setUpdateTime(LocalDateTime.now());

        testDatasource = new ReportDatasource();
        testDatasource.setId(100L);
        testDatasource.setDatasourceName("测试数据源");
        testDatasource.setDatasourceType("jdbc");
        testDatasource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        testDatasource.setUsername("root");
        testDatasource.setPassword("password");
        testDatasource.setStatus(1);
        testDatasource.setDelFlag(0);
    }

    @Test
    @DisplayName("分页查询数据集")
    void pageQuery_ShouldReturnPagedDatasets() {
        // Arrange
        DatasetDTO dto = new DatasetDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);

        List<ReportDataset> datasets = Arrays.asList(testDataset);
        when(datasetMapper.selectList(any())).thenReturn(datasets);

        // Act
        PageInfo<ReportDataset> result = datasetService.pageQuery(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals("测试数据集", result.getList().get(0).getDatasetName());
        verify(datasetMapper).selectList(any());
    }

    @Test
    @DisplayName("分页查询数据集 - 按名称筛选")
    void pageQuery_WithNameFilter_ShouldReturnFilteredDatasets() {
        // Arrange
        DatasetDTO dto = new DatasetDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setDatasetName("测试");

        List<ReportDataset> datasets = Arrays.asList(testDataset);
        when(datasetMapper.selectList(any())).thenReturn(datasets);

        // Act
        PageInfo<ReportDataset> result = datasetService.pageQuery(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSize());
        verify(datasetMapper).selectList(any());
    }

    @Test
    @DisplayName("分页查询数据集 - 按数据源ID筛选")
    void pageQuery_WithDatasourceId_ShouldReturnFilteredDatasets() {
        // Arrange
        DatasetDTO dto = new DatasetDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setDatasourceId(100L);

        List<ReportDataset> datasets = Arrays.asList(testDataset);
        when(datasetMapper.selectList(any())).thenReturn(datasets);

        // Act
        PageInfo<ReportDataset> result = datasetService.pageQuery(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSize());
        verify(datasetMapper).selectList(any());
    }

    @Test
    @DisplayName("获取数据集详情 - 成功")
    void getById_WhenDatasetExists_ShouldReturnDataset() {
        // Arrange
        when(datasetMapper.selectById(1L)).thenReturn(testDataset);

        // Act
        ReportDataset result = datasetService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试数据集", result.getDatasetName());
        verify(datasetMapper).selectById(1L);
    }

    @Test
    @DisplayName("获取数据集详情 - 数据集不存在")
    void getById_WhenDatasetNotExists_ShouldThrowException() {
        // Arrange
        when(datasetMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            datasetService.getById(999L);
        });

        assertEquals("数据集不存在", exception.getMessage());
    }

    @Test
    @DisplayName("获取数据集详情 - 数据集已删除")
    void getById_WhenDatasetDeleted_ShouldThrowException() {
        // Arrange
        testDataset.setDelFlag(1);
        when(datasetMapper.selectById(1L)).thenReturn(testDataset);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            datasetService.getById(1L);
        });

        assertEquals("数据集不存在", exception.getMessage());
    }

    @Test
    @DisplayName("新增数据集")
    void add_ShouldInsertDataset() {
        // Arrange
        ReportDataset newDataset = new ReportDataset();
        newDataset.setDatasetName("新数据集");
        newDataset.setDatasetType("jdbc");
        newDataset.setDatasourceId(100L);

        try (MockedStatic<com.xreport.common.util.IdWorker> idWorkerMock =
                     mockStatic(com.xreport.common.util.IdWorker.class)) {
            idWorkerMock.when(com.xreport.common.util.IdWorker::nextId).thenReturn(100L);

            // Act
            datasetService.add(newDataset);

            // Assert
            assertEquals(100L, newDataset.getId());
            assertEquals(0, newDataset.getDelFlag());
            assertEquals(1, newDataset.getStatus());
            assertEquals(1L, newDataset.getTenantId());
            verify(datasetMapper).insert(newDataset);
        }
    }

    @Test
    @DisplayName("更新数据集")
    void update_ShouldUpdateDatasetFields() {
        // Arrange
        ReportDataset updateDataset = new ReportDataset();
        updateDataset.setId(1L);
        updateDataset.setDatasetName("更新后的数据集");
        updateDataset.setDatasetCode("UPDATED_CODE");

        when(datasetMapper.selectById(1L)).thenReturn(testDataset);

        // Act
        datasetService.update(updateDataset);

        // Assert
        assertEquals("更新后的数据集", testDataset.getDatasetName());
        assertEquals("UPDATED_CODE", testDataset.getDatasetCode());
        verify(datasetMapper).updateById(testDataset);
    }

    @Test
    @DisplayName("删除数据集 - 软删除")
    void delete_ShouldSetDelFlagToOne() {
        // Arrange
        when(datasetMapper.selectById(1L)).thenReturn(testDataset);

        // Act
        datasetService.delete(1L);

        // Assert
        assertEquals(1, testDataset.getDelFlag());
        verify(datasetMapper).updateById(testDataset);
    }

    @Test
    @DisplayName("获取启用的数据集列表")
    void listEnabled_ShouldReturnActiveDatasets() {
        // Arrange
        List<ReportDataset> datasets = Arrays.asList(testDataset);
        when(datasetMapper.selectList(any())).thenReturn(datasets);

        // Act
        List<ReportDataset> result = datasetService.listEnabled();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(datasetMapper).selectList(any());
    }

    @Test
    @DisplayName("执行数据集查询")
    void executeQuery_ShouldReturnQueryResults() {
        // Arrange
        Map<String, Object> params = new HashMap<>();
        params.put("category", "test");

        List<Map<String, Object>> expectedResults = Arrays.asList(
                Map.of("id", 1, "name", "item1"),
                Map.of("id", 2, "name", "item2")
        );

        when(datasetMapper.selectById(1L)).thenReturn(testDataset);
        when(datasourceService.getById(100L)).thenReturn(testDatasource);
        when(queryEngine.execute(any(), any(), any())).thenReturn(expectedResults);

        // Act
        List<Map<String, Object>> result = datasetService.executeQuery(1L, params);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(datasetMapper).selectById(1L);
        verify(datasourceService).getById(100L);
        verify(queryEngine).execute(any(), any(), any());
    }

    @Test
    @DisplayName("预览数据集查询结果 - 应限制100条")
    void previewQuery_ShouldAddLimitParameter() {
        // Arrange
        Map<String, Object> params = new HashMap<>();
        params.put("category", "test");

        List<Map<String, Object>> expectedResults = Arrays.asList(
                Map.of("id", 1, "name", "item1")
        );

        when(datasetMapper.selectById(1L)).thenReturn(testDataset);
        when(datasourceService.getById(100L)).thenReturn(testDatasource);
        when(queryEngine.execute(any(), any(), any())).thenReturn(expectedResults);

        // Act
        List<Map<String, Object>> result = datasetService.previewQuery(1L, params);

        // Assert
        assertNotNull(result);
        verify(datasetMapper).selectById(1L);
        verify(datasourceService).getById(100L);
        // 验证queryEngine被调用，且params中包含limit=100
        verify(queryEngine).execute(any(), any(), argThat(map ->
                map.containsKey("limit") && map.get("limit").equals(100)
        ));
    }

    @Test
    @DisplayName("预览数据集查询结果 - null参数处理")
    void previewQuery_WithNullParams_ShouldHandleGracefully() {
        // Arrange
        List<Map<String, Object>> expectedResults = Arrays.asList(
                Map.of("id", 1, "name", "item1")
        );

        when(datasetMapper.selectById(1L)).thenReturn(testDataset);
        when(datasourceService.getById(100L)).thenReturn(testDatasource);
        when(queryEngine.execute(any(), any(), any())).thenReturn(expectedResults);

        // Act
        List<Map<String, Object>> result = datasetService.previewQuery(1L, null);

        // Assert
        assertNotNull(result);
        verify(queryEngine).execute(any(), any(), argThat(map ->
                map.containsKey("limit") && map.get("limit").equals(100)
        ));
    }
}