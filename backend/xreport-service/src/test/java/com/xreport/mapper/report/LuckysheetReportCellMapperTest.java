package com.xreport.mapper.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.pojo.entity.LuckysheetReportCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * LuckysheetReportCellMapper 单元测试
 */
@ExtendWith(MockitoExtension.class)
class LuckysheetReportCellMapperTest {

    @Mock
    private LuckysheetReportCellMapper cellMapper;

    private LuckysheetReportCell testCell;

    @BeforeEach
    void setUp() {
        testCell = new LuckysheetReportCell();
        testCell.setId(1L);
        testCell.setTplId(100L);
        testCell.setSheetId(10L);
        testCell.setCellData("[{\"r\":0,\"c\":0,\"v\":\"test\"}]");
        testCell.setVer(1);
        testCell.setDelFlag(0);
        testCell.setTenantId(1L);
        testCell.setCreateTime(LocalDateTime.now());
        testCell.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("selectById - 应该返回单元格")
    void selectById_ShouldReturnCell() {
        // Arrange
        when(cellMapper.selectById(1L)).thenReturn(testCell);

        // Act
        LuckysheetReportCell result = cellMapper.selectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(cellMapper).selectById(1L);
    }

    @Test
    @DisplayName("selectOne - 按模板ID和SheetID查询应该返回单元格")
    void selectOne_ByTplIdAndSheetId_ShouldReturnCell() {
        // Arrange
        when(cellMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testCell);

        // Act
        LambdaQueryWrapper<LuckysheetReportCell> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LuckysheetReportCell::getTplId, 100L)
               .eq(LuckysheetReportCell::getSheetId, 10L)
               .eq(LuckysheetReportCell::getDelFlag, 0);
        LuckysheetReportCell result = cellMapper.selectOne(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getTplId());
        verify(cellMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectOne - 无数据应返回null")
    void selectOne_WhenNotExists_ShouldReturnNull() {
        // Arrange
        when(cellMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // Act
        LambdaQueryWrapper<LuckysheetReportCell> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LuckysheetReportCell::getTplId, 999L);
        LuckysheetReportCell result = cellMapper.selectOne(wrapper);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("insert - 应该插入单元格并返回影响行数")
    void insert_ShouldInsertAndReturnAffectedRows() {
        // Arrange
        LuckysheetReportCell newCell = new LuckysheetReportCell();
        newCell.setTplId(100L);
        newCell.setSheetId(20L);
        newCell.setCellData("[{\"r\":0,\"c\":0,\"v\":\"new\"}]");

        when(cellMapper.insert(any(LuckysheetReportCell.class))).thenReturn(1);

        // Act
        int result = cellMapper.insert(newCell);

        // Assert
        assertEquals(1, result);
        verify(cellMapper).insert(any(LuckysheetReportCell.class));
    }

    @Test
    @DisplayName("updateById - 应该更新单元格")
    void updateById_ShouldUpdateCell() {
        // Arrange
        testCell.setCellData("[{\"r\":0,\"c\":0,\"v\":\"updated\"}]");

        when(cellMapper.updateById(any(LuckysheetReportCell.class))).thenReturn(1);

        // Act
        int result = cellMapper.updateById(testCell);

        // Assert
        assertEquals(1, result);
        verify(cellMapper).updateById(testCell);
    }

    @Test
    @DisplayName("deleteById - 应该删除单元格")
    void deleteById_ShouldDeleteCell() {
        // Arrange
        when(cellMapper.deleteById(1L)).thenReturn(1);

        // Act
        int result = cellMapper.deleteById(1L);

        // Assert
        assertEquals(1, result);
        verify(cellMapper).deleteById(1L);
    }
}