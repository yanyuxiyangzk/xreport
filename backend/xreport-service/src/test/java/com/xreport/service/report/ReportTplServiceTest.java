package com.xreport.service.report;

import com.github.pagehelper.PageInfo;
import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.report.LuckysheetReportCellMapper;
import com.xreport.mapper.report.ReportTplMapper;
import com.xreport.mapper.report.ReportTplSheetMapper;
import com.xreport.pojo.dto.FullTemplateResponse;
import com.xreport.pojo.dto.FullTemplateSaveRequest;
import com.xreport.pojo.dto.ReportTplDto;
import com.xreport.pojo.entity.LuckysheetReportCell;
import com.xreport.pojo.entity.ReportTpl;
import com.xreport.pojo.entity.ReportTplSheet;
import com.xreport.service.report.impl.ReportTplServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ReportTplService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ReportTplServiceTest {

    @Mock
    private ReportTplMapper tplMapper;

    @Mock
    private ReportTplSheetMapper sheetMapper;

    @Mock
    private LuckysheetReportCellMapper cellMapper;

    @Mock
    private IReportTplSheetService sheetService;

    @Mock
    private ILuckysheetReportCellService cellService;

    private ReportTplServiceImpl tplService;

    private ReportTpl testTpl;

    @BeforeEach
    void setUp() {
        tplService = new ReportTplServiceImpl(tplMapper, sheetMapper, cellMapper, sheetService, cellService);

        testTpl = new ReportTpl();
        testTpl.setId(1L);
        testTpl.setTplName("测试报表");
        testTpl.setTplType(1);
        testTpl.setIsExample(0);
        testTpl.setSearchFormType(1);
        testTpl.setCdnHost("http://cdn.example.com");
        testTpl.setWaterMarkType(0);
        testTpl.setStatus(1);
        testTpl.setDelFlag(0);
        testTpl.setTenantId(1L);
        testTpl.setCreateTime(LocalDateTime.now());
        testTpl.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("分页查询报表模板列表")
    void pageQuery_ShouldReturnPagedTemplates() {
        // Arrange
        ReportTplDto dto = new ReportTplDto();
        dto.setPageNum(1);
        dto.setPageSize(10);

        List<ReportTpl> templates = Arrays.asList(testTpl);
        when(tplMapper.selectList(any())).thenReturn(templates);

        // Act
        PageInfo<ReportTpl> result = tplService.pageQuery(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals("测试报表", result.getList().get(0).getTplName());
        verify(tplMapper).selectList(any());
    }

    @Test
    @DisplayName("分页查询报表模板列表 - 按名称筛选")
    void pageQuery_WithNameFilter_ShouldReturnFilteredTemplates() {
        // Arrange
        ReportTplDto dto = new ReportTplDto();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setTplName("测试");

        List<ReportTpl> templates = Arrays.asList(testTpl);
        when(tplMapper.selectList(any())).thenReturn(templates);

        // Act
        PageInfo<ReportTpl> result = tplService.pageQuery(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSize());
        verify(tplMapper).selectList(any());
    }

    @Test
    @DisplayName("获取报表模板详情 - 成功")
    void getById_WhenTemplateExists_ShouldReturnTemplate() {
        // Arrange
        when(tplMapper.selectById(1L)).thenReturn(testTpl);

        // Act
        ReportTpl result = tplService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试报表", result.getTplName());
        verify(tplMapper).selectById(1L);
    }

    @Test
    @DisplayName("获取报表模板详情 - 模板不存在")
    void getById_WhenTemplateNotExists_ShouldThrowException() {
        // Arrange
        when(tplMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tplService.getById(999L);
        });

        assertEquals("报表模板不存在", exception.getMessage());
    }

    @Test
    @DisplayName("获取报表模板详情 - 模板已删除")
    void getById_WhenTemplateDeleted_ShouldThrowException() {
        // Arrange
        testTpl.setDelFlag(1);
        when(tplMapper.selectById(1L)).thenReturn(testTpl);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tplService.getById(1L);
        });

        assertEquals("报表模板不存在", exception.getMessage());
    }

    @Test
    @DisplayName("新增报表模板")
    void add_ShouldInsertTemplate() {
        // Arrange
        ReportTpl newTpl = new ReportTpl();
        newTpl.setTplName("新报表");
        newTpl.setTplType(1);

        try (MockedStatic<com.xreport.common.util.IdWorker> idWorkerMock =
                     mockStatic(com.xreport.common.util.IdWorker.class)) {
            idWorkerMock.when(com.xreport.common.util.IdWorker::nextId).thenReturn(100L);

            // Act
            tplService.add(newTpl);

            // Assert
            assertEquals(100L, newTpl.getId());
            assertEquals(0, newTpl.getDelFlag());
            assertEquals(1, newTpl.getStatus());
            assertEquals(1L, newTpl.getTenantId());
            verify(tplMapper).insert(newTpl);
        }
    }

    @Test
    @DisplayName("更新报表模板")
    void update_ShouldUpdateTemplateFields() {
        // Arrange
        ReportTpl updateTpl = new ReportTpl();
        updateTpl.setId(1L);
        updateTpl.setTplName("更新后的报表");
        updateTpl.setTplType(2);
        updateTpl.setSearchFormType(2);
        updateTpl.setCdnHost("http://newcdn.example.com");
        updateTpl.setWaterMarkType(1);
        updateTpl.setThumbnail("new_thumbnail.png");

        when(tplMapper.selectById(1L)).thenReturn(testTpl);

        // Act
        tplService.update(updateTpl);

        // Assert
        assertEquals("更新后的报表", testTpl.getTplName());
        assertEquals(2, testTpl.getTplType());
        assertEquals("http://newcdn.example.com", testTpl.getCdnHost());
        verify(tplMapper).updateById(testTpl);
    }

    @Test
    @DisplayName("删除报表模板 - 软删除")
    void delete_ShouldSoftDeleteTemplate() {
        // Arrange
        when(tplMapper.selectById(1L)).thenReturn(testTpl);

        // Act
        tplService.delete(1L);

        // Assert
        assertEquals(1, testTpl.getDelFlag());
        verify(tplMapper).updateById(testTpl);
    }

    @Test
    @DisplayName("更新报表模板状态")
    void updateStatus_ShouldUpdateStatus() {
        // Arrange
        when(tplMapper.selectById(1L)).thenReturn(testTpl);

        // Act
        tplService.updateStatus(1L, 2);

        // Assert
        assertEquals(2, testTpl.getStatus());
        verify(tplMapper).updateById(testTpl);
    }

    @Test
    @DisplayName("发布报表模板")
    void publish_ShouldSetStatusToPublished() {
        // Arrange
        when(tplMapper.selectById(1L)).thenReturn(testTpl);

        // Act
        tplService.publish(1L);

        // Assert
        assertEquals(2, testTpl.getStatus());
        verify(tplMapper).updateById(testTpl);
    }
}