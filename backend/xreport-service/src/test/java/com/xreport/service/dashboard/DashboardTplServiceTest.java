package com.xreport.service.dashboard;

import com.github.pagehelper.PageInfo;
import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.dashboard.DashboardTplMapper;
import com.xreport.pojo.dto.DashboardTplDto;
import com.xreport.pojo.entity.dashboard.DashboardTpl;
import com.xreport.service.dashboard.impl.DashboardTplServiceImpl;
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
 * DashboardTplService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class DashboardTplServiceTest {

    @Mock
    private DashboardTplMapper tplMapper;

    private DashboardTplServiceImpl dashboardTplService;

    private DashboardTpl testTpl;

    @BeforeEach
    void setUp() {
        dashboardTplService = new DashboardTplServiceImpl(tplMapper);

        testTpl = new DashboardTpl();
        testTpl.setId(1L);
        testTpl.setTplName("销售大屏");
        testTpl.setTplCode("SALES_DASHBOARD");
        testTpl.setDescription("销售数据可视化大屏");
        testTpl.setConfig("{\"theme\":\"dark\"}");
        testTpl.setPreviewUrl("/preview/1.png");
        testTpl.setStatus(1);
        testTpl.setCreateTime(LocalDateTime.now());
        testTpl.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("分页查询大屏模板列表")
    void pageQuery_ShouldReturnPagedTemplates() {
        // Arrange
        DashboardTplDto dto = new DashboardTplDto();
        dto.setPageNum(1);
        dto.setPageSize(10);

        List<DashboardTpl> templates = Arrays.asList(testTpl);
        when(tplMapper.selectList(any())).thenReturn(templates);

        // Act
        PageInfo<DashboardTpl> result = dashboardTplService.pageQuery(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals("销售大屏", result.getList().get(0).getTplName());
        verify(tplMapper).selectList(any());
    }

    @Test
    @DisplayName("分页查询大屏模板列表 - 按名称筛选")
    void pageQuery_WithNameFilter_ShouldReturnFilteredTemplates() {
        // Arrange
        DashboardTplDto dto = new DashboardTplDto();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setTplName("销售");

        List<DashboardTpl> templates = Arrays.asList(testTpl);
        when(tplMapper.selectList(any())).thenReturn(templates);

        // Act
        PageInfo<DashboardTpl> result = dashboardTplService.pageQuery(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSize());
        verify(tplMapper).selectList(any());
    }

    @Test
    @DisplayName("分页查询大屏模板列表 - 按编码筛选")
    void pageQuery_WithCodeFilter_ShouldReturnFilteredTemplates() {
        // Arrange
        DashboardTplDto dto = new DashboardTplDto();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setTplCode("SALES_DASHBOARD");

        List<DashboardTpl> templates = Arrays.asList(testTpl);
        when(tplMapper.selectList(any())).thenReturn(templates);

        // Act
        PageInfo<DashboardTpl> result = dashboardTplService.pageQuery(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSize());
    }

    @Test
    @DisplayName("分页查询大屏模板列表 - 按状态筛选")
    void pageQuery_WithStatusFilter_ShouldReturnFilteredTemplates() {
        // Arrange
        DashboardTplDto dto = new DashboardTplDto();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setStatus(1);

        List<DashboardTpl> templates = Arrays.asList(testTpl);
        when(tplMapper.selectList(any())).thenReturn(templates);

        // Act
        PageInfo<DashboardTpl> result = dashboardTplService.pageQuery(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSize());
    }

    @Test
    @DisplayName("获取启用的模板列表")
    void list_ShouldReturnEnabledTemplates() {
        // Arrange
        DashboardTplDto dto = new DashboardTplDto();
        List<DashboardTpl> templates = Arrays.asList(testTpl);
        when(tplMapper.selectList(any())).thenReturn(templates);

        // Act
        List<DashboardTpl> result = dashboardTplService.list(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tplMapper).selectList(any());
    }

    @Test
    @DisplayName("根据ID获取模板 - 成功")
    void getById_WhenTemplateExists_ShouldReturnTemplate() {
        // Arrange
        when(tplMapper.selectById(1L)).thenReturn(testTpl);

        // Act
        DashboardTpl result = dashboardTplService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("销售大屏", result.getTplName());
        verify(tplMapper).selectById(1L);
    }

    @Test
    @DisplayName("根据ID获取模板 - 模板不存在")
    void getById_WhenTemplateNotExists_ShouldThrowException() {
        // Arrange
        when(tplMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            dashboardTplService.getById(999L);
        });

        assertEquals("大屏模板不存在", exception.getMessage());
    }

    @Test
    @DisplayName("根据编码获取模板 - 成功")
    void getByCode_WhenTemplateExists_ShouldReturnTemplate() {
        // Arrange
        when(tplMapper.selectOne(any())).thenReturn(testTpl);

        // Act
        DashboardTpl result = dashboardTplService.getByCode("SALES_DASHBOARD");

        // Assert
        assertNotNull(result);
        assertEquals("SALES_DASHBOARD", result.getTplCode());
        verify(tplMapper).selectOne(any());
    }

    @Test
    @DisplayName("根据编码获取模板 - 模板不存在")
    void getByCode_WhenTemplateNotExists_ShouldThrowException() {
        // Arrange
        when(tplMapper.selectOne(any())).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            dashboardTplService.getByCode("NONEXISTENT");
        });

        assertEquals("大屏模板不存在", exception.getMessage());
    }

    @Test
    @DisplayName("新增大屏模板")
    void add_ShouldInsertTemplate() {
        // Arrange
        DashboardTpl newTpl = new DashboardTpl();
        newTpl.setTplName("新大屏");
        newTpl.setTplCode("NEW_DASHBOARD");
        newTpl.setDescription("新的大屏模板");

        try (MockedStatic<com.xreport.common.util.IdWorker> idWorkerMock =
                     mockStatic(com.xreport.common.util.IdWorker.class)) {
            idWorkerMock.when(com.xreport.common.util.IdWorker::nextId).thenReturn(100L);

            // Act
            dashboardTplService.add(newTpl);

            // Assert
            assertEquals(100L, newTpl.getId());
            assertEquals(1, newTpl.getStatus());
            verify(tplMapper).insert(newTpl);
        }
    }

    @Test
    @DisplayName("更新大屏模板")
    void update_ShouldUpdateTemplateFields() {
        // Arrange
        DashboardTpl updateTpl = new DashboardTpl();
        updateTpl.setId(1L);
        updateTpl.setTplName("更新后的大屏");
        updateTpl.setTplCode("UPDATED_DASHBOARD");
        updateTpl.setDescription("更新后的描述");
        updateTpl.setConfig("{\"theme\":\"light\"}");
        updateTpl.setPreviewUrl("/preview/updated.png");
        updateTpl.setStatus(0);

        when(tplMapper.selectById(1L)).thenReturn(testTpl);

        // Act
        dashboardTplService.update(updateTpl);

        // Assert
        assertEquals("更新后的大屏", testTpl.getTplName());
        assertEquals("UPDATED_DASHBOARD", testTpl.getTplCode());
        assertEquals("{\"theme\":\"light\"}", testTpl.getConfig());
        assertEquals(0, testTpl.getStatus());
        verify(tplMapper).updateById(testTpl);
    }

    @Test
    @DisplayName("删除大屏模板 - 设置status为0")
    void delete_ShouldSetStatusToZero() {
        // Arrange
        when(tplMapper.selectById(1L)).thenReturn(testTpl);

        // Act
        dashboardTplService.delete(1L);

        // Assert
        assertEquals(0, testTpl.getStatus());
        verify(tplMapper).updateById(testTpl);
    }
}