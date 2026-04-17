package com.xreport.mapper.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.pojo.entity.ReportTplSheet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ReportTplSheetMapper 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ReportTplSheetMapperTest {

    @Mock
    private ReportTplSheetMapper sheetMapper;

    private ReportTplSheet testSheet;

    @BeforeEach
    void setUp() {
        testSheet = new ReportTplSheet();
        testSheet.setId(1L);
        testSheet.setTplId(100L);
        testSheet.setSheetName("Sheet1");
        testSheet.setSheetOrder(0);
        testSheet.setIsLoop(0);
        testSheet.setLoopSettings(null);
        testSheet.setDatasourceId(10L);
        testSheet.setSqlStr("SELECT * FROM users");
        testSheet.setDelFlag(0);
        testSheet.setTenantId(1L);
        testSheet.setCreateTime(LocalDateTime.now());
        testSheet.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("selectById - 应该返回Sheet")
    void selectById_ShouldReturnSheet() {
        // Arrange
        when(sheetMapper.selectById(1L)).thenReturn(testSheet);

        // Act
        ReportTplSheet result = sheetMapper.selectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Sheet1", result.getSheetName());
        verify(sheetMapper).selectById(1L);
    }

    @Test
    @DisplayName("selectById - Sheet不存在应返回null")
    void selectById_WhenNotExists_ShouldReturnNull() {
        // Arrange
        when(sheetMapper.selectById(999L)).thenReturn(null);

        // Act
        ReportTplSheet result = sheetMapper.selectById(999L);

        // Assert
        assertNull(result);
        verify(sheetMapper).selectById(999L);
    }

    @Test
    @DisplayName("insert - 应该插入Sheet并返回影响行数")
    void insert_ShouldInsertAndReturnAffectedRows() {
        // Arrange
        ReportTplSheet newSheet = new ReportTplSheet();
        newSheet.setTplId(100L);
        newSheet.setSheetName("NewSheet");

        when(sheetMapper.insert(any(ReportTplSheet.class))).thenReturn(1);

        // Act
        int result = sheetMapper.insert(newSheet);

        // Assert
        assertEquals(1, result);
        verify(sheetMapper).insert(any(ReportTplSheet.class));
    }

    @Test
    @DisplayName("updateById - 应该更新Sheet")
    void updateById_ShouldUpdateSheet() {
        // Arrange
        testSheet.setSheetName("UpdatedSheet");

        when(sheetMapper.updateById(any(ReportTplSheet.class))).thenReturn(1);

        // Act
        int result = sheetMapper.updateById(testSheet);

        // Assert
        assertEquals(1, result);
        verify(sheetMapper).updateById(testSheet);
    }

    @Test
    @DisplayName("deleteById - 应该删除Sheet")
    void deleteById_ShouldDeleteSheet() {
        // Arrange
        when(sheetMapper.deleteById(1L)).thenReturn(1);

        // Act
        int result = sheetMapper.deleteById(1L);

        // Assert
        assertEquals(1, result);
        verify(sheetMapper).deleteById(1L);
    }

    @Test
    @DisplayName("selectList - 按模板ID查询应该返回Sheet列表")
    void selectList_ByTplId_ShouldReturnSheets() {
        // Arrange
        List<ReportTplSheet> sheets = List.of(testSheet);
        when(sheetMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(sheets);

        // Act
        LambdaQueryWrapper<ReportTplSheet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportTplSheet::getTplId, 100L).eq(ReportTplSheet::getDelFlag, 0);
        List<ReportTplSheet> result = sheetMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sheetMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectList - 按模板ID和删除标志查询应该返回Sheet列表")
    void selectList_ByTplIdAndDelFlag_ShouldReturnSheets() {
        // Arrange
        List<ReportTplSheet> sheets = List.of(testSheet);
        when(sheetMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(sheets);

        // Act
        LambdaQueryWrapper<ReportTplSheet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportTplSheet::getTplId, 100L)
               .eq(ReportTplSheet::getDelFlag, 0)
               .orderByAsc(ReportTplSheet::getSheetOrder);
        List<ReportTplSheet> result = sheetMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}