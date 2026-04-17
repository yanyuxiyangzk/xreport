package com.xreport.service.report;

import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.report.ReportTplSheetMapper;
import com.xreport.pojo.entity.ReportTplSheet;
import com.xreport.service.report.impl.ReportTplSheetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ReportTplSheetService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ReportTplSheetServiceTest {

    @Mock
    private ReportTplSheetMapper sheetMapper;

    private ReportTplSheetServiceImpl sheetService;

    private ReportTplSheet testSheet;

    @BeforeEach
    void setUp() {
        sheetService = new ReportTplSheetServiceImpl(sheetMapper);

        testSheet = new ReportTplSheet();
        testSheet.setId(1L);
        testSheet.setTplId(100L);
        testSheet.setSheetName("Sheet1");
        testSheet.setSheetOrder(0);
        testSheet.setIsLoop(0);
        testSheet.setLoopSettings(null);
        testSheet.setDatasourceId(10L);
        testSheet.setSqlStr("SELECT * FROM users");
        testSheet.setParams(null);
        testSheet.setSort(null);
        testSheet.setDelFlag(0);
        testSheet.setTenantId(1L);
        testSheet.setCreateTime(LocalDateTime.now());
        testSheet.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("根据模板ID获取Sheet列表")
    void getByTplId_ShouldReturnSheets() {
        // Arrange
        List<ReportTplSheet> sheets = Arrays.asList(testSheet);
        when(sheetMapper.selectList(any())).thenReturn(sheets);

        // Act
        List<ReportTplSheet> result = sheetService.getByTplId(100L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sheet1", result.get(0).getSheetName());
        verify(sheetMapper).selectList(any());
    }

    @Test
    @DisplayName("根据ID获取Sheet - 成功")
    void getById_WhenSheetExists_ShouldReturnSheet() {
        // Arrange
        when(sheetMapper.selectById(1L)).thenReturn(testSheet);

        // Act
        ReportTplSheet result = sheetService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Sheet1", result.getSheetName());
        verify(sheetMapper).selectById(1L);
    }

    @Test
    @DisplayName("根据ID获取Sheet - Sheet不存在")
    void getById_WhenSheetNotExists_ShouldThrowException() {
        // Arrange
        when(sheetMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sheetService.getById(999L);
        });

        assertEquals("Sheet不存在", exception.getMessage());
    }

    @Test
    @DisplayName("根据ID获取Sheet - Sheet已删除")
    void getById_WhenSheetDeleted_ShouldThrowException() {
        // Arrange
        testSheet.setDelFlag(1);
        when(sheetMapper.selectById(1L)).thenReturn(testSheet);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sheetService.getById(1L);
        });

        assertEquals("Sheet不存在", exception.getMessage());
    }

    @Test
    @DisplayName("新增Sheet")
    void add_ShouldInsertSheet() {
        // Arrange
        ReportTplSheet newSheet = new ReportTplSheet();
        newSheet.setTplId(100L);
        newSheet.setSheetName("NewSheet");
        newSheet.setSheetOrder(1);

        try (MockedStatic<com.xreport.common.util.IdWorker> idWorkerMock =
                     mockStatic(com.xreport.common.util.IdWorker.class)) {
            idWorkerMock.when(com.xreport.common.util.IdWorker::nextId).thenReturn(200L);

            // Act
            sheetService.add(newSheet);

            // Assert
            assertEquals(200L, newSheet.getId());
            assertEquals(0, newSheet.getDelFlag());
            assertEquals(1L, newSheet.getTenantId());
            verify(sheetMapper).insert(newSheet);
        }
    }

    @Test
    @DisplayName("更新Sheet")
    void update_ShouldUpdateSheetFields() {
        // Arrange
        ReportTplSheet updateSheet = new ReportTplSheet();
        updateSheet.setId(1L);
        updateSheet.setSheetName("UpdatedSheet");
        updateSheet.setSheetOrder(2);
        updateSheet.setIsLoop(1);
        updateSheet.setLoopSettings("{\"loopField\":\"id\"}");
        updateSheet.setDatasourceId(20L);
        updateSheet.setSqlStr("SELECT * FROM orders");
        updateSheet.setParams("{\"status\":1}");
        updateSheet.setSort(1);

        when(sheetMapper.selectById(1L)).thenReturn(testSheet);

        // Act
        sheetService.update(updateSheet);

        // Assert
        assertEquals("UpdatedSheet", testSheet.getSheetName());
        assertEquals(2, testSheet.getSheetOrder());
        assertEquals(1, testSheet.getIsLoop());
        assertEquals("{\"loopField\":\"id\"}", testSheet.getLoopSettings());
        assertEquals(20L, testSheet.getDatasourceId());
        verify(sheetMapper).updateById(testSheet);
    }

    @Test
    @DisplayName("删除Sheet - 软删除")
    void delete_ShouldSetDelFlagToOne() {
        // Arrange
        when(sheetMapper.selectById(1L)).thenReturn(testSheet);

        // Act
        sheetService.delete(1L);

        // Assert
        assertEquals(1, testSheet.getDelFlag());
        verify(sheetMapper).updateById(testSheet);
    }

    @Test
    @DisplayName("根据模板ID删除所有Sheet - 软删除")
    void deleteByTplId_ShouldSoftDeleteAllSheets() {
        // Arrange
        ReportTplSheet sheet2 = new ReportTplSheet();
        sheet2.setId(2L);
        sheet2.setTplId(100L);
        sheet2.setDelFlag(0);

        when(sheetMapper.selectList(any())).thenReturn(Arrays.asList(testSheet, sheet2));

        // Act
        sheetService.deleteByTplId(100L);

        // Assert
        assertEquals(1, testSheet.getDelFlag());
        assertEquals(1, sheet2.getDelFlag());
        verify(sheetMapper, times(2)).updateById(any(ReportTplSheet.class));
    }

    @Test
    @DisplayName("根据模板ID删除所有Sheet - 无Sheet时不应调用updateById")
    void deleteByTplId_WhenNoSheets_ShouldNotCallUpdate() {
        // Arrange
        when(sheetMapper.selectList(any())).thenReturn(Arrays.asList());

        // Act
        sheetService.deleteByTplId(100L);

        // Assert
        verify(sheetMapper, never()).updateById(any(ReportTplSheet.class));
    }
}