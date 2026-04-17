package com.xreport.mapper.dashboard;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.pojo.entity.dashboard.DashboardTpl;
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
 * DashboardTplMapper 单元测试
 */
@ExtendWith(MockitoExtension.class)
class DashboardTplMapperTest {

    @Mock
    private DashboardTplMapper dashboardTplMapper;

    private DashboardTpl testTpl;

    @BeforeEach
    void setUp() {
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
    @DisplayName("selectById - 应该返回大屏模板")
    void selectById_ShouldReturnTemplate() {
        // Arrange
        when(dashboardTplMapper.selectById(1L)).thenReturn(testTpl);

        // Act
        DashboardTpl result = dashboardTplMapper.selectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("销售大屏", result.getTplName());
        verify(dashboardTplMapper).selectById(1L);
    }

    @Test
    @DisplayName("selectById - 模板不存在应返回null")
    void selectById_WhenNotExists_ShouldReturnNull() {
        // Arrange
        when(dashboardTplMapper.selectById(999L)).thenReturn(null);

        // Act
        DashboardTpl result = dashboardTplMapper.selectById(999L);

        // Assert
        assertNull(result);
        verify(dashboardTplMapper).selectById(999L);
    }

    @Test
    @DisplayName("insert - 应该插入模板并返回影响行数")
    void insert_ShouldInsertAndReturnAffectedRows() {
        // Arrange
        DashboardTpl newTpl = new DashboardTpl();
        newTpl.setTplName("新大屏");
        newTpl.setTplCode("NEW_DASHBOARD");

        when(dashboardTplMapper.insert(any(DashboardTpl.class))).thenReturn(1);

        // Act
        int result = dashboardTplMapper.insert(newTpl);

        // Assert
        assertEquals(1, result);
        verify(dashboardTplMapper).insert(any(DashboardTpl.class));
    }

    @Test
    @DisplayName("updateById - 应该更新模板")
    void updateById_ShouldUpdateTemplate() {
        // Arrange
        testTpl.setTplName("更新后的大屏");

        when(dashboardTplMapper.updateById(any(DashboardTpl.class))).thenReturn(1);

        // Act
        int result = dashboardTplMapper.updateById(testTpl);

        // Assert
        assertEquals(1, result);
        verify(dashboardTplMapper).updateById(testTpl);
    }

    @Test
    @DisplayName("deleteById - 应该删除模板")
    void deleteById_ShouldDeleteTemplate() {
        // Arrange
        when(dashboardTplMapper.deleteById(1L)).thenReturn(1);

        // Act
        int result = dashboardTplMapper.deleteById(1L);

        // Assert
        assertEquals(1, result);
        verify(dashboardTplMapper).deleteById(1L);
    }

    @Test
    @DisplayName("selectOne - 按模板编码查询应该返回模板")
    void selectOne_ByTplCode_ShouldReturnTemplate() {
        // Arrange
        when(dashboardTplMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testTpl);

        // Act
        LambdaQueryWrapper<DashboardTpl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DashboardTpl::getTplCode, "SALES_DASHBOARD");
        DashboardTpl result = dashboardTplMapper.selectOne(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals("SALES_DASHBOARD", result.getTplCode());
        verify(dashboardTplMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectList - 使用Wrapper查询应该返回模板列表")
    void selectList_WithWrapper_ShouldReturnTemplates() {
        // Arrange
        List<DashboardTpl> templates = List.of(testTpl);
        when(dashboardTplMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(templates);

        // Act
        LambdaQueryWrapper<DashboardTpl> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DashboardTpl::getCreateTime);
        List<DashboardTpl> result = dashboardTplMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dashboardTplMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectList - 按名称模糊查询应该返回匹配的模板")
    void selectList_WithNameLike_ShouldReturnMatchedTemplates() {
        // Arrange
        List<DashboardTpl> templates = List.of(testTpl);
        when(dashboardTplMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(templates);

        // Act
        LambdaQueryWrapper<DashboardTpl> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(DashboardTpl::getTplName, "销售");
        List<DashboardTpl> result = dashboardTplMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dashboardTplMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectList - 按状态查询应该返回匹配的模板")
    void selectList_WithStatus_ShouldReturnMatchedTemplates() {
        // Arrange
        List<DashboardTpl> templates = List.of(testTpl);
        when(dashboardTplMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(templates);

        // Act
        LambdaQueryWrapper<DashboardTpl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DashboardTpl::getStatus, 1);
        List<DashboardTpl> result = dashboardTplMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}